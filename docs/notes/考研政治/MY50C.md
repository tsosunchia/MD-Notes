### Recent

MIT OS课程

Karmam filter ：GPS怎么实时计算速度 定位误差怎么办？

设置断点有几种方式？你说的方式为什么只要1字节？怎么调试操作系统？怎么防止被别人调试？

有root权限，如何写linux驱动做到无感登录？

旅行商问题怎么实现？

crontab怎么实现（触发）的？时钟怎么实现的？

ntp时钟服务器怎么搭建？dns怎么污染？

docker命令为什么要加sudo？

### Total (Before 2022.03)

- 论文 / 专利
- 看完+整理完 左神算法体系班、进阶班，对做过的leetcode有个归类、有自己的总结；剑指 offer 过一下，查缺补漏
- 基础
  - 《操作系统真象还原》
  - 破解校园网
  - C++
  - CSAPP
- 看论文
  - GFS，Map Reduce，Big Table
  - FUSE
  - skiplist
- 其他短期
  - ByteCTF https://security.bytedance.com/fe/ai-challenge#/home 10.16-10.17
  - google doc what i wonder

### Future (After 2022.03)

> zlibrary 找电子书

- 算法导论
- 深入理解Linux内核
- 编译原理
- 理解了实现再谈网络性能
- SICP

### MIT 麻省理工

> 如何做上面的 lab？
>
> 优质课程收集：https://github.com/jackwener/CS-Awesome-Courses/blob/master/README.md

