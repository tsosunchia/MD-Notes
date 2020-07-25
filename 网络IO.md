---
typora-copy-images-to: /images/
---



# 网络IO

### 同步模型、异步模型

- 同步模型：程序自己读取，程序在IO上的模型就叫 **同步模型**
- 异步模型：程序把读取的过程交给内核，自己做自己的事情，叫 **异步模型**

允许程序去调用内核，来监控更多的客户端，直到一个或多个文件描述符是可用的状态，再返回。减少了用户态、内核态的无用切换。



### BIO

- 阻塞式的 `ServerSocket`
  - 阻塞地等待客户端的连接，连接后抛出一个线程
  - 阻塞地等待客户端发送消息
  - C10K 问题，如果有1万个连接，就要抛出1万个线程
  
  

### NIO

Java：New IO 新的IO包

OS：Non-Blocking IO 非阻塞的IO

- 非阻塞式的 `ServerSocketChannel`
  - `ss.configBlocking(false)` 设置非阻塞
  - 在`accept(3,`中空转，要么返回client的描述符，要么返回-1，如果拿到了新的连接，调用clientList.add()
- 缺点
  - `ByteBuffer`只有一个指针，用起来有很多坑
  - NIO的存在的问题：C10K问题

    - 放大：C10K当并发量很大的时候，需要遍历的文件描述符会很多，**每循环 **一次，**都要调用 10K 次recv 系统调用**（复杂度O(n)），进行用户态到内核态的切换，性能损耗大。
    - 缩小：你调用了 **那么多次 recv 系统调用**，但是如果 C10K 只有 1 个 Client 发来了数据，**只有 1 次系统调用是有效的**，剩余 n-1 次的监听都是无效的。我们希望，能够有一种方式，只需要调用 **有效** 的 **recv 系统调用** 就可以。
    
    

### 多路复用

OS 提供的**多路复用器**有 `select`, `pool`, `epoll`, `kqueue` 等

select，poll 的缺点是，每次你调用的时候，都需要将所有的 fds （文件描述符的集合）作为参数传递给函数，而 epoll 的优势是在内核中开辟了一个空间（调用的是 epoll_create）

 Java 把所有的多路复用器封装成了 **Selector 类**

可以在启动时，指定使用哪种多路复用器（默认优先选择 epoll），注意 windows 上是没有 epoll 的。

```java
-Djava.nio.channels.spi.SelectorProvider=sun.nio.ch.EPollSelectorProvider
```

- Linux内核提供的 `select` 多路复用器，会返回给程序一个 list，告诉程序 **哪些 fd 是可以读/写的状态**，然后**程序要自己读取**，这是 IO 同步模型。

  只有当 read 系统调用不需要你程序自己去做的时候，才属于异步的 IO，目前只有 windows iocp 实现了。

- 缺点
  
  - 如果有很多长连接，内核每次都要给程序传递很多连接对象



### epoll

- epoll 也是多路复用器，但是它有一个 **存放结果集的链表**，**它与 select / poll 的区别如下：**

  - select：应用程什么时候调用 select，内核就什么时候遍历所有的文件描述符，修正 fd 的状态。

  - epoll：应用程序在内核的 **红黑树** 中存放过一些fd，那么，内核基于中断处理完 fd 的 buffer/状态 之后，继续把有状态的 fd 拷贝到链表中。

    即便应用程序不调用内核，内核也会随着中断，完成所有fd状态的设置。这样，程序调用`epoll_wait`可以及时去取链表包含中有状态的 fd 的结果集。规避了对于文件描述符的全量遍历。

    拿到 fd 的结果集之后，**程序需要自己取处理 accept / recv 等系统调用的过程**。所以`epoll_wait`依然是同步模型。

- 它不负责读取 IO，只关心返回结果

- Epoll是Event poll，把有数据这个事件通知给程序，还需要程序自己取读取数据

- `man epoll` 帮助文档：

  The `epoll` API performs a similar task to `poll`(2): monitoring multiple file descriptors to see if I/O is possible on any of them.  The `epoll` API can be used either as an edge-triggered(边缘触发) or a  level-triggered(条件触发)  interface and scales well to large numbers of watched file descriptors(可以很好地扩展到大量监视文件描述符).  The following system calls are provided to create and manage an epoll instance:  [ 注：这里 (2) 的意思是 2 类系统调用。类似地，还有 7 类杂项]

  - `epoll_create` creates an epoll instance  and  returns  a  file  descriptor  referring  to  that instance.

    创建成功之后，返回一个 fd 文件描述符例如`fd6`。在内核开辟一块空间，里面存放红黑树。

  - `epoll_ctl`, This  system  call  performs  control  operations  on  the `epoll`(7) instance referred to by the file descriptor epfd.  It requests that the operation op be performed for the target file descriptor, fd.

    ```c
    int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
    ```

    例如，在文件描述符`fd6`中，使用 `EPOLL_CTL_ADD` 添加服务器用于 listen 的文件描述符

    - `int op` 可选参数：`EPOLL_CTL_ADD`, `EPOLL_CTL_MOD`, `EPOLL_CTL_DEL`

  - `epoll_wait` waits for I/O events, blocking the calling thread if no events are currently available.

    **epoll_wait 不传递 fds，不触发内核遍历。**你的程序需要写一个死循环，一直调用 epoll_wait，可以设置阻塞，或者非阻塞

