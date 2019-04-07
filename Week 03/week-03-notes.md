## Week 3: Maximum Flow and Minimum Cut

> In this lecture we introduce the maximum flow and minimum cut problems. We begin with the Ford–Fulkerson algorithm. To analyze its correctness, we establish the maxflow–mincut theorem. Next, we consider an efficient implementation of the Ford–Fulkerson algorithm, using the shortest augmenting path rule. Finally, we consider applications, including bipartite matching and baseball elimination.

### Introduction to Maxflow
* A mincut problem takes an edge-weighted digraph, source vertex *s*, and target vertex *t*
* **Mincut** - a ***st*-cut (cut)** is a partition of the vertices into two disjoint sets, with *s* in one set *A* and *t* in the other set *B*, it's **capacity** is the sum of the capacities of the edges from *A* to *B*
* **Minimum st-cut (mincut) problem** - find a cut of minimum capacity

* A maxflow problem is similar in that the input is an edge-weighted digraph, source vertex *s*, and target vertex *t*
* **Maxflow** - a ***st*-flow (flow)** is an assignment of values to the edges such that:
  * Capacity constraint: 0 <= edge's flow <= edge's capacity
  * Local equilibrium: inflow = outflow at every vertex (except *s* and *t*)
* The **value** of a flow is the inflow at *t*
* **Maximum st-flow (maxflow) problem** - find a flow of maximum value

* Mincut and maxflow problem are actually dual:
> The maximum amount of flow passing from the source to the sink is equal to the total weight of the edges in the minimum cut, i.e. the smallest total weight of the edges which if removed would disconnect the source from the sink. [wiki: Max-flow min-cut theorem](https://en.wikipedia.org/wiki/Max-flow_min-cut_theorem)