- 6.854 算法进阶
- [MIT 大名鼎鼎的 6.828 Operating System](https://pdos.csail.mit.edu/6.828/2018/schedule.html) 和 6.S01 一样
- 线性代数 18.06

这个网站是提供MIT课程视频的, https://ocw.mit.edu/, 你把课程号（例如：6•006）输入搜索栏就能显示课程了，但是有少数课程不提供课程视频，不过大多数课程是提供的。

[6.824 ](https://link.zhihu.com/?target=https%3A//pdos.csail.mit.edu/6.824/)Distributed System: 系统方向非常好的一门课程，每堂课都讲一个新的分布式系统模型，没有教材，每堂课都是直接讲论文。老师是MIT PDOS的神牛Robert Morris (不错，这人就是当年因为发明蠕虫病毒而蹲监然后回MIT当教授的神人)和Frans Kaashoek。这些分布式系统都是实际用在各个大公司里的系统，比如说Spark, GFS，PNUTS。当年我修这门课的时候感觉课程压力非常大，有期中期末考试，有lab作业，有reading work, 还有course project，但是整个课程设计得非常好。lab要用Golang实现，硬生生地学了门新的语言。最后我的course project是用Go实现了一个Spark原型系统（[metalbubble/GoSpark](https://link.zhihu.com/?target=https%3A//github.com/metalbubble/GoSpark)），那个还是2013年的时候，Spark还刚开始崭露头角：）。

6.830 [Database Systems](https://link.zhihu.com/?target=http%3A//db.csail.mit.edu/6.830/): 数据库系统的一门核心课程。由数据库的一大山头Samuel Madden教授。前半部分比较基础的数据库的知识，后半段主要在讲Distributed Databases的东西，各种consistency挺有意思，也是database比较火的研究方向。

[18.409 Algorithmic Aspects of Machine Learning, Spring 2015](https://link.zhihu.com/?target=http%3A//people.csail.mit.edu/moitra/409.html): Ankur Moitra教的machine learning课程。课程切入点跟一般的机器学习课程都不同，Ankur自己是做theory背景的（攻FOCS, STOC之类的会），所以这个课程有深厚的理论根基。对sparse coding, topic model, tensor decompositions等会有脑洞大开的认识。

6.869 Advances in Computer Vision ([Fall 2016](https://link.zhihu.com/?target=http%3A//6.869.csail.mit.edu/fa16/) [Fall 2015](https://link.zhihu.com/?target=http%3A//6.869.csail.mit.edu/fa15/)) 我TA过的一门计算机视觉的课程。课件不错，过了一遍CV的传统内容，也增加了很多deep learning的内容，适合初学者入门，也适合除了deep learning就不懂computer vision其他东西的朋友。。。Final Project我设计了一个Mini Places Challenge, 让学生可以组队比赛，训练深度模型。



强推：[6.006 Introduction to Algorithms](https://link.zhihu.com/?target=https%3A//www.youtube.com/watch%3Fv%3DHtSuA80QTyo%26list%3DPLUl4u3cNGP61Oq3tWYp6V_F-5jb5L2iHb)

主讲人：Srini Devadas, Professor at MIT

这是一门比较难的算法课，一定要做他们的练习题，不然无法保证你真的理解了这些算法。犹记得15年的时候准备google的面试，一周把这门课上完了，然而并没有掌握好。难归难，算法则是一定要学好的。



强推：[Networking tutorial](https://link.zhihu.com/?target=https%3A//www.youtube.com/watch%3Fv%3DXaGXPObx2Gs%26list%3DPLowKtXNTBypH19whXTVoG3oKSuOcw_XeW%26index%3D1)

主讲人：Ben Eater, Khan Academy(former)

这是一门很短却直至精髓的课。从大家都能看到的网线开始讲电信号如何传输，最后以介绍TCP协议为止。每个人对学习方法的偏好不同，有人喜欢抽象，有人喜欢具体，我正好是后者。这门课就非常具体，把每个bit如何在网络中游走讲的一清二楚。这门课不涉及算法，优化等，十分适合入门。



推荐：[Distributed Computer Systems](https://link.zhihu.com/?target=https%3A//www.youtube.com/playlist%3Flist%3DPLawkBQ15NDEkDJ5IyLIJUTZ1rRM9YQq6N)

Srinivasan Keshav, Professor of Computer Science at the University of Cambridge

这门课主要讲的是网络通信，从底层的switch一步步讲到TCP等协议。老师很有意思，课上各种喷google是如何侵犯大家的隐私的。



推荐：[Intro to Computer Science](https://link.zhihu.com/?target=https%3A//classroom.udacity.com/courses/cs101)

主讲人：David Evans, Professor of Computer Science University of Virginia

这门课的核心是使用python来打造一个搜索引擎。之所以推荐它，是因为这门课更接近于软件工程，有大量的实际操作，需要你写出正确的代码才能进行下一步学习。另外这位老师的冷笑话也蛮好听，后面还会推荐他的另一门课。



推荐：[Computation Structures](https://link.zhihu.com/?target=https%3A//www.youtube.com/user/Cjtatmitdotedu/feed)

主讲人：Chris Terman. Senior Lecturer, Electrical Engineering and Computer Science at MIT

对于想要了解计算机是如何从简单的电压变化演变到现在的操作系统的，可以看看这门课。老头子风趣幽默，特别喜欢冷笑话。



推荐：[C++ Tutorial](https://link.zhihu.com/?target=https%3A//www.youtube.com/watch%3Fv%3D18c3MTX0PK0%26list%3DPLlrATfBNZ98dudnM48yfGUldqGD0S4FFb)

主讲人：The Cherno(Yan Chernikov), Software Engineer at Electronic Arts (EA)

小伙子长得贼精神，讲的贼清楚，若想要学C++，推荐看他的视频。





### 2022.03

不知道为什么 2022.03 成了大家口中的 milestone

### 2022.02

### 2022.01

### 2021.12

### 2021.11

### 2021.10



### MY50C 计划

多看牛客 多面试 多交流 白天没时间的话可以晚上面试 没事看看基础 保持随时跑路的能力 发论文 实习 3年->5年经验 算法面试 小公司试一下吧 至少知道怎么开始

什么是面试？面试不是他准备了一个问题，你给他答案，这不叫面试。面试的唯一目的，是你用自己的表达，让他从想法上、从coding上、从交流上喜欢你。面试就是聊，你把他聊开心了，目的就达到了。你可以一边想，一边在嘴里碎碎念，但凡他有点提醒，就顺着他引导的方向往下想。

解出这道题不是目的，有的草包可能看不懂你的代码，而让面试官明白你的思路，让面试官喜欢你，才是最重要的。

- 手撕算法
  - leetcode
  - 左神算法 至少要看完体系学习班 1-40 进阶班leetcode专题49-78 不能再省了 否则就cover不住了（左神说5个月）
  - 剑指 offer
- 基础
  - 经典书籍
  - mashibing课程
- 面经
  - 关注牛客面经 关注常问面试题
  - 阶段性面试 检验水平
- 项目
  - 关注产出、亮点
- 个人博客
  - 深度、原创文章
  - 直播形式

### 技能清单

精通Java核心，熟悉JDK中各种集合，队列，锁机制，多线程，高并发相关底层结构及原理
精通各种GC，各种垃圾回收算法，有JVM调优经验
精通NIO相关的常见IO模型以及优化策略
熟悉常用的数据结构与算法，熟悉常用的设计模式
熟悉LVS、Nginx负载均衡策略及原理
熟悉Redis内存模型，Redis缓存常见问题 
熟悉MySQL的存储引擎，索引、事务原理
熟悉SpringBoot框架
熟悉分布式系统设计方案和原理，熟悉Zookeeper分布式协调框架
熟悉分布式消息队列Kafka的原理及使用
熟悉Linux常用命令，对Linux内核源码有过研究
有GitLab + Jenkins项目自动构建、自动部署经验；对Docker，Kubernetes有一定的了解
了解分布式系统AKF拆分原则，CAP定理，Paxos协议
了解集群下的并发解决方案，有Nginx，LVS，KeepAlive实战经验，支持HA高可用

### 进度

| 公司 | base | 网站 | 行测 | 笔试 | 一面 | 二面 | 方向 | 备注 |
| ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- | ---- |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |
|      |      |      |      |      |      |      |      |      |