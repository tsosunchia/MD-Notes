

#### Approach #1: Subtree Sum and Count [Accepted]

**Intuition**

Let `ans` be the returned answer, so that in particular `ans[x]` be the answer for node `x`.

Naively, finding each `ans[x]` would take O(N)*O*(*N*) time (where N*N* is the number of nodes in the graph), which is too slow. This is the motivation to find out how `ans[x]` and `ans[y]` are related, so that we cut down on repeated work.

Let's investigate the answers of neighboring nodes x*x* and y*y*. In particular, say xy*x**y* is an edge of the graph, that if cut would form two trees X*X* (containing x*x*) and Y*Y* (containing y*y*).

![Tree diagram illustrating recurrence for ans[child]](../docs/images/sketch1.png)

Then, as illustrated in the diagram, the answer for x*x* in the entire tree, is the answer of x*x* on X*X* `"x@X"`, plus the answer of y*y* on Y*Y* `"y@Y"`, plus the number of nodes in Y*Y* `"#(Y)"`. The last part `"#(Y)"` is specifically because for any node `z in Y`, `dist(x, z) = dist(y, z) + 1`.

By similar reasoning, the answer for y*y* in the entire tree is `ans[y] = x@X + y@Y + #(X)`. Hence, for neighboring nodes x*x* and y*y*, `ans[x] - ans[y] = #(Y) - #(X)`.

**Algorithm**

Root the tree. For each node, consider the subtree S_{\text{node}}*S*node of that node plus all descendants. Let `count[node]` be the number of nodes in S_{\text{node}}*S*node, and `stsum[node]` ("subtree sum") be the sum of the distances from `node` to the nodes in S_{\text{node}}*S*node.

We can calculate `count` and `stsum` using a post-order traversal, where on exiting some `node`, the `count` and `stsum` of all descendants of this node is correct, and we now calculate `count[node] += count[child]` and `stsum[node] += stsum[child] + count[child]`.

This will give us the right answer for the `root`: `ans[root] = stsum[root]`.

Now, to use the insight explained previously: if we have a node `parent` and it's child `child`, then these are neighboring nodes, and so `ans[child] = ans[parent] - count[child] + (N - count[child])`. This is because there are `count[child]` nodes that are `1` easier to get to from `child` than `parent`, and `N-count[child]` nodes that are `1` harder to get to from `child` than `parent`.

![Tree diagram illustrating recurrence for ans[child]](../docs/images/sketch2.png)

Using a second, pre-order traversal, we can update our answer in linear time for all of our nodes.
