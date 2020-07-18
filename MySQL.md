# MySQL

##### log 有多少种？

​		binlog, undolog, redolog, relaylog, errorlog, slowlog 等

##### log 有多少种？

​		binlog, undolog, redolog, relaylog, errorlog, slowlog 等

​		所有存储引擎都有binlog，errorlog，relaylog，slowlog

**redolog**

​		固定大小，循环写，两阶段提交

##### undolog是否需要落盘？

​		

##### 事务是怎么实现的？

​		事务中的所有操作作为一个整体，像原子一样不可分割（原子性），通过undolog 回滚日志实现

##### 持久性是怎么实现的？

​		持久性通过redolog和binlog共同保证。

​		事务提交前，只需要将redolog持久化，不需要将数据持久化。系统崩溃时，系统可以根据redolog中的内容，将数据恢复到最新状态。

##### MVCC

​		multi version concurrency control，多版本并发控制

##### MySQL有多少种锁？

​		共享锁，排它锁，独占锁，间隙锁，临键锁，自增锁，意向锁

​		MVCC：multi version concurrency control 多版本并发控制

​		排它锁怎么加？query for update

​		共享锁怎么加？lock in share mode

##### WAL

​		Write Ahead Log溢写日志

##### 自定义变量的使用

​		在给一个变量赋值的同时，使用这个变量

```mysql
select actor_id, @rounum:=@rownum+1 as rownum from actor limit 10;
```

##### 分区表

​		创建表时使用partition by子句定义每个分区存放的数据，在执行查询的时候，优化器会根据分区定义过滤那些没有我们需要数据的分区，这样查询就无须扫描所有分区。





### 存储引擎

- innodb
  - 有redolog, undolog
  - 簇族索引
- myisam
  - 非簇族索引
  - 不支持事务
- memory
  - 数据在内存中，有持久化文件
  - 默认使用哈希索引

### 事务

- Spring事务和数据库事务有什么区别？
- 隔离级别
  - 能够模拟脏读、幻读、不可重复读的情况

<img src="C:\Users\Bug\Desktop\大总结\20200622212043729.png" alt="img" style="zoom:67%;" />

- ACID
  - Atomicity 原子性
    - 事务必须是一个原子的操作序列单元
    - 使用 undolog 逻辑日志实现
  - Consistency 一致性
    - **事务的执行**不能破坏**数据库数据**的完整性和一致性
    - 事务执行的结果必须使数据库从**一个一致性状态**转变到**另一个一致性状态**
    - 如果事务被迫中断，不应该有一部分被写入物理数据库
  - Isolation 隔离性
    - 锁机制
    - 并发环境中，并发的事务是相互隔离的，并发执行的事务之间不能相互干扰
    - 隔离级别：假设A，B都开启了事务
      - 读未提交（未授权读取）：即使A事务未提交，B事务也能看到A的修改
      - 读已提交（授权读取）：A事务提交后，B事务中才能看到A的修改
      - 可重复读：无论A怎么修改，事务B在事务期间都不会看到A的修改
      - 串行化：所有事物只能一个接一个处理，不能并发执行
  - Durability 持久性
    - 事务一旦提交，数据必须永久保存
    - 即使宕机，重启后也能恢复到事务成功结束时的状态
    - redolog



### 锁

##### MyIsam

只能锁表

- 共享读锁
- 独占写锁

##### Innodb

支持表锁，行锁。实质上锁的是索引，如果没有索引的话，退化成为表锁。

- 共享锁（s），又称读锁
- 排它锁（x），又称写锁