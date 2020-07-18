# 网络IO

#### 同步模型、异步模型

- 同步模型：程序自己读取，程序在IO上的模型就叫**同步模型**
- 异步模型：程序把读取的过程交给内核，自己做自己的事情，叫**异步模型**

允许程序去调用内核，来监控更多的客户端，直到一个或多个文件描述符是可用的状态，再返回。减少了用户态、内核态的无用切换。

#### BIO

- 阻塞式的 `ServerSocket`
  - 阻塞地等待客户端的连接，连接后抛出一个线程
  - 阻塞地等待客户端发送消息

#### NIO

- 非阻塞式的 `ServerSocketChannel`
  - `ss.configBlocking(false)` 设置非阻塞
  - 在`accept(3,`中空转，要么返回client的描述符，要么返回-1
- 缺点
  - `ByteBuffer`只有一个指针，用起来有很多坑
  - NIO的存在的问题：C10K问题

    - 放大：C10K当并发量很大的时候，文件描述符会很多，每循环一次，都要调用一次recv系统调用（复杂度O(n)），进行用户态到内核态的切换，切换时性能损耗大。
    - 缩小：当C10K只有1个C发来了数据，只使用到了1个有用的系统调用，剩余n-1次的监听都是无效的

#### 多路复用

- Linux内核提供的`select`多路复用器返回给程序一个list，告诉程序哪些可以读取，然后程序要自己读取。这是同步模型。

- 缺点
  - 如果有很多长连接，内核每次都要给程序传递很多连接对象

#### epoll

- epoll 也是多路复用器，但是它有一个链表，规避了对于文件描述符的全量遍历，这是它与 select poll 的区别。

- 它不负责读取IO，只关心返回结果

- Epoll是Event poll，把有数据这个事件通知给程序，还需要程序自己取读取数据

- `man epoll` 帮助文档：

  The `epoll` API performs a similar task to `poll`(2): monitoring multiple file descriptors to see if I/O is possible on any of them.  The `epoll` API can be used either as an edge-triggered(边缘触发) or a  level-triggered(条件触发)  interface and scales well to large numbers of watched file descriptors(可以很好地扩展到大量监视文件描述符).  The following system calls are provided to create and manage an epoll instance:  [ 注：这里 (2) 的意思是 2 类系统调用。类似地，还有 7 类杂项]

  - `epoll_create` creates an epoll instance  and  returns  a  file  descriptor  referring  to  that instance.

    创建成功之后，返回一个 fd 文件描述符例如`fd6`。在内核开辟一块空间，里面存放红黑树。

  - `epoll_ctl`, This  system  call  performs  control  operations  on  the `epoll`(7) instance referred to by the file descriptor epfd.  It requests that the operation op be performed for the target file descriptor, fd.

    ```c
    int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event);
    ```

    l例如，在文件描述符`fd6`中，使用 `EPOLL_CTL_ADD` 添加服务器用于 listen 的文件描述符

    - `int op` 可选参数：`EPOLL_CTL_ADD`, `EPOLL_CTL_MOD`, `EPOLL_CTL_DEL`

  - `epoll_wait` waits for I/O events, blocking the calling thread if no events are currently available.

    epoll_wait 在等待链表中放入数据，而不需要去遍历所有的文件描述符。

- 早期在没有上述三个系统调用的时候，使用了 mmap 来提速，后期在 2.6 内核版本之后，提供了这些系统调用，就不需要 mmap 这种实现方式了。在早期，需要用户调用 mmap 实现两端的内存共享。



#### AIO

- AIO 是异步的模型
- 使用的是 callback / hook / templateMethod 回调，是基于事件模型的 IO
- Netty封装的是NIO，不是AIO
  - AIO只有Window支持（内核中使用CompletionPort完成端口）
  - 在Linux上的AIO只不过是对NIO的封装而已（是基于epoll的轮询）

### Netty

Netty主要用于网络通信。

- 很多网页游戏的服务器都是用Netty写的。
- Tomcat，Zookeeper，很多开源分布式的底层也是netty写的。



![image-20200718112457843](C:\Users\Bug\Desktop\大总结\image-20200718112457843.png)
