## Quiz 7: Graph 3-colorability

*An undirected graph is 3-colorable if the vertices can be colored red, green, or blue in such a way that no edge connects two vertices with the same color. Prove that `3COLOR` is **NP**-complete.*

A: First, we would have to check the entire graph for each edge. Second, we check if each edge has different colored vertices attached. Consequently, `3COLOR` is NP-complete since it runs in poly-time.
