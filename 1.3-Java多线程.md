---
typora-copy-images-to: /images/
---



# 多线程

##### 启动线程的三种方式

- 继承Thread，重写run方法
- 实现Runnable接口，重写run方法（或Lambda表达式）
- 通过线程池来启动（实际上是以上两种之一）

##### sleep yield join 的含义

- `sleep`：当前线程暂停一段时间，让别的线程去执行。不释放锁。睡眠时间到，自动复活
- `yield`：当前线程执行时，停下来进入等待队列。系统调度算法决定哪个线程继续运行（有可能还是自己）
- `join`：在当前线程加入调用的join线程，等调用的线程运行完了，自己再继续执行

##### wait notify notifyAll 的含义

Object 对象中有三个方法wait()、notify()、notifyAll()，它们的用途都是用来控制线程的状态。

- wait：必须在同步方法/同步代码块中被调用；释放锁，调用该线程的方法进入**等待队列**，直到被唤醒
- notify：随机唤醒等待队列中等待的一个线程，使得该线程由**等待状态**进入**可运行状态**
- notifyAll：唤醒在此对象监视器上等待的所有线程，被唤醒的线程将以常规方式与在该对象上主动同步的其他所有线程进行竞争

##### 如何关闭线程？interrupt 的含义？

Java没有提供任何机制来安全地终止线程。它提供了 interrupt，这仅仅是会通知到被终止的线程“你该停止运行了”，由编写者决定如何处理 InterruptedException

```java
@Override
public void run() {
    while (true && !Thread.currentThread().isInterrupted()) { // 判断是否处于中断状态
        System.out.println("go");
        try {
            throwInMethod();
        } catch (InterruptedException e) {
            // 恢复设置中断状态，便于在下一个循环的时候检测到中断状态，正常退出
            Thread.currentThread().interrupt();
            e.printStackTrace();// 可以记录一下日志
        }
    }
}
```

不要使用 stop() 关闭线程。

sleep阻塞时候会自动检测中断：`java.lang.InterruptedException: sleep interrupted`



##### ThreadLocal

ThreadLocal 是线程引用对象，线程之间不共享。

- 为什么要有ThreadLocal？

  - Spring的声明式事务会用到。（Spring的声明式事务在一个线程里）
  - connection在连接池里，不同的connection之间怎么形成完整的事务？把connection放在当前线程的ThreadLocal里面，以后拿的时候从ThreadLocal直接拿，不去线池里面拿。

- ThreadLocal是怎么做到线程独有的？

  <img src="images\threadlocal.png" alt="img" style="zoom: 33%;" />

  - `ThreadLocalMap`是当前Thread的一个成员变量，其 Key 是 ThreadLocal 对象，值是 Entry 对象，Entry 中只有一个 Object 类的 vaule 值。
  - 使用虚引用，让`Key`指向`ThreadLocal`

##### 强软弱虚四种引用

- 强引用 StrongReference

  - `Object o = new Object()`
  - 只要有引用指向它，就算是OOM了，也不会被回收

- 软引用 SoftReference

  ```java
  SoftReference<byte[]> m = new SoftReference<>(new byte[1024 * 1024 * 10]);
  System.out.println(m.get());
  ```

  - 内存空间不够时，弱引用会被回收
  - 用来做缓存

- 弱引用 WeakReference

  ```java
  WeakReference<M> m = new WeakReference<>(new M());
  System.out.println(m.get());
  ```

  - 只要发生GC，弱引用就会被回收
  - ThreadLocal 中会使用弱引用

- 虚引用 PhantomReference

  ```java
  private static final ReferenceQueue<M> QUEUE = new ReferenceQueue<>();
  public static void main(String[] args) {
      PhantomReference<M> phantomReference = new PhantomReference<>(new M(), QUEUE);
  ```

  - 必须和引用队列`ReferenceQueue`联合使用
  - 虚引用对象被回收前，会被加入到队列中，需要另一线程不断检测队列并处理
  - 用来管理**堆外内存**，堆外内存包括：
    - 方法区
    - NIO 的`DirectByteBuffer`



### 线程池

#### 线程池前值知识

##### Executor 接口关系

<img src="images/20200627235119496.png" alt="img" style="zoom: 60%;" />

##### Callable

​		类似于Runnable，但是可以有返回值

##### Future

​		存储将来执行的结果。Callable被执行完之后的结果，被封装到Future里面。

##### FutureTask

​		更加灵活，是Runnable和Future的结合，既是一个Runnable，又可以存结果。

##### CompletableFuture

​		可以用来管理多个Future的结果，对各种各样的结果进行组合处理。提供了非常好用的接口，十分友好。

> 场景：假设你需要提供一个服务，这个服务查询 京东、淘宝、天猫 对于同一类产品的价格并汇总展示，你用CompletableFuture开启三个线程，来完成这个任务，三个任务全部完成之后，才能继续向下运行。

#### 线程池

**ThreadPoolExecutor**：我们通常所说的线程池。多个线程共享同一个任务队列。

- SingleThreadPool

  为什么会有单线程的线程池？单线程的线程池是有任务队列的；线程池能帮你提供线程生命周期的管理。

  - 线程池里面只有一个线程
  - 保证我们扔进去的任务是被顺序执行的

- CachedThreadPool

  当任务到来时，如果有线程空闲，我就用现有的线程；如果所有线程忙，就启动一个新线程。

  - 核心线程数为0
  - 最大线程数是Integer.MAX_VALUE
  - 保证任务不会堆积
  - SynchronousQueue 是容量为0的阻塞队列，每个插入操作必须等待另一个线程执行相应的删除操作

- FixedThreadPool

  - 固定线程数的线程池
  - 适合做一些**并行**的计算，比如你要找1-200000之内所有的质数，你将这个大任务拆成4个小线程，共同去运行，肯定比串行计算要更快。

- ScheduledPool

  - 专门用来执行定时任务的一个线程池

**ForkJoinPoll**：先将任务分解，最后再汇总，可以有返回值或无返回值。每个线程有自己的任务队列。

- WorkStealingPool
  - 普通的线程池是有一个线程的集合，所有线程去同一个任务队列里面取任务，取出任务之后执行，而 WorkStealingPool 是每一个线程都有自己独立的任务队列，如果某一个线程执行完自己的任务之后，要去别的线程那里偷任务，分担别的线程的任务。
  - WorkStealingPool 本质上还是一个 ForkJoinPool



#### 自定义一个线程池

```java
public class TestThreadPool {
    static class Task implements Runnable {
        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " is running task");
        }
    }
    public static void main(String[] args) {
        // ThreadPoolExecutor 7个参数
        ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 4,
                60, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(4),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()); // 调用者处理服务，这里是main调用
        for (int i = 0; i < 8; i++) { // 开启8个任务，放进线程池执行
            tpe.execute(new Task());
        }
        tpe.shutdown();
    }
}
```

##### new ThreadPoolExecutor() 7个参数

- corePoolSize：核心线程数

- maximumPoolSize：最大线程数

- keepAliveTime：空闲线程生存时间

- TimeUnit：生存时间的单位

- BlockingQueue<Runnable>：各种各样的任务队列

- ThreadFactory：线程工厂

  可以使用`Executors.defaultThreadFactory()`，也可以自定义工厂，指定线程名称

- RejectedExecutionHandler：线程池忙且任务队列满时的 **拒绝策略**

  - CallerRunsPolicy，让调用者线程去处理任务
  - AbortPolicy，抛异常
  - DiscardPolicy，扔掉，不抛异常
  - DiscardOldestPolicy，扔掉排队时间最久的