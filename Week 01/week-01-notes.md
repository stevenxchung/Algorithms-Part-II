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
