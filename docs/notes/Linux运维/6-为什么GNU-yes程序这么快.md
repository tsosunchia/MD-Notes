# 为什么 GNU yes 程序这么快

> https://dgideas.net/2021/why-gnu-yes-so-fast/
>
> 发布日期：2021年4月23日

几乎所有人在学习一门新的程序设计语言时，首先学到的功能便是将一段字符串——通常是“Hello, world!”——输出到屏幕上。在 *NIX 系统中，就一直存在着这样一个功能简单的程序，能够重复地将字符 “y”（或是其他什么东西）输出到屏幕上，通常用在一些需要用户重复输入确认指令的场景下：这个程序叫做 yes。

> ***NIX**：表示类 Unix 操作系统，这通常包括 FreeBSD、GNU/Linux、MINIX 以及 macOS 等
>
> [Wikipedia](https://en.wikipedia.org/wiki/Unix-like)



2017 年，有一位名为 [kjensenxz](https://www.reddit.com/user/kjensenxz/) 的 Reddit 用户[无意间发现](https://www.reddit.com/r/unix/comments/6gxduc/how_is_gnu_yes_so_fast/)他在 Linux 系统中使用的 yes 程序——GNU yes 具有极其夸张的吞吐性能：

```
$ yes | pv > /dev/null
... [10.2GiB/s] ...
```

在他的测试中，相比其他系统中预置的 yes 程序：NetBSD 版本（139MiB/s）、illumos 版本（141MiB/s）、OS X 版本（使用了一个较旧的 NetBSD 版本，但速度差不多）、BusyBox 版本（107MiB/s）与 Ultrix 版本（139MiB/s）的速度，Linux 系统附带的 GNU 版本的 yes 程序简直快得令人发指。

在本文中，我们尝试复现 yes 程序的逻辑，并探究何种因素能够提高程序的吞吐性能——在做“向屏幕输出”这个简单的事情上。为便于复现文中结果，我们使用了 Google Cloud 的 F1-micro 实例：这种共享核心机器能够通过在 Google Cloud 上绑定信用卡的方式免费获得。

## 迈出第一步

我们进行的第一个测试，是在 F1-micro 规格实例中测量 GNU yes 程序的吞吐量，这将作为本文讨论的性能基准：

```
$ yes | pv > /dev/null
... [2.35GiB/s] ...
```

这看起来要比其他用户测试的 10.2GiB/s 的速度要慢，不过考虑到测试环境的性能所限（F1-micro 实例最多使用 20% 的 CPU 时间），这个结果是合理的。

### hello world 程序

类似于教科书上“Hello, world!”程序的标准写法，我们首先尝试使用最简单的方法实现我们的 yes 程序：

```
#include "stdio.h"
int main() {
    for (;;) {
        printf("y\n");
    }
}
```

在使用 gcc 9.3.0 以及默认编译选项的情况下，我们得到了约 90.9MiB/s 的速度。看起来这要比 GNU yes 程序慢上不止一个数量级，但是 C 语言版本的实现却比 Python 版本的相应实现（`python3 -c "while True: print('y')"`，速度约为 8.69MiB/s）快上十倍不止。

那么，是什么原因导致我们的实现与 GNU 版本的 yes 实现速度上差异如此大呢？我们想到程序的吞吐量可能与使用的输出方式以及编译选项有关。

## 提高性能的尝试

我们首先尝试更改程序的编译选项。通常来讲，更高阶的编译器优化选项可以带来“免费”的性能提升。一般来说，适用于 GCC 编译器的常用编译选项主要有以下三个：

- `-O3`：最高的编译优化选项，它启用了一些需要额外的编译时间以及编译期内存占用的一些优化选项
- `-march=native`：告诉编译器应当为本地系统生成最优化的编译方案
- `-mtune=native`：告诉编译器应当以最适用于本地处理器的方式编排程序

GCC 编译器的 [x86 Options 页面](https://gcc.gnu.org/onlinedocs/gcc/x86-Options.html)介绍了编译桌面应用程序时常见的编译指令。

在我们使用上述优化指令编译程序后，我们得到了如下数据：

```
$ gcc yes.c -o yes -O3 -march=native -mtune=native
$ ./yes | pv > /dev/null
... [90.5MiB/s] ...
```

为什么我们的性能还降低了？看起来我们的 yes.c 程序已经足够简单，无法通过简单的编译器优化来获得任何额外的性能提升。我们应该转变思维，从将字符串向屏幕的输出方式的角度来思考如何优化。



在初次尝试中，我们在每个循环中只输出一个字符 “y”。那么，能不能在每次循环中输出多个 “y” 来提高性能呢？出于这个思路，我们写出了下列程序，利用一个字符串 `buf` 来提高输出性能：

```
#include "stdio.h"
int main() {
    char buf[1024];
    for (int i=0; i<512; i++) {
        buf[2*i] = 'y';
        buf[2*i+1] = '\n';
    }
    for (;;) {
        printf("%s", buf);
    }
}
```

然后，编译运行并测试程序的吞吐性能：

```
$ gcc yes.c -o yes -O3
$ ./yes | pv > /dev/null
... [579MiB/s] ...
```

效果不一般！看来通过缓存字符串提高吞吐性能的方式是可行的。在上例中，我们设置了 `buf` 的大小为 1024 个字符（即 512 个 `y\n`）。使用不同的 `buf` 大小会带来怎样的性能影响呢，我们进行了如下测试：

| `buf` 大小 | 吞吐量（三次测试平均值） |
| :--------: | :----------------------: |
|    512     |         577MiB/s         |
|    1024    |         579MiB/s         |
|    2048    |         540MiB/s         |
|    4096    |         543MiB/s         |
|    8192    |         577MiB/s         |
|   16384    |         586MiB/s         |

编译选项均为 `gcc yes.c -o yes`

使用了缓冲区后，我们注意到程序的吞吐性能有了较大提升，但仍与 GNU yes 的 2.35GiB/s 有很大差距。瓶颈出现在哪里呢？我们需要深入 GNU yes 的代码，从实现上寻找我们的答案。GNU yes 的代码在 GitHub 上[有一份副本](https://github.com/coreutils/coreutils/blame/master/src/yes.c)，我们可以看到这个近 30 年前编写的程序如今仍在被活跃维护。

> 插一句话，对于喜欢考据 yes 程序历史的同学，我们[在这里](https://github.com/dspinellis/unix-history-repo/blob/4c37048d6dd7b8f65481c8c86ef8cede2e782bb3/usr/src/cmd/yes.c)找到了 GNU yes 程序的第一版本代码，祝时空旅行愉快

在 GNU yes 代码中，我们注意到了以下部分：

```
...
/* Repeatedly output the buffer until there is a write error; then fail.  */
while (full_write (STDOUT_FILENO, buf, bufused) == bufused)
  continue;
...
```

能阅读 GNU yes 的实现对我们来说是一大进步，但为什么我们没有达到和 GNU yes 一样的速度？我们在做几乎完全相同的事情，而这里的 `full_write` 函数是 C 语言标准 [`write` 函数](https://pubs.opengroup.org/onlinepubs/007904875/functions/write.html)的一个装饰器。除了使用的输出函数不同（GNU 使用的是 `write` 函数，而我们是 `printf`）以外，另一个引人注目的点在于他们定义了一个独特的 `BUFSIZ` 值。

在 Reddit 有关的讨论原文中，作者 kjensenxz 调研了 BUFSIZ 是如何被定义的——这个值是被定义在 `stdio.h` 中的宏：

```
#define BUFSIZ _IO_BUFSIZ
```

而 `_IO_BUFSIZ` 则在 `libio.h` 中被定义：

```
#define _IO_BUFSIZ _G_BUFSIZ
```

而最后，作者在 `_G_config.h` 文件中找到了 `_G_BUFSIZ` 的定义：

```
#define _G_BUFSIZ 8192
```

这里定义的缓存大小是 4 的倍数，用来确保[**内存对齐**](https://stackoverflow.com/questions/381244/purpose-of-memory-alignment/381368#381368)（Memory alignment）。现在一切都清晰了，`BUFSIZ` 的大小是内存页对齐的（内存页大小通常是 4096 的倍数）。因此，我们可以效仿 GNU yes 程序以编写我们自己的 yes 程序。

## 再次起飞

根据 GNU yes 程序带来的灵感，我们重新组织了 yes.c 程序。这段程序来自 Reddit 讨论帖的原始代码：

```
#define LEN 2
#define TOTAL 8192
int main() {
    char yes[LEN] = {'y', '\n'};
    char *buf = malloc(TOTAL);
    int bufused = 0;
    while (bufused < TOTAL) {
        memcpy(buf+bufused, yes, LEN);
        bufused += LEN;
    }
    while(write(1, buf, TOTAL));
    return 1;
}
```

并且在相同的测试环境中进行测试：

```
$ gcc yes.c -o yes -O3
$ ./yes | pv > /dev/null
... [2.31GiB/s] ...
```

这一次我们达到了和 GNU yes （2.35GiB/s）几乎相同的吞吐效率。所以，到底还能不能更快？还真的有一些好奇的网友在此基础上进行了额外实验。他们发现，随着吞吐量的增加，最终制约吞吐量大小的因素在于测量程序 `pv` 本身的吞吐限制。因此，这位好奇的网友进行了[一系列暴改操作](https://www.reddit.com/r/unix/comments/6gxduc/how_is_gnu_yes_so_fast/diua761/)，最终在他的计算机上达到了惊人的 123GiB/s 的吞吐效率：

```
$ taskset -c 0 ./yes | taskset -c 0 ~/pv-1.6.0/pv >/dev/null 
 469GiB 0:00:02 [ 123GiB/s] [ <=>
```

像“向屏幕输出文字”这样的简单操作，在众多对性能有着极致追求的极客手中，竟然最终有着高达百倍的性能提升。计算机领域还有着许多像[**平方根倒数速算法**](https://en.wikipedia.org/wiki/Fast_inverse_square_root)（Fast inverse square root）一样的充满人类智慧和对于性能无尽追求的程序艺术品，我们有时间将会慢慢讨论。

## 参考链接

- 2017 年在 Reddit 论坛上的讨论：[How is GNU `yes` so fast?](https://www.reddit.com/r/unix/comments/6gxduc/how_is_gnu_yes_so_fast/)
- Matthias Endler 的博客文章《[A Little Story About the `yes` Unix Command](https://endler.dev/2017/yes/)》，这是另一名对 yes 程序好奇并尝试复现它的用户
- Wikipedia 上的[相关条目](https://en.wikipedia.org/wiki/Yes_(Unix))