- 早期在没有上述三个系统调用的时候，需要应用程序调用 mmap 来实现两端的内存共享提速，后期在 2.6 内核版本之后，提供了这些系统调用，就不需要 mmap 这种实现方式了

##### 边沿触发

只要缓冲区还有东西可以读，只要你调用了epoll_wait函数，它就会继续通知你

##### 水平触发

就像高低电平一样，只有从高电平到低电平或者低电平到高电平时才会通知我们。只有客户端再次向服务器端发送数据时，epoll_wait 才会再返回给你



### AIO

- AIO 是异步的模型
- 使用的是 callback / hook / templateMethod 回调，是基于事件模型的 IO
- Netty封装的是NIO，不是AIO
  - AIO 只有 Window 支持（内核中使用CompletionPort完成端口）
  - 在 Linux 上的 AIO 只不过是对 NIO 的封装而已（是基于epoll 的轮询）

“停止使用这种只适用于特殊情况的垃圾，让所有人都在乎的系统核心尽其所能地运行好其基本的性能”，就是说，你cpu做你cpu该做的事，别净整些没用的。。

就像系统调用那样，尽管Linux上建立一个新的系统调用非常容易，但并不提倡每出现一种新的抽象就简单的加入一个新的系统调用。这使得它的系统调用接口简洁得令人叹为观止（2.6版本338个），新系统调用增加频率很低也反映出它是一个相对较稳定并且功能已经较为完善的操作系统。

这也是为什么以前Linux版本的主内核树中没有类似于windows 上 AIO这样的通用的内核异步IO处理方案（在Linux 上的AIO只不过是对 NIO 的封装而已），因为不安全，会让内核做的事情太多，容易出bug。windows敢于这么做，是因为它的市场比较广，一方面是用户市场，一方面是服务器市场，况且windows比较注重用户市场，所以敢于把内核做的胖一些，也是因此虽然现在已经win10了，但是蓝屏啊，死机啊，挂机啊这些问题也还是会出现。





### Netty

Netty主要用于网络通信。

- 很多网页游戏的服务器都是用Netty写的。
- Tomcat，Zookeeper，很多开源分布式的底层也是netty写的。



![epoll](images/image-20200718112457843.png)





### Java 中对于多路复用的封装

```java
package com.bjmashibing.system.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class SocketMultiplexingSingleThreadv1 {

    //坦克一、二期（netty）
    private ServerSocketChannel server = null;
    private Selector selector = null;   //linux 提供多路复用器（select poll epoll kqueue）等，在java中被封装为Selector。拓展：nginx的event模块
    int port = 9090;

    public void initServer() {
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false); // 设置成非阻塞
            server.bind(new InetSocketAddress(port));  // 绑定监听的端口号

            //如果在epoll模型下，Selector.open()其实完成了epoll_create，可能给你返回了一个 fd3
            selector = Selector.open();  // 可以选择 select  poll  *epoll，在linux中会优先选择epoll  但是可以在JVM使用-D参数修正
            /*
                server 约等于 listen 状态的 fd4
                server.register()初始化过程：
                1、如果在select，poll的模型下，Selector.open()操作实际上是在jvm里开辟一个数组，把fd4放进去
                2、如果在epoll的模型下，调用了epoll_ctl(fd3,ADD,fd4,EPOLLIN)
             */
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        initServer();
        System.out.println("服务器启动了");
        try {
            while (true) {  //死循环
                Set<SelectionKey> keys = selector.keys();
                System.out.println("keys.size() = " + keys.size());
                //1、selector.select调用多路复用器(分为select,poll or epoll(实质上是调用的epoll_wait))
                /*
                    java中调用的select()方法是啥意思：
                        1、如果用select，poll模型，其实调的是内核的select方法，并传入参数（fd4），或者poll(fd4)
                        2、如果用epoll模型，其实调用的是内核的epoll_wait()，因为fd4在上面的epoll_ctl已经传进去了
                    注意：
                        参数可以带时间。如果没有时间，或者时间是0，代表阻塞。如果有时间，则设置一个超时时间。
                        方法selector.wakeup()可以外部控制让它不阻塞。这时select的结果返回是0。
                    懒加载：
                        其实再触碰到selector.select()调用的时候，触发了epoll_ctl的调用
                 */
                while (selector.select(500) > 0) {
                    Set<SelectionKey> selectionKeys = selector.selectedKeys();  // 拿到返回的有状态的fd结果集
                    Iterator<SelectionKey> iter = selectionKeys.iterator();  // 将有状态的fd结果集转成迭代器
                    //所以，不管你是哪一种多路复用器，你只能告诉我fd的状态，我作为应用程序，还需要一个一个的去处理他们的R/W。同步好辛苦！！！
                    //我们之前用NIO的时候，需要自己对每一个fd都去调用系统调用，浪费资源。那么你看，现在是不是只调用了一次select方法，就能知道具体的那些可以R/W了？是不是很省力？
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove(); //这时一个set，不移除的话会重复循环处理
                        if (key.isAcceptable()) { //我前边强调过，socket分为两种，一种是listen的，一种是用于通信 R/W 的，所以拿到之后需要判断它是可读还是可写。
                            //这里是重点，如果要去接受一个新的连接，语义上，accept接受连接且返回新连接的FD，对吧？
                            //那新的FD放在哪里？
                            //1、如果使用select，poll的时候，因为他们在内核没有开辟空间，那么由jvm去维护一个集合(是个native方法)，和前边的fd4那个listen的放在一起
                            //2、如果使用epoll的话，我们希望通过epoll_ctl把新的客户端fd注册到内核空间
                            acceptHandler(key);
                        } else if (key.isReadable()) {
                            readHandler(key);
                            //在当前线程，readHandler处理了很多东西，那么这个方法可能会阻塞，如果阻塞了十年，其他的IO早就没电了。那其他的IO怎么办？
                            //所以，这就是为什么提出了IO THREADS，我把读到的东西扔出去，而不是现场处理
                            //你想，redis是不是用了epoll？redis是不是有个io threads的概念？redis是不是单线程的？它的worker是单线程的，但是它的io threads是多线程的。
                            //你想，tomcat 8,9版本之后，是不是也提出了一种异步的处理方式？是不是也在 IO 和处理上解耦？
                            //这些都是等效的。
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void acceptHandler(SelectionKey key) {
        try {
            ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
            SocketChannel client = ssc.accept(); //来啦，目的是调用accept接受客户端  fd7
            client.configureBlocking(false);

            ByteBuffer buffer = ByteBuffer.allocate(8192);  //前边讲过了
            /*
                你看，调用了register。register做了什么呢？
                1、select，poll：在jvm里开辟一个数组，把 fd7 放进去
                2、epoll：调用epoll_ctl(fd3,ADD,fd7,EPOLLIN
             */
            client.register(selector, SelectionKey.OP_READ, buffer);
            System.out.println("新客户端：" + client.getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readHandler(SelectionKey key) {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = (ByteBuffer) key.attachment();
        buffer.clear();
        int read = 0;
        try {
            while (true) {
                read = client.read(buffer);
                if (read > 0) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        client.write(buffer);
                    }
                    buffer.clear();
                } else if (read == 0) {
                    break;
                } else {
                    client.close();
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SocketMultiplexingSingleThreadv1 service = new SocketMultiplexingSingleThreadv1();
        service.start();
    }
}
```



### TCP 

#### 三次握手

1. **C -> S (syn)**    C 说，我想连接
2. **S -> C (syn+ack)**    发完之后，C 知道了 S 能收到自己的消息
3. **C -> S (ack)**    发完之后，S 知道了 C 能收到自己的消息（确认是双向的），这就是为什么需要第三次握手

三次握手之后，双方开辟资源，建立了 socket



#### 四次分手

![image-20200720212402282](images/image-20200720212402282-1595251446842.png)

1. **C -> S (FIN)**    C 说，我想断开
2. **S -> C (FIN+ack)**    S 知道了 C 想断开
3. **S -> C (FIN)**    S 说，我也想断开
4. **C -> S (ack)**    C 说，好的，断开吧

**四次分手之后，还会等两个传输时间，才会释放资源。**因为如果最后 C 端返回的 ACK 号丢失了，这时 S 端没有收到 ACK，会重发一遍 FIN，如果此时客户端的套接字已经被删除了，会发生什么呢？套接字被删除，端口被释放，这时别的应用可能创建新的套接字，恰好分配了同一个端口号，而服务器重发的 FIN 正好到达，这个 FIN 就会错误的跑到新的套接字里面，新的套接字就开始执行断开操作了。为了避免这样的误操作，C 端会等几分钟再删除套接字。



#### socket

套接字，`ip:port ip:port` 四元组，为了区分每一个 socket 对应关系



### IP

- IP 地址 `‭11000000‬ ‭10101000‬ ‭10010110‬ 0000‭0011‬ (192.168.150.11)`

- 子网掩码：二进制按位与 `11111111 11111111 11111111 00000000 (255.255.255.0)` 

- 子网：`‭11000000‬ ‭10101000‬ ‭10010110‬ 0000‭0000 (192.168.150.0)`‬

- 下一跳机制

  - 路由表

    ![route.png](images/route.png)
    
  - 通过修改 mac 地址找下一跳。在不考虑 NAT 的情况下，ip 地址不会改变
  
    - mac 地址记录在 arp 表中
    - 链路层![lianluceng](images/lianluceng.png)