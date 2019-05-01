## Quiz 1: Longest path and longest cycle

Consider the following two problems:

* LongestPath: Given an undirected graph *G* and two distinct vertices *s* and *t*, find a simple path (no repeated vertices) between *s* and *t* with the most edges. 
* LongestCycle: Given an undirected graph *G'*, find a simple cycle (no repeated vertices or edges except the first and last vertex) with the most edges. 

Show that *LongestPath* linear-time reduces to *LongestCycle*

A: If there exists a path *p* from *s* to *t*, then there exists a cycle *c* such that *p* contains the longest path from *s* to *t* in graph *G*. If cycle does not exists then there is no path from *s* to *t*.