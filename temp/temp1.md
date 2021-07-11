#### Approach 1: Topological Sorting

**Intuition**

First of all, let us clarify some concepts.

> The *distance* between two nodes is the number of edges that connect the two nodes.

Note, normally there could be multiple paths to connect nodes in a graph. In our case though, since the input graph can form a tree from any node, as specified in the problem, there could only be *one path* between any two nodes. In addition, there would be no cycle in the graph. As a result, there would be no ambiguity in the above definition of *distance*.

> The *height* of a tree can be defined as the *maximum distance* between the root and all its leaf nodes.

With the above definitions, we can rephrase the problem as finding out the nodes that are *overall* close to all other nodes, especially the leaf nodes.

> If we view the graph as an *area of circle*, and the leaf nodes as the *peripheral* of the circle, then what we are looking for are actually the *[centroids](https://en.wikipedia.org/wiki/Centroid)* of the circle, *i.e.* nodes that is close to all the peripheral nodes (leaf nodes).

![example of graph](../docs/images/310_example.png)

For instance, in the above graph, it is clear that the node with the value `1` is the *centroid* of the graph. If we pick the node `1` as the root to form a tree, we would obtain a tree with the *minimum height*, compared to other trees that are formed with any other nodes.

Before we proceed, here we make one assertion which is essential to the algorithm.

> For the tree-alike graph, the number of centroids is no more than 2.

If the nodes form a chain, it is intuitive to see that the above statement holds, which can be broken into the following two cases:

- If the number of nodes is even, then there would be two centroids.
- If the number of nodes is odd, then there would be only one centroid.

![example of centroids](../docs/images/310_1_2_centroids.png)

For the rest of cases, we could prove by *contradiction*. Suppose that we have 3 centroids in the graph, if we remove all the non-centroid nodes in the graph, then the 3 centroids nodes must form a *triangle* shape, as follows:

![triangle](../docs/images/310_triangle.png)

Because these centroids are equally important to each other, and they should equally close to each other as well. If any of the edges that is missing from the triangle, then the 3 centroids would be reduced down to a single centroid.

However, the triangle shape forms a *cycle* which is *contradicted* to the condition that there is no cycle in our tree-alike graph. Similarly, for any of the cases that have more than 2 centroids, they must form a cycle among the centroids, which is contradicted to our condition.

Therefore, there cannot be more than 2 centroids in a tree-alike graph.

**Algorithm**

> Given the above intuition, the problem is now reduced down to looking for all the ***centroid*** nodes in a tree-alike graph, which in addition are no more than two.

The idea is that we *trim* out the leaf nodes layer by layer, until we reach the *core* of the graph, which are the centroids nodes.

![trim](../docs/images/310_trim.png)

Once we trim out the first layer of the leaf nodes (nodes that have only one connection), some of the non-leaf nodes would become leaf nodes.

The trimming process continues until there are only two nodes left in the graph, which are the *centroids* that we are looking for.

The above algorithm resembles the *topological sorting* algorithm which generates the order of objects based on their dependencies. For instance, in the scenario of course scheduling, the courses that have the least dependency would appear first in the order.

In our case, we trim out the leaf nodes first, which are the **farther** away from the centroids. At each step, the nodes we trim out are closer to the centroids than the nodes in the previous step. At the end, the trimming process terminates at the **centroids** nodes.

**Implementation**

Given the above algorithm, we could implement it via the *Breadth First Search* (BFS) strategy, to trim the leaf nodes layer by layer (*i.e.* level by level).

- Initially, we would build a graph with the *[adjacency list](https://en.wikipedia.org/wiki/Adjacency_list)* from the input.
- We then create a queue which would be used to hold the leaf nodes.
- At the beginning, we put all the current leaf nodes into the queue.
- We then run a loop until there is only two nodes left in the graph.
- At each iteration, we remove the current leaf nodes from the queue. While removing the nodes, we also remove the edges that are linked to the nodes. As a consequence, some of the non-leaf nodes would become leaf nodes. And these are the nodes that would be trimmed out in the next iteration.
- The iteration terminates when there are no more than two nodes left in the graph, which are the desired *centroids* nodes.

Here are some sample implementations that are inspired from the post of [dietpepsi](https://leetcode.com/problems/minimum-height-trees/discuss/76055/Share-some-thoughts) in the discussion forum.
