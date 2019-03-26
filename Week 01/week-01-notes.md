## Week 1: Undirected Graphs

> We define an undirected graph API and consider the adjacency-matrix and adjacency-lists representations. We introduce two classic algorithms for searching a graph—depth-first search and breadth-first search. We also consider the problem of computing connected components and conclude with related problems and applications.

### Introduction to Graphs
* *Graph* - set of **vertices** connected pairwise by **edges**
* Why study graph algorithms?
  * Thousands of practical applications
  * Hundreds of graph algorithms known
  * Interesting and broadly useful abstraction
  * Challenging branch of computer science and discrete math
* *Path* - sequence of vertices connected by edges
* *Cycle* - path whose first and last vertices are the same
* Two vertices are **connected** if there is a path between them

### Graph API
* *Graph drawing* - provides intuition about the structure of the graph (however, intuition can be misleading)
* *Vertex representation*:
  * In this lecture we will use integers between 0 and *V - 1*
  * In application, we convert between names and integers with a symbol table

* The typical graph API is as follows:
```java
public class Graph {
  // Create an empty graph with V vertices
  Graph(int V) {}

  // Create a graph from input stream
  Graph(In in) {}

  // Add an edge v-w
  void addEdge(int v, int w) {}

  // Vertices adjacent to v
  Iterable<Integer> adj(int v) {}

  // Number of vertices
  int V() {}

  // Number of edges
  int E() {}

  // String representation
  String toString() {}
}
```

* Adjacency-matrix graph representation requires a two-dimensional *V-by-V* boolean array, where each edge *v-w* in graph: `adj[v][w] == adj[w][v]` returns true
* The Java implementation for the adjacency-list graph representation is as follows:
```java
public class Graph {
  // Use bag data type
  private final int V;
  private Bag<Integer>[] adj;

  // Create empty graph with V vertices
  public Graph(int V) {
    this.V = V;
    adj = (Bag<Integer>[]) new Bag[V];
    for (int v = 0; v < V; v++) {
      adj[v] = new Bag<Integer>();
    }
  }

  // Add edge v-w
  public void addEdge(int v, int w) {
    adj[v].add(w);
    adj[w].add(v);
  }

  // Iterator for vertices adjacent to v
  public Iterable<Integer> adj(int v) {
    return adj[v];
  }
}
```

* In practice, use adjacency-lists representation:
  * Algorithms based on iterating over vertices adjacent to *v*
  * Real-world graphs tend to be **sparse** (huge number of vertices, small average vertex degree)

### Depth-first Search
* Take a maze graph for example:
  * Each vertex is an intersection and each edge a passage
  * The goal is to explore every intersection in the maze
* Tremaux maze exploration:
  * Unroll a ball of string behind you
  * Mark each visited intersection and each visited passage
  * Retrace steps when no unvisited options

* DFS (Depth-first Search) is similar to this maze problem in that:
  * Goal is to systematically search through a graph
  * Find all vertices connected to a given source vertex
  * Find a path between two vertices
* The design pattern for graph processing is as follows:
  * Decouple graph data type from graph processing
  * Create a `Graph` object
  * Pass the `Graph` to a graph-processing routine
  * Query the graph-processing routine for information
* DFS then becomes the following:
  * Goal is to visit vertex *v*
  * Mark vertex *v* as visited
  * Recursively visit all unmarked vertices adjacent to *v*

* DFS can be implemented in Java as follows:
```java
public class DepthFirstPaths {
  // marked[v] = true if v connected to s
  private boolean[] marked;
  // edgeTo[v] = previous vertex on path from s to v
  private int[] edgeTo;
  private int s;

  // Initialize data structures
  public DepthFirstPaths(Graph G, int s) {
    // ...
    // Find vertices connected to s
    dfs(G, s);
  }

  // Recursive DFS
  private void dfs(Graph G, int v) {
    marked[v] = true;
    for (int w : G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
        edgeTo[w] = v;
      }
    }
  }
}
```

* DFS marks all vertices connected to *s* in time proportional to the sum of their degrees
* Each vertex connect to *s* is visited once
* After DFS, can find vertices connected to s in constant time and can find a path to *s* (if one exists) in time proportional to its length

### Breadth-first Search
* BFS (Breadth-first Search) is similar to DFS but we use a queue and we repeat the following process until queue is empty:
  * Remove vertex *v* from queue
  * Add to queue all unmarked vertices adjacent to *v* and mark them
* *DFS* - put unvisited vertices on a **stack**
* *BFS* - put unvisited vertices on a **queue**
* *Shortest path* - find path from *s* to *t* that uses **fewest number of edges**
* BFS computes shortest paths (fewest number of edges) from *s* to all other vertices in a graph in time proportional to *E + V*
* Each vertex connected to *s* is visited once

* BFS is implemented in Java as follows:
```java
public class BreadthFirstPaths {
  private boolean[] marked;
  private int[] edgeTo;
  // …

  private void bfs(Graph G, int s) {
    Queue<Integer> q = new Queue<Integer>();
    q.enqueue(s);
    marked[s] = true;
    while (!q.isEmpty()) {
      int v = q.dequeue();
      for (int w : G.adj(v)) {
        if (!marked[w]) {
          q.enqueue(w);
          marked[w] = true;
          edgeTo[w] = v;
        }
      }
    }
  }
}
```

### Connected Components
* Connectivity queries:
  * Vertices *v* and *w* are **connected** if there is a path between them
  * Goal: preprocess graph to answer queries of the form: *is v connected to w?* in **constant** time
  * Solution: use DFS
* *Connected components* - a maximal set of connected values
* The *is-connected-to* relation is an *equivalence relation* if:
  * Reflexive: *v* is connected to *v*
  * Symmetric: if *v* is connected to *w*, then *w* is connected to *v*
  * Transitive: if *v* is connected to *w* and *w* connected to *x*, then *v* connected to *x*
* Given connected components we can answer queries in constant time
* To visit a vertex *v*:
  * Mark vertex *v* as visited
  * Recursively visit all unmarked vertices adjacent to *v*

### Challenges
* What are some challenges we face in graph-processing?
  * Is a graph bipartite? DFS-based solution
  * Find a cycle: DFS-based solution
  * Find a (general) cycle that uses every edge exactly twice: Eulerian tour
  * Find a cycle that visits every vertex exactly once: Hamiltonian cycle
  * Are two graphs identical except for vertex names? Graph isomorphism (no one knows)
  * Lay out a graph in the plane without crossing edges? Linear-time DFS-based planarity algorithm discovered by Tarjan in 1970s (too complicated for most practitioners)
* The Seven Bridges of Konigsberg: is there a (general) cycle that uses each edge exactly twice? a connected graph is Eulerian if and only if all vertices have **even** degree

## Week 1: Directed Graphs

> In this lecture we study directed graphs. We begin with depth-first search and breadth-first search in digraphs and describe applications ranging from garbage collection to web crawling. Next, we introduce a depth-first search based algorithm for computing the topological order of an acyclic digraph. Finally, we implement the Kosaraju-Sharir algorithm for computing the strong components of a digraph.

### Introduction to Digraphs
* *Digraph* - set of vertices connected pairwise by **directed** edges
* Some digraph problems include:
  * *Path* - is there a directed path from *s* to *t*?
  * *Shortest path* - what is the shortest directed path from *s* to *t*?
  * *Topological sort* - can you draw a digraph so that all edges point upwards?
  * *Strong connectivity* - is there a directed path between all pairs of vertices?
  * *Transitive closure* - for which vertices *v* and *w* is there a path from *v* to *w*?
  * *PageRank* - what is the importance of a web page?
