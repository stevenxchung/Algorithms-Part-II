# Algorithms, Part II - Princeton University

This course covers programming and problem solving with applications as well as data structures and algorithms:

| topic      | data structures and algorithms                     | part    |
| ---------- | -------------------------------------------------- | ------- |
| data types | stack, queue, bag, union-find, priority queue      | part I  |
| sorting    | quicksort, mergesort, heapsort                     | part I  |
| searching  | BST, red-black BST, hash table                     | part I  |
| graphs     | BFS, DFS, Prim, Kruskal, Dijkstra                  | part II |
| strings    | radix sorts, tries, KMP, regexps, data compression | part II |
| advanced   | B-tree, suffix array, maxflow                      | part II |

- Week 1:
  - Undirected Graphs
  - Directed Graphs
- Week 2:
  - Minimum Spanning Trees
  - Shortest Paths
- Week 3:
  - Maximum Flow and Minimum Cut
  - Radix Sorts
- Week 4:
  - Tries
  - Substring Search
- Week 5:
  - Regular Expressions
  - Data Compression
- Week 6:
  - Reductions
  - Linear Programming
  - Intractability

## Week 1: Undirected Graphs

> We define an undirected graph API and consider the adjacency-matrix and adjacency-lists representations. We introduce two classic algorithms for searching a graph—depth-first search and breadth-first search. We also consider the problem of computing connected components and conclude with related problems and applications.

### Introduction to Graphs

- _Graph_ - set of **vertices** connected pairwise by **edges**
- Why study graph algorithms?
  - Thousands of practical applications
  - Hundreds of graph algorithms known
  - Interesting and broadly useful abstraction
  - Challenging branch of computer science and discrete math
- _Path_ - sequence of vertices connected by edges
- _Cycle_ - path whose first and last vertices are the same
- Two vertices are **connected** if there is a path between them

### Graph API

- _Graph drawing_ - provides intuition about the structure of the graph (however, intuition can be misleading)
- _Vertex representation_:

  - In this lecture we will use integers between 0 and _V - 1_
  - In application, we convert between names and integers with a symbol table

- The typical graph API is as follows:

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

- Adjacency-matrix graph representation requires a two-dimensional _V-by-V_ boolean array, where each edge _v-w_ in graph: `adj[v][w] == adj[w][v]` returns true
- The Java implementation for the adjacency-list graph representation is as follows:

```java
public class Graph {
  // Use bag data type
  private final int V;
  private final Bag<Integer>[] adj;

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

- In practice, use adjacency-lists representation:
  - Algorithms based on iterating over vertices adjacent to _v_
  - Real-world graphs tend to be **sparse** (huge number of vertices, small average vertex degree)

### Depth-first Search

- Take a maze graph for example:
  - Each vertex is an intersection and each edge a passage
  - The goal is to explore every intersection in the maze
- Tremaux maze exploration:

  - Unroll a ball of string behind you
  - Mark each visited intersection and each visited passage
  - Retrace steps when no unvisited options

- DFS (Depth-first Search) is similar to this maze problem in that:
  - Goal is to systematically search through a graph
  - Find all vertices connected to a given source vertex
  - Find a path between two vertices
- The design pattern for graph processing is as follows:
  - Decouple graph data type from graph processing
  - Create a `Graph` object
  - Pass the `Graph` to a graph-processing routine
  - Query the graph-processing routine for information
- DFS then becomes the following:

  - Goal is to visit vertex _v_
  - Mark vertex _v_ as visited
  - Recursively visit all unmarked vertices adjacent to _v_

- DFS can be implemented in Java as follows:

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

- DFS marks all vertices connected to _s_ in time proportional to the sum of their degrees
- Each vertex connect to _s_ is visited once
- After DFS, can find vertices connected to s in constant time and can find a path to _s_ (if one exists) in time proportional to its length

### Breadth-first Search

- BFS (Breadth-first Search) is similar to DFS but we use a queue and we repeat the following process until queue is empty:
  - Remove vertex _v_ from queue
  - Add to queue all unmarked vertices adjacent to _v_ and mark them
- _DFS_ - put unvisited vertices on a **stack**
- _BFS_ - put unvisited vertices on a **queue**
- _Shortest path_ - find path from _s_ to _t_ that uses **fewest number of edges**
- BFS computes shortest paths (fewest number of edges) from _s_ to all other vertices in a graph in time proportional to _E + V_
- Each vertex connected to _s_ is visited once

- BFS is implemented in Java as follows:

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

- Connectivity queries:
  - Vertices _v_ and _w_ are **connected** if there is a path between them
  - Goal: preprocess graph to answer queries of the form: _is v connected to w?_ in **constant** time
  - Solution: use DFS
- _Connected components_ - a maximal set of connected values
- The _is-connected-to_ relation is an _equivalence relation_ if:
  - Reflexive: _v_ is connected to _v_
  - Symmetric: if _v_ is connected to _w_, then _w_ is connected to _v_
  - Transitive: if _v_ is connected to _w_ and _w_ connected to _x_, then _v_ connected to _x_
- Given connected components we can answer queries in constant time
- To visit a vertex _v_:
  - Mark vertex _v_ as visited
  - Recursively visit all unmarked vertices adjacent to _v_

### Challenges

- What are some challenges we face in graph-processing?
  - Is a graph bipartite? DFS-based solution
  - Find a cycle: DFS-based solution
  - Find a (general) cycle that uses every edge exactly twice: Eulerian tour
  - Find a cycle that visits every vertex exactly once: Hamiltonian cycle
  - Are two graphs identical except for vertex names? Graph isomorphism (no one knows)
  - Lay out a graph in the plane without crossing edges? Linear-time DFS-based planarity algorithm discovered by Tarjan in 1970s (too complicated for most practitioners)
- The Seven Bridges of Konigsberg: is there a (general) cycle that uses each edge exactly twice? a connected graph is Eulerian if and only if all vertices have **even** degree

## Week 1: Directed Graphs

> In this lecture we study directed graphs. We begin with depth-first search and breadth-first search in digraphs and describe applications ranging from garbage collection to web crawling. Next, we introduce a depth-first search based algorithm for computing the topological order of an acyclic digraph. Finally, we implement the Kosaraju-Sharir algorithm for computing the strong components of a digraph.

### Introduction to Digraphs

- _Digraph_ - set of vertices connected pairwise by **directed** edges
- Some digraph problems include:
  - _Path_ - is there a directed path from _s_ to _t_?
  - _Shortest path_ - what is the shortest directed path from _s_ to _t_?
  - _Topological sort_ - can you draw a digraph so that all edges point upwards?
  - _Strong connectivity_ - is there a directed path between all pairs of vertices?
  - _Transitive closure_ - for which vertices _v_ and _w_ is there a path from _v_ to _w_?
  - _PageRank_ - what is the importance of a web page?

### Diagraph API

- The diagraph API is as follows:

```java
public class Digraph {
  // Create an empty diagraph with V vertices
  Digraph(int V) {}

  // Create a diagraph from input stream
  Diagraph(In in) {}

  // Add a directed edge v -> w
  void addEdge(int v, int w) {}

  // Vertices pointing from v
  Iterable<Integer> adj (int v)

  // Number of vertices
  int V() {}

  // Number of edges
  int E() {}

  // Reverse of this diagraph
  Digraph reverse() {}

  // String representation
  String toString() {}
}
```

- The Java implementation for the adjacency-list digraph representation is very similar to the graph representation, the main difference is in `addEdge()`:

```java
public class Digraph {
  // Use bag data type
  private final int V;
  private final Bag<Integer>[] adj;

  // Create empty digraph with V vertices
  public Digraph(int V) {
    this.V = V;
    adj = (Bag<Integer>[]) new Bag[V];
    for (int v = 0; v < V; v++) {
      adj[v] = new Bag<Integer>();
    }
  }

  // Add edge v->w
  public void addEdge(int v, int w) {
    adj[v].add(w);
  }

  // Iterator for vertices pointing from v
  public Iterable<Integer> adj(int v) {
    return adj[v];
  }
}
```

- In practice use adjacency-lists representation:
  - Algorithms based on iterating over vertices pointing from _v_
  - Real-world digraphs tend to be sparse (huge number of vertices, small average vertex degree)

### Digraph Search

- Reachability - find all vertices reachable from _s_ along a directed path
- DFS in digraphs:

  - Same method as for undirected graphs
  - Every undirected graph is a digraph (with edges in both directions)
  - DFS is a **digraph** algorithm
  - Recall DFS, for digraphs is slightly different (one direction):
    - Marks vertex _v_ as visited
    - Recursively visits all unmarked vertices pointing from _v_

- DFS implementation for digraphs is identical to DFS implementation for graphs:

```java
public class DirectedDFS {
  // True if path from s
  private boolean[] marked;

  // Constructor marks vertices reachable from s
  public DirectedDFS(Digraph G, int s) {
    marked = new boolean[G.V()];
    dfs(G, s);
  }

  // Recursive DFS does the work
  private void dfs(Digraph G, int v) {
    marked[v] = true;
    for (int w: G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
    }
  }

  // Client can ask whether any vertex is reachable from s
  public boolean visited(int v) {
    return marked[v];
  }
}
```

- DFS enables direct solution of simple digraph problems:
  - Reachability
  - Path finding
  - Topological sort
  - Directed cycle detection
- DFS forms the basis for solving difficult digraph problems:

  - Two-satisfiability
  - Directed Euler path
  - Strongly-connected components

- Similarly, BFS is same method as for undirected graphs:
  - Every undirected graph is a digraph (with edges in both directions)
  - BFS is a **digraph** algorithm
- BFS computes shortest paths (fewest number of edges) from _s_ to all other vertices in a digraph in time proportional to _E + V_
- BFS works as follows for directed graphs:
  - Repeat until queue is empty
  - Remove vertex _v_ from queue
  - Add to queue all unmarked vertices point from _v_ and mark them
- For multiple-source shortest paths:
  - Given a digraph and a **set** of source vertices
  - Find shortest path from any vertex in the set to each other vertex

### Topological Sort

- _DAG_ - directed acyclic graph
- _Topological sort_ - redraw DAG so all edges point upwards (use DFS)
- Topological sort works as follows:
  - Run DFS
  - Return vertices in reverse postorder
- Some observations:
  - Reverse DFS postorder of a DAG is a topological order
  - A digraph has a topological order if and only if no directed cycle

### Strong Components

- Vertices _v_ and _w_ are **strongly connected** if there is both a directed path from _v_ to _w_ **and** a directed path from _w_ to _v_
- Strong connectivity is an **equivalence relation**:
  - _v_ is strongly connected to _v_
  - If _v_ is strongly connected to _w_, then _w_ is strongly connected to _v_
  - If _v_ is strongly connected to _w_ and _w_ to _x_, then _v_ is strongly connected to _x_
- A **strong component** is a maximal subset of strongly-connected vertices

- Kosaraju-Sharir algorithm:
  - _Reverse graph_ - strong components in _G_ are same as in _G^R_
  - _Kernel DAG_ - contract each strong component into a single vertex
  - _Idea_:
    - Compute topological order (reverse postorder) in kernel DAG
    - Run DFS, considering vertices in reverse topological order
  - _Phase 1_ - compute reverse postorder in _G^R_
  - _Phase 2_ - run DFS in _G_, visiting unmarked vertices in reverse postorder of _G^R_
- Another way to implement the Kosaraju-Sharir algorithm:
  - _Phase 1_ - run DFS on _G^R_ to compute reverse postorder
  - _Phase 2_ - run DFS on _G_, considering vertices in order given by first DFS
- An observation of the Kosaraju-Sharir algorithm is that it computers the strong components of a digraph in time proportional to _E + V_

- Recall DFS implementation for connected components in an undirected graph:

```java
public class CC {
  private boolean marked[];
  private int[] id;
  private int count;

  public CC(Graph G) {
    marked = new boolean[G.V()];
    id = new int[G.V()];
    for (int v = 0; v < G.V(); v++) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
  }

  private void dfs(Graph G, int v) {
    marked[v] = true;
    id[v] = count;
    for (int w: G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
    }
  }

  public boolean connected(int v, int w) {
    return id[v] == id[w];
  }
}
```

- For strong components in a digraph we could use two DFS:

```java
public class KosarajuSharirSCC {
  private boolean marked[];
  private int[] id;
  private int count;

  public KosarajuSharirSCC(Digraph G) {
    marked = new boolean[G.V()];
    id = new int[G.V()];
    DepthFirstOrder dfs = new DepthFirstOrder(G.reverse());
    for (int v: dfs.reversePost()) {
      if (!marked[v]) {
        dfs(G, v);
        count++;
      }
    }
  }

  private void dfs(Digraph G, int v) {
    marked[v] = true;
    id[v] = count;
    for (int w: G.adj(v)) {
      if (!marked[w]) {
        dfs(G, w);
      }
    }
  }

  public boolean stronglyConnected(int v, int w) {
    return id[v] == id[w];
  }
}
```

## Week 2: Minimum Spanning Trees

> In this lecture we study the minimum spanning tree problem. We begin by considering a generic greedy algorithm for the problem. Next, we consider and implement two classic algorithm for the problem—Kruskal's algorithm and Prim's algorithm. We conclude with some applications and open problems.

### Introduction to MSTs

- What is a MST (Minimum Spanning Tree)?
  - Given an undirected graph _G_ with positive edge weights (connected), a **spanning tree** of _G_ is a sub-graph _T_ that is both a **tree** (connected and acyclic) and **spanning** (includes all of the vertices)
- The goal of a MST is to find a minimum weight spanning tree
- MST is a fundamental problem with diverse applications (see lecture slides for more examples)

### Greedy Algorithm

- First we simplify assumptions:
  - Edge weights are distinct
  - Graph is connected
- If the previous assumptions are true then MST exists and is unique

- A **cut** in a graph is a partition of its vertices into two (nonempty) sets
- A **crossing edge** connects a vertex in one set with a vertex in the other
- **Cut property** - given any cut, the crossing edge of min weight is in the MST

- Since the cut property is defined we can apply the same idea to greedy MSTs:
  - Start with all edges colored gray
  - Find cut with no black crossing edges; color its min-weight edge black
  - Repeat until _V - 1_ edges are colored black
- The greedy algorithm computes the MST
- What is edge weights are not distinct?
  - Greedy MST algorithms still correct if equal weights are present (correctness proof fails however)
- What if graph is not connected?
  - Compute minimum spanning forest = MST of each component

### Edge-Weighted Graph API

- Edge abstraction for the weighted edge API is as follows:

```java
public class Edge implements Comparable<Edge> {
  // Create a weighted edge v-w
  Edge(int v, int w, double weight) {}

  // Either endpoint
  int either() {}

  // The endpoint that's not v
  int other(int v) {}

  // Compare this edge to that edge
  int compareTo(Edge that) {}

  // The weight
  double weight() {}

  // String representation
  String toString() {}
}
```

- The weighted edge implementation in Java becomes:

```java
public class Edge implements Comparable<Edge> {
  private final int v, w;
  private final double weight;

  // Constructor
  public Edge(int v, int w, double weight) {
    this.v = v;
    this.w = w;
    this.weight = weight;
  }

  public int either() {
    // Either endpoint
    return v;
  }

  public int other(int vertex) {
    // Other endpoint
    if (vertex == v) {
      return w;
    } else {
      return v;
    }
  }

  public int compareTo(Edge that) {
    // Compare edges by weight
    if (this.weight < that.weight) {
      return -1;
    } else if (this.weight > that.weight) {
      return +1;
    } else {
      return 0;
    }
  }
}
```

- The edge-weighted graph API is can be represented as follows:

```java
public class EdgeWeightedGraph {
  // Create an empty graph with V vertices
  EdgeWeightedGraph(int V) {}

  // Add weighted edge e to this graph
  void addEdge(Edge e) {}

  // Edges incident to v
  Iterable<Edge> adj(int v) {}
}
```

- The edge-weighted API allows for self-loops and parallel edges

- How to represent MST for edge-weighted graph API?

```java
public class MST {
  // Constructor
  MST(EdgeWeightedGraph G) {}

  // Edges in MST
  Iterable<Edge> edges() {}
}
```

### Kruskal's Algorithm

- To implement Kruskal's algorithm, consider edges in ascending order of weight:
  - Add next edge to tree _T_ unless doing so would create a cycle
- Kruskal's algorithm computes the MST
- The Java implementation of Kruskal's algorithm is as follows:

```java
public class KruskalMST {
  private Queue<Edge> mst = new Queue<Edge>();

  public KruskalMST(EdgeWeightedGraph G) {
    // Build priority queue
    MinPQ<Edge> pq = new MinPQ<Edge>();
    for (Edge e : G.edges())
    pq.insert(e);
    UF uf = new UF(G.V());
    while (!pq.isEmpty() && mst.size() < G.V() - 1) {
      // Greedily add edges to MST
      Edge e = pq.delMin();
      int v = e.either(), w = e.other(v);
      // Edge v–w does not create cycle
      if (!uf.connected(v, w)) {
        // Merge sets
        uf.union(v, w);
        // Add edge to MST
        mst.enqueue(e);
      }
    }
  }

  public Iterable<Edge> edges() {
    return mst;
  }
}
```

- The running time for Kruskal's algorithm is as follows:

| operation  | frequency | time per op   |
| ---------- | --------- | ------------- |
| build pq   | 1         | _E _ log(E)\* |
| delete-min | _E_       | _log(E)_      |
| union      | _V_       | _log(V)_      |
| connected  | _E_       | _log(V)_      |

### Prim's Algorithm

- There exists several of steps for Prim's algorithm:
  - Start with vertex 0 and greedily grow tree _T_
  - Add to _T_ the minimum weight edge with exactly one endpoint in _T_
  - Repeat until _V - 1_ edges
- Like Kruskal's algorithm, Prim's algorithm also computes the MST
- For the lazy solution, the goal is to find the min weight edge with exactly one endpoint in _T_
- We want to maintain a PQ of **edges** with (at least) one endpoint in _T_:
  - Key = edge, priority = weight of edge
  - Delete-min to determine next edge _e = v - w_ to add to _T_
  - Disregard if both endpoints _v_ and _w_ are marked (both in _T_)
  - Otherwise, let _w_ be the unmarked vertex (not in _T_):
    - Add to PQ any edge incident to _w_ (assuming other endpoint not in _T_)
    - Add _e_ to _T_ and mark with _w_
- The run-time for the lazy version of Prim's algorithm is as follows:

| operation  | frequency | binary heap |
| ---------- | --------- | ----------- |
| delete-min | _E_       | _log(E)_    |
| insert     | _E_       | _log(E)_    |

- There also exists an eager solution of Prim's algorithm:

  - Maintain a PQ of **vertices** connected by an edge to _T_, where priority of vertex _v_ equals the weight of shortest edge connecting _v_ to _T_
  - Delete min vertex _v_ and add its associated edge _e = v - w_ to _T_
  - Update PQ by considering all edges _e = v - x_ incident to _v_:
    - Ignore if _x_ is already in _T_
    - Add _x_ to PQ if not already on it
    - **Decrease priority** of _x_ if _v - x_ becomes shortest edge connecting _x_ to _T_

- We can use the indexed priority queue abstraction to help implement Prim's algorithm:

```java
public class IndexMinPQ<Key extends Comparable<Key>> {
  // Create indexed priority queue with indices 0, 1, …, N-1
  IndexMinPQ(int N) {}

  // Associate key with index i
  void insert(int i, Key key) {}

  // Decrease the key associated with index i
  void decreaseKey(int i, Key key) {}

  // Is i an index on the priority queue?
  boolean contains(int i) {}

  // Remove a minimal key and return its associated index
  int delMin() {}

  // Is the priority queue empty?
  boolean isEmpty() {}

  // Number of entries in the priority queue
  int size() {}
}
```

- Implementation of Prim's algorithm using indexed priority queue:

  - Start with same code as `MinPQ`
  - Maintain parallel arrays `keys[]`, `pq[]`, and `qp[]` so that:
    - `keys[i]` is the priority of `i`
    - `pq[i]` is the index of the key in heap position `i`
    - `qp[i]` is the heap position of the key with index `i`
  - Use `swim(qp[i])` implement `decreaseKey(i, key)`

- Array implementation of Prim's algorithm is optimal for dense graphs
- Binary heap much faster for sparse graphs
- Four-way heap worth the trouble in performance-critical situations
- Fibonacci heap is best in theory but not worth implementing

### MST Context

- Does a linear-time MST algorithm exist? (See lecture slides for deterministic compare-based MST algorithms and their worst-case run-time scenarios)
  - A linear-time randomized MST algorithm (Karger-Klein-Tarjan 1995) exists
- Euclidean MST is defined as follows:
  - Given _N_ points in the plane, find MST connecting them, where the distances between point pairs are their **Euclidean** distances
- In clustering, we divide a set of objects and classify into _k_ coherent groups, we use a **distance function** to specify _closeness_ of two objects
  - Goal of clustering is to get objects in different clusters far apart

## Week 2: Shortest Paths

> In this lecture we study shortest-paths problems. We begin by analyzing some basic properties of shortest paths and a generic algorithm for the problem. We introduce and analyze Dijkstra's algorithm for shortest-paths problems with non-negative weights. Next, we consider an even faster algorithm for DAGs, which works even if the weights are negative. We conclude with the Bellman–Ford–Moore algorithm for edge-weighted digraphs with no negative cycles. We also consider applications ranging from content-aware fill to arbitrage.

### Shortest Paths APIs

- Given an edge weighted digraph, find the shortest path from _s_ to _t_
- There are many applications (think Google Maps!)

- The weighted directed edge API is abstracted as follows:

```java
public class DirectedEdge {
  // Weighted edge v -> w
  DirectedEdge(int v, int w, double weight) {}

  // Vertex v
  int from() {}

  // Vertex w
  int to() {}

  // Weight of this edge
  double weight() {}

  // String representation
  String toString() {}
}
```

- The implementation of weighted directed edge is shown as:

```java
public class DirectedEdge {
  private final int v, w;
  private final double weight;

  public DirectedEdge(int v, int w, double weight) {
    this.v = v;
    this.w = w;
    this.weight = weight;
  }

  // Replaces either() from undirected graphs
  public int from() {
    return v;
  }

  // Replaces other() from undirected graphs
  public int to() {
    return w;
  }

  public int weight() {
    return weight;
  }
}
```

- For edge-weighted digraphs the API is abstracted as:

```java
public class EdgeWeightedDigraph {
  // Edge-weighted digraph with V vertices
  EdgeWeightedDigraph(int V) {}

  // Add weighted directed edge e
  void addEdge(DirectedEdge e) {}

  // Edges pointing from v
  Iterable<DirectedEdge> adj(int v) {}

  // Number of vertices
  int V() {}
}
```

- The convention is to allow self-loops and parallel edges

- Using adjacency-lists, the edge-weighted digraph implementation becomes:

```java
public class EdgeWeightedDigraph {
  private final int V;
  private final Bag<DirectedEdge>[] adj;

  public EdgeWeightedDigraph(int V) {
    this.V = V;
    adj = (Bag<DirectedEdge>[]) new Bag[V];
    for (int v = 0; v < V; v++)
    adj[v] = new Bag<DirectedEdge>();
  }

  // Add edge e = v -> w to only v's adjacency list
  public void addEdge(DirectedEdge e) {
    int v = e.from();
    adj[v].add(e);
  }

  public Iterable<DirectedEdge> adj(int v) {
    return adj[v];
  }
}
```

- Single-source shortest paths API is abstracted as follows:

```java
public class SP {
  // Shortest paths from s in graph G
  SP(EdgeWeightedDigraph G, int s) {}

  // Length of shortest path from s to v
  double distTo(int v) {}

  // Shortest path from s to v
  Iterable <DirectedEdge> pathTo(int v) {}
}
```

- The goal of the single-source shortest path is to find the shortest path from _s_ to every other vertex

### Shortest Path Properties

- **Goal** - find the shortest path from _s_ to every other vertex
- **Observation** - a SPT (shortest-paths tree) solution exists
- **Consequence** - can represent the SPT with two vertex-indexed arrays:

  - `distTo[v]` is length of shortest path from _s_ to _v_
  - `edgeTo[v]` is last edge on shortest path from _s_ to _v_

- Edge relaxation: relax edge `e = v -> w`

  - `distTo[v]` is length of shortest known path from _s_ to _v_
  - `distTo[w]` is length of shortest known path from _s_ to _w_
  - `edgeTo[w]` is last edge on shortest known path from _s_ to _w_
  - If `e = v -> w` gives shorter path to _w_ through _v_, update both `distTo[w]` and `edgeTo[w]`

- Shortest-paths optimality conditions: let _G_ be an edge-weighted digraph, then `distTo[]` are the shortest path distances from _s_ if and only if:

  - `distTo[s] = 0`
  - For each vertex _v_, `distTo[v]` is the length of some path from _s_ to _v_
  - For each edge `e = v -> w`, `distTo[W] <= distTo[v] + e.weight()`

- Generic shortest-paths algorithm:
  - Initialize `distTo[s] = 0` and `distTo[v] = inf` for all other vertices
  - Repeat until optimality conditions are satisfied:
    - Relax any edge
- Generic algorithm computes SPT (if it exists) from _s_
- How to choose which edge to relax?
  - Dijkstra's algorithm (non-negative weights)
  - Topological sort algorithm (no directed cycles)
  - Bellman-Ford algorithm (no negative cycles)

### Dijkstra's Algorithm

- Consider vertices in increasing order of distance from _s_ (non-tree vertex with the lowest `distTo[]` value)
- Add vertex to tree and relax all edges pointing from that vertex
- Dijkstra's algorithm computes a SPT in any edge-weight digraph with non-negative weights

- The implementation of Dijkstra's in Java is as follows:

```java
public class DijkstraSP {
  private DirectedEdge[] edgeTo;
  private double[] distTo;
  private IndexMinPQ<Double> pq;

  public DijkstraSP(EdgeWeightedDigraph G, int s) {
    edgeTo = new DirectedEdge[G.V()];
    distTo = new double[G.V()];
    pq = new IndexMinPQ<Double>(G.V());

    for (int v = 0; v < G.V(); v++) {
      distTo[v] = Double.POSITIVE_INFINITY;
    }
    distTo[s] = 0.0;

    pq.insert(s, 0.0);
    // Relax vertices in order of distance from s
    while (!pq.isEmpty()) {
      int v = pq.delMin();
      for (DirectedEdge e : G.adj(v))
      relax(e);
    }
  }
}

private void relax(DirectedEdge e) {
  int v = e.from(), w = e.to();
  if (distTo[w] > distTo[v] + e.weight()) {
    distTo[w] = distTo[v] + e.weight();
    edgeTo[w] = e;
    if (pq.contains(w)) {
      pq.decreaseKey(w, distTo[w]);
    } else {
      pq.insert (w, distTo[w]);
    }
  }
}
```

- Prim's algorithm is essentially the same as Dijkstra's, the rule to choose the next vertex for the tree for both algorithms is as follows
  - Prim's: closest vertex to the **tree** (via an undirected edge)
  - Dijkstra's: closest vertex to the **source** (via a directed path)
- Both are in a family of algorithms that compute a graph's spanning tree, DFS and BFS are also in this family of algorithms

- Array implementation optimal for dense graphs
- Binary heap much faster for sparse graphs
- Four-way heap worth the trouble in performance-critical situations
- Fibonacci heap is best in theory but not worth implementing

### Edge-weighted DAGs

- Suppose that an edge-weighted digraph has no directed cycles. Is it easier to find shortest paths than in a general digraph?
  - Yes
- The acyclic shortest paths works as follows:
  - Consider the vertices in topological order
  - Relax all edges pointing from that vertex
- Topological sort algorithm computes SPT in any (edge-weights can be negative) edge-weighted DAG in time proportional to _E + V_

- An implementation of shortest paths in edge-weighted DAGs:

```java
public class AcyclicSP {
  private DirectedEdge[] edgeTo;
  private double[] distTo;

  public AcyclicSP(EdgeWeightedDigraph G, int s) {
    edgeTo = new DirectedEdge[G.V()];
    distTo = new double[G.V()];

    for (int v = 0; v < G.V(); v++) {
      distTo[v] = Double.POSITIVE_INFINITY;
    }
    distTo[s] = 0.0;

    Topological topological = new Topological(G);
    for (int v : topological.order()) {
      for (DirectedEdge e : G.adj(v)) {
        relax(e);
      }
    }
  }
}
```

### Negative Weights

- Unfortunately, Dijkstra's algorithm does not work with negative edge weights
- **Negative cycles** - directed cycles whose sum of edge weights is negative
- A SPT exists if and only if (assuming all vertices reachable from _s_) no negative cycles
- Bellman-Ford algorithm:
  - Initialize `distTo[s] = 0` and `distTo[v] = inf` for all other vertices
  - Repeat _V_ times:
    - Relax each edge
- **Proposition** - dynamic programming algorithm computes SPT in any edge-weighted digraph with no negative cycles in time proportional to _E _ V\*
- Some improvements to the Bellman-Ford algorithm:

  - **Observation** - if `distTo[v]` does not change during pass _i_, no need to relax any edge pointing from _v_ in pass _i + 1_
  - **FIFO implementation** - maintain **queue** (be careful to keep at most one copy of each vertex on queue) of vertices whose `distTo[]` changed
  - **Overall effect** - The running time is still proportional to _E _ V\* in worst case but much faster than that in practice

- For single-source shortest-paths implementation:

  - Directed cycles make the problem harder
  - Negative weights make the problem harder
  - Negative cycles make the problem intractable

- To find a negative cycle we start with an abstraction:

```java
// Is there a negative cycle?
boolean hasNegativeCycle() {}

// Negative cycle reachable from s
Iterable <DirectedEdge> negativeCycle() {}
```

- In summary for shortest paths:

  - Dijkstra's algorithm:
    - Nearly linear-time when weights are non-negative
    - Generalization encompasses DFS, BFS, and Prim's
  - Acyclic edge-weighted digraphs
    - Arise in applications
    - Faster than Dijkstra’s algorithm
    - Negative weights are no problem
  - Negative weights and negative cycles
    - Arise in applications
    - If no negative cycles, can find shortest paths via Bellman-Ford
    - If negative cycles, can find one via Bellman-Ford

- Shortest-paths is a broadly useful problem-solving model

## Week 3: Maximum Flow and Minimum Cut

> In this lecture we introduce the maximum flow and minimum cut problems. We begin with the Ford–Fulkerson algorithm. To analyze its correctness, we establish the maxflow–mincut theorem. Next, we consider an efficient implementation of the Ford–Fulkerson algorithm, using the shortest augmenting path rule. Finally, we consider applications, including bipartite matching and baseball elimination.

### Introduction to Maxflow

- A mincut problem takes an edge-weighted digraph, source vertex _s_, and target vertex _t_
- **Mincut** - a **_st_-cut (cut)** is a partition of the vertices into two disjoint sets, with _s_ in one set _A_ and _t_ in the other set _B_, it's **capacity** is the sum of the capacities of the edges from _A_ to _B_
- **Minimum st-cut (mincut) problem** - find a cut of minimum capacity

- A maxflow problem is similar in that the input is an edge-weighted digraph, source vertex _s_, and target vertex _t_
- **Maxflow** - a **_st_-flow (flow)** is an assignment of values to the edges such that:
  - Capacity constraint: 0 <= edge's flow <= edge's capacity
  - Local equilibrium: inflow = outflow at every vertex (except _s_ and _t_)
- The **value** of a flow is the inflow at _t_
- **Maximum st-flow (maxflow) problem** - find a flow of maximum value

- Mincut and maxflow problem are actually dual:
  > The maximum amount of flow passing from the source to the sink is equal to the total weight of the edges in the minimum cut, i.e. the smallest total weight of the edges which if removed would disconnect the source from the sink. [wiki: Max-flow min-cut theorem](https://en.wikipedia.org/wiki/Max-flow_min-cut_theorem)

### Ford-Fulkerson Algorithm

- **Initialization** - start with 0 flow
- **Augmenting path** - find an undirected path from _s_ to _t_ such that:
  - Can increase flow on forward edges (not full)
  - Can decrease flow on backward edge (not empty)
- **Termination** - all paths from _s_ to _t_ are blocked by either a:
  - Full forward edge
  - Empty backward edge

### Maxflow-mincut Theorem

- The **net flow across** a cut (_A_, _B_) is the sum of the flows on its edges from _A_ to _B_ minus the sum of the flows on its edges from from _B_ to _A_
- **Flow-value lemma** - let _f_ be any flow and let (_A_, _B_) be any cut. Then, the net
  flow across (_A_, _B_) equals the value of _f_
- **Augmenting path theorem** - a flow _f_ is a maxflow if and only if no augmenting paths
- **Maxflow-mincut theorem** - value of the maxflow = capacity of mincut
- To compute mincut (_A_, _B_) from maxflow _f_:
  - By augmenting path theorem, no augmenting paths with respect to _f_
  - Compute _A_ = set of vertices connected to _s_ by an undirected path with no full forward or empty backward edges

### Running Time Analysis

- Several questions emerge for maxflow and mincut:
  - How to compute a mincut? Easy
  - How to find an augmenting path? BFS works well
  - If FF terminates, does it always compute a maxflow? Yes
  - Does FF always terminate? If so, after how many augmentations? Yes, FF (Ford-Fulkerson) always terminates provided edge capacities are integers (or augmenting paths are chosen carefully), augmentations requires clever analysis
- Special case for Ford-Fulkerson algorithm: edge capacities are integers between 1 and _U_
- **Integrality theorem** - There exists an integer-valued maxflow (FF finds one)
- The bad news for Ford-Fulkerson algorithm is that even when edge capacities are integers, number of augmenting paths could be equal to the value of the maxflow (although case is easily avoided)
- In summary, Ford-Fulkerson algorithm depends on choice of augmenting paths:

| augmenting path | number of paths     | implementation   |
| --------------- | ------------------- | ---------------- |
| shortest path   | <= _(1/2) _ E _ V_  | queue (BFS)      |
| fattest path    | <= _E _ log(E _ U)_ | priority queue   |
| random path     | <= _E _ U\*         | randomized queue |
| DFS path        | <= _E _ U\*         | stack (DFS)      |

### Java Implementation

- The flow edge API is abstracted as follows:

```java
public class FlowEdge {
  // Create a flow edge v -> w
  FlowEdge(int v, int w, double capacity) {}

  // Vertex this edge points from
  int from() {}

  // Vertex this edge points to
  int to() {}

  // Other endpoint
  int other(int v) {}

  // Capacity of this edge
  double capacity() {}

  // Flow in this edge
  double flow() {}

  // Residual capacity toward v
  double residualCapacityTo(int v) {}

  // Add delta flow toward v
  void addResidualFlowTo(int v, double delta) {}
}
```

- Flow edge could then be implemented as:

```java
public class FlowEdge {
  // From and to
  private final int v, w;
  // Capacity
  private final double capacity;
  // Flow
  private double flow;

  public FlowEdge(int v, int w, double capacity) {
    this.v = v;
    this.w = w;
    this.capacity = capacity;
  }

  public int from() {
    return v;
  }

  public int to() {
    return w;
  }

  public double capacity() {
    return capacity;
  }

  public double flow() {
    return flow;
  }

  public int other(int vertex) {
    if (vertex == v) {
      return w;
    } else if (vertex == w) {
      return v;
    }
  }

  public double residualCapacityTo(int vertex) {
    if (vertex == v) {
      // Backward edge
      return flow;
    } else if (vertex == w) {
      // Forward edge
      return capacity - flow;
    }
  }

  public void addResidualFlowTo(int vertex, double delta) {
    if (vertex == v) {
      // Backward edge
      flow -= delta;
    } else if (vertex == w) {
      // Forward edge
      flow += delta;
    }
  }
}
```

- The flow network API can be abstracted as:

```java
public class FlowNetwork {
  // Create an empty flow network with V vertices
  FlowNetwork(int V) {}

  // Add flow edge e to this flow network
  void addEdge(FlowEdge e) {}

  // Forward and backward edges incident to v
  Iterable<FlowEdge> adj(int v) {}
}
```

- Flow network could then be implemented as:

```java
public class FlowNetwork {
  // Same as EdgeWeightedGraph, but adjacency lists of FlowEdges instead of Edges
  private final int V;
  private Bag<FlowEdge>[] adj;

  public FlowNetwork(int V) {
    this.V = V;
    adj = (Bag<FlowEdge>[]) new Bag[V];
    for (int v = 0; v < V; v++) {
      adj[v] = new Bag<FlowEdge>();
    }
  }

  public void addEdge(FlowEdge e) {
    int v = e.from();
    int w = e.to();
    // Add forward edge
    adj[v].add(e);
    // Add backward edge
    adj[w].add(e);
  }

  public Iterable<FlowEdge> adj(int v) {
    return adj[v];
  }
}
```

- Using _FlowEdge_ we can implement the Ford-Fulkerson algorithm in Java

```java
public class FordFulkerson {
  // True if s -> v path in residual network
  private boolean[] marked;
  // Last edge on s -> v path
  private FlowEdge[] edgeTo;
  // Value of flow
  private double value;

  public FordFulkerson(FlowNetwork G, int s, int t) {
    value = 0.0;
    while (hasAugmentingPath(G, s, t)) {
      double bottle = Double.POSITIVE_INFINITY;
      // Compute bottleneck capacity
      for (int v = t; v != s; v = edgeTo[v].other(v)) {
        bottle = Math.min(bottle, edgeTo[v].residualCapacityTo(v));
      }
      // Augment flow
      for (int v = t; v != s; v = edgeTo[v].other(v)) {
        edgeTo[v].addResidualFlowTo(v, bottle);
      }
      value += bottle;
    }
  }

  private boolean hasAugmentingPath(FlowNetwork G, int s, int t) {
    edgeTo = new FlowEdge[G.V()];
    marked = new boolean[G.V()];

    Queue<Integer> queue = new Queue<Integer>();
    queue.enqueue(s);
    marked[s] = true;
    while (!queue.isEmpty()) {

      int v = queue.dequeue();

      for (FlowEdge e : G.adj(v)) {
        int w = e.other(v);
        // Found path from s to w in the residual network?
        if (e.residualCapacityTo(w) > 0 && !marked[w]) {
          // Sabe last edge on path to w, mark w, and add w to the queue
          edgeTo[w] = e;
          marked[w] = true;
          queue.enqueue(w);
        }
      }
    }
    // Is t reachable from s in residual network
    return marked[t];
  }

  public double value() {
    return value;
  }

  // Is v reachable from s in residual network?
  public boolean inCut(int v) {
    return marked[v];
  }
}
```

### Maxflow applications

- Maxflow/mincut is a widely applicable problem-solving model (see lecture slides for full list)
- In summary:
  - Mincut problem tries to find an _st_-cut of minimum capacity
  - Maxflow problem tries to find an _st_-flow of maximum value
  - Duality is when the value of the maxflow = capacity of mincut
- Proven successful approaches to the maxflow/mincut problem includes:
  - Ford-Fulkerson (various augmenting-path strategies)
  - Preflow-push (various versions)
- Still there remain challenges for this category of problems:
  - Practice: solve real-world maxflow/mincut problems in linear time
  - Theory: prove it for worst-case inputs
  - Conclusion: there is still much to be learned for maxflow/mincut

## Week 3: Radix Sorts

> In this lecture we consider specialized sorting algorithms for strings and related objects. We begin with a subroutine to sort integers in a small range. We then consider two classic radix sorting algorithms—LSD and MSD radix sorts. Next, we consider an especially efficient variant, which is a hybrid of MSD radix sort and quicksort known as 3-way radix quicksort. We conclude with suffix sorting and related applications.

### Strings in Java

- Strings are a sequence of characters
- The `String` data type in Java is immutable (see lecture slides for implementation)
- Underlying implementation is immutable `char[]` array, offset, and length
- Some operations on strings include:
  - **Length** - number of characters
  - **Indexing** - get the _ith_ character
  - **Substring extraction** - get a contiguous subsequence of characters
  - **String concatenation** - append one character to end of another string.
- Unlike `String`, the `StringBuilder` data type is mutable
- Underlying implementation is resizing `char[]` array and length
- For alphabets, strings can be:
  - **Digital key** - sequence of digits over fixed alphabet
  - **Radix** - number of digits _R_ in alphabet

### Key-indexed Counting

- Frequency of operations = key compares
- Some assumptions about keys:
  - Keys are integers between 0 and _R - 1_
  - Can use key as an array index
- Applications of keys extend to:
  - Sort string by first letter
  - Sort class roster by section
  - Sort phone numbers by area code
  - Subroutine in a sorting algorithm
- Note: keys may have associated data which means that we can't just count up number of keys of each value

- For key-indexed counting we want to sort an array `a[]` of _N_ integers between 0 and _R - 1_ by performing the following:

  - Count frequencies of each letter using key as index
  - Compute frequency cumulates which specify destinations
  - Access cumulates using key as index to move items
  - Copy back into original array

- The Java implementation of key-indexed counting is as follows:

```java
int N = a.length;
int[] count = new int[R + 1];

for (int i = 0; i < N; i++) {
  count[a[i] + 1]++;
}

for (int r = 0; r < R; r++) {
  count[r + 1] += count[r];
}

for (int i = 0; i < N; i++) {
  aux[count[a[i]]++] = a[i];
}

for (int i = 0; i < N; i++) {
  a[i] = aux[i];
}
```

- In summary, for key-indexed counting:
  - Key-indexed counting uses ~ _11 _ N + 4 _ R_ array accesses to sort _N_ items whose keys are integers between 0 and _R - 1_
  - Key-indexed counting uses extra space proportional to _N + R_

### LSD Radix Sort

- LSD (least-significant-digit-first string sort) works as follows:
  - Consider characters from right to left
  - Stably sort using _dth_ character as the key (using key-indexed counting)
- LSD sorts fixed-length strings in ascending order

- LSD string sort can be implemented in Java as follows:

```java
public class LSD {
  // Fixed-length W strings
  public static void sort(String[] a, int W) {
    // Radix R
    int R = 256;
    int N = a.length;
    String[] aux = new String[N];

    // Do key-indexed counting for each digit from right to left
    for (int d = W - 1; d >= 0; d--) {
      // Key-indexed counting
      int[] count = new int[R + 1];
      for (int i = 0; i < N; i++) {
        count[a[i].charAt(d) + 1]++;
      }
      for (int r = 0; r < R; r++) {
        count[r + 1] += count[r];
      }
      for (int i = 0; i < N; i++) {
        aux[count[a[i].charAt(d)]++] = a[i];
      }
      for (int i = 0; i < N; i++) {
        a[i] = aux[i];
      }
    }
  }
}
```

- LSD sort serves as the foundation for applications such as:
  - 1890: census used sorting machine developed by Herman Hollerith to automate sorting
  - 1900s-19050s: punch cards were used for data entry, storage, and processing
  - Mainframe
  - Line printer

### MSD Radix Sort

- MSD (most-significant-digit-first string sort) works as follows:

  - Partition array into _R_ pieces according to first character (use key-indexed counting)
  - Recursively sort all strings that start with each character (key-indexed counts delineate sub-arrays to sort)

- MSD string sort can be implemented in Java as follows:

```java
  public static void sort(String[] a) {
    aux = new String[a.length];
    sort(a, aux, 0, a.length-1, 0);
  }

  private static void sort(String[] a, String[] aux, int lo, int hi, int d) {
    if (hi <= lo) {
      return;
    }
    // Key-indexed counting
    int[] count = new int[R + 2];
    for (int i = lo; i <= hi; i++) {
      count[charAt(a[i], d) + 2]++;
    }
    for (int r = 0; r < R + 1; r++) {
      count[r + 1] += count[r];
    }
    for (int i = lo; i <= hi; i++) {
      aux[count[charAt(a[i], d) + 1]++] = a[i];
    }
    for (int i = lo; i <= hi; i++) {
      a[i] = aux[i - lo];
    }

    // Sort R sub-arrays recursively
    for (int r = 0; r < R; r++) {
      sort(a, aux, lo + count[r], lo + count[r+1] - 1, d+1);
    }
  }
```

- MSD has a potential for disastrous performance:

  - Much too slow for small sub-arrays, each function call needs its own `count[]` array
  - Huge number of small sub-arrays because of recursion

- We can cutoff MSD to insertion sort for small sub-arrays:

  - Insertion sort, but start at _dth_ character
  - Implement `less()` so that it compares starting at _dth_ character

- The performance of MSD can be summarized as follows:

  - MSD examines just enough characters to sort the keys
  - Number of characters examined depends on keys
  - Can be sub-linear in input size

- MSD string sort vs quicksort for strings:
  - MSD sort:
    - Extra space for `aux[]`
    - Extra space for `count[]`
    - Inner loop has a lot of instructions
    - Accesses memory "randomly" (cache inefficient)
  - Quicksort:
    - Linearithmic number of string compares (not linear)
    - Has to re-scan many characters in keys with long prefix matches
- We can actually combine the advantages of MSD and quicksort however (stay tuned)

### Three-way Radix Quicksort

- **Goal** - do three-way partitioning on the _dth_ character:

  - Less overhead than _R_-way partitioning in MSD string sort
  - Does not re-examine characters equal to the partitioning char (but does re-examine characters not equal to the partitioning char)

- The three-way string quicksort could be implemented as follows:

```java
private static void sort(String[] a) {
  sort(a, 0, a.length - 1, 0);
}

private static void sort(String[] a, int lo, int hi, int d) {
  // Three-way partitioning (using dth character)
  if (hi <= lo) {
    return;
  }
  int lt = lo, gt = hi;
  int v = charAt(a[lo], d);
  int i = lo + 1;
  while (i <= gt) {
    int t = charAt(a[i], d);
    if (t < v) {
      exch(a, lt++, i++);
    }
    else if (t > v) {
      exch(a, i, gt--);
    }
    else {
      i++;
    }
  }

  sort(a, lo, lt - 1, d);
  if (v >= 0) {
    sort(a, lt, gt, d + 1);
  }
  sort(a, gt + 1, hi, d);
}
```

- Three-way string quicksort versus standard quicksort:

  - Standard quicksort:
    - Uses ~ _2 _ N _ log(N)_ string compares on average
    - Costly for keys with long common prefixes (and this is a common case!)
  - Three-way string quicksort:
    - Uses ~ _2 _ N _ log(N)_ character compares on average for random strings
    - Avoids re-comparing long common prefixes.

- Three-way string quicksort versus MSD string sort:

  - MSD string sort:
    - Is cache-inefficient
    - Too much memory storing `count[]`
    - Too much overhead re-initializing `count[]` and `aux[]`
  - Three-way string quicksort:
    - Has a short inner loop
    - Is cache-friendly
    - Is in-place

- We can conclude that three-way string quicksort is the method of choice for sorting strings

### Suffix Arrays

- **Problem** - given a text of _N_ characters, preprocess it to enable fast substring search
  (find all occurrences of query string context)
- **Applications** - linguistics, databases, web search, word processing, etc.
- One way to solve this problem is to use suffix-sorting algorithm:
  - Preprocess: **suffix sort** the text
  - Query: **binary search** for query; scan until mismatch
- Worst case input for suffix sorting is when longest repeated substring is very long:
  - E.g., same letter repeated _N_ times
  - E.g., two copies of the same Java codebase
- LRS (longest repeated substring) needs at least 1 + 2 + 3 + ... + _D_ character compares, where _D_ = length of longest match
- The run-time of the worst case is quadratic or worse in _D_ for LRS (and also for sort)

- To do suffix sorting in linearithmic time we apply Manber-Myers MSD algorithm:
  - Phase 0: sort on first character using key-indexed counting sort
  - Phase _i_: given array of suffixes sorted on first _2^(i - 1)_ characters, create array of suffixes sorted on first _2^i_ characters
- Worst case run-time for Manber-Myers MSD is _N _ log(N)\*:

  - Finishes after _log(N)_ phases
  - Can perform a phase in linear time however

- We can summarize sting sorts as follows:
  - Linear-time sorts:
    - Key compares not necessary for string keys
    - Use characters as index in an array
  - Sub-linear-time sorts:
    - Input size is amount of data in keys (not number of keys)
    - Not all of the data has to be examined
  - Three-way string quicksort is asymptotically optimal:
    - _1.39 _ N _ log(N)_ chars for random data
  - Long stings are rarely random in practice:
    - Goal is often to learn the structure!
    - May need specialized algorithms

## Week 4: Tries

> In this lecture we consider specialized algorithms for symbol tables with string keys. Our goal is a data structure that is as fast as hashing and even more flexible than binary search trees. We begin with multiway tries; next we consider ternary search tries. Finally, we consider character-based operations, including prefix match and longest prefix, and related applications.

### R-way Tries

- Can we do better than previous symbol table implementations (see lecture for full table on red-black BST and hash tables)?

  - Yes, we can do better than previous symbol table implementations by avoiding examining the entire key, as with string sorting

- An abstraction of the string symbol table API is as follows:

```java
public class StringST<Value> {
  // Create an empty symbol table
  StringST() {}

  // Put key-value pair into the symbol table
  void put(String key, Value val) {}

  // Return value paired with given key
  Value get(String key) {}

  // Delete key and corresponding value
  void delete(String key) {}
}
```

- **Goal** - we want a new symbol table which is faster than hashing and more flexible than BSTs

- The idea behind tries is to:

  - Store characters in nodes (not keys)
  - Each node has _R_ children, one for each possible character
  - For now, we do not draw null links

- Search in a trie occurs by following links corresponding to each character in key:

  - **Search hit**: node where search ends has a non-null value
  - **Search miss**: reach null link or node where search ends has null value

- Insertion into a trie:

  - Encounter a null link: create new node
  - Encounter the last character of the key: set value in that node.

- The implementation for a node in a trie:

```java
private static class Node {
  // Use Object instead of Value since no generic array creation in Java
  private Object value;
  private Node[] next = new Node[R];
}
```

- R-way trie implementation in Java:

```java
public class TrieST<Value> {
  // Extended ASCII
  private static final int R = 256;
  private Node root = new Node();

  private static class Node {
    private Object value;
    private Node[] next = new Node[R];
  }

  public void put(String key, Value val) {
    root = put(root, key, val, 0);
  }

  private Node put(Node x, String key, Value val, int d) {
    if (x == null) {
      x = new Node();
    }
    if (d == key.length()) {
      x.val = val; return x;
    }
    char c = key.charAt(d);
    x.next[c] = put(x.next[c], key, val, d + 1);

    return x;
  }

  public boolean contains(String key) {
    return get(key) != null;
  }

  public Value get(String key) {
    Node x = get(root, key, 0);
    if (x == null) {
      return null;
    }
    // Cast needed
    return (Value) x.val;
  }

  private Node get(Node x, String key, int d) {
    if (x == null) {
      return null;
    }
    if (d == key.length()) {
      return x;
    }
    char c = key.charAt(d);

    return get(x.next[c], key, d+1);
  }
}
```

- Trie performance could be summarized as follows:
  - **Search hit** - Need to examine all _L_ characters for equality
  - **Search miss** - Could have mismatch on first character or - more typically - examine only a few characters (sub-linear)
  - **Space** - _R_ null links at each leaf (but sub-linear space possible if many short strings share common prefixes)
- Bottom line is that tries offer fast search hit and even faster search miss, however they waste space

- To delete in an R-way trie:

  - Find the node corresponding to key and set value to null
  - If node has null value and all null links, remove that node (and recur)

- A R-way trie is the method of choice for small _R_ but takes up too much memory for large _R_

### Ternary Search Tree

- Store characters and values in nodes (not keys)
- Each node has three children: smaller (left), equal (middle), and larger (right)

- Search in a TST (Ternary Search Tree) is done by also following links corresponding to each character in the key:
  - If less, take left link; if greater, take right link
  - If equal, take the middle link and move to the next key character
- **Search hit** - Node where search ends has a non-null value
- **Search miss** - Reach a null link or node where search ends has null value

- The Java representation of a Node for TSTs:

```java
private class Node {
  private Value val;
  private char c;
  private Node left, mid, right;
}
```

- The Java representation of a TST is then:

```java
public class TST<Value> {
  private Node root;
  private class Node {
    private Value val;
    private char c;
    private Node left, mid, right;
  }

  public void put(String key, Value val) {
    root = put(root, key, val, 0);
  }

  private Node put(Node x, String key, Value val, int d) {
    char c = key.charAt(d);
    if (x == null) {
      x = new Node(); x.c = c;
    }
    if (c < x.c) {
      x.left = put(x.left, key, val, d);
    } else if (c > x.c) {
      x.right = put(x.right, key, val, d);
    } else if (d < key.length() - 1) {
      x.mid = put(x.mid, key, val, d + 1);
    } else {
      x.val = val;
    }

    return x;
  }

  public boolean contains(String key) {
    return get(key) != null;
  }

  public Value get(String key) {
    Node x = get(root, key, 0);
    if (x == null) {
      return null;
    }

    return x.val;
  }

  private Node get(Node x, String key, int d) {
    if (x == null) {
      return null;
    }
    char c = key.charAt(d);
    if (c < x.c) {
      return get(x.left, key, d);
    } else if (c > x.c) {
      return get(x.right, key, d);
    } else if (d < key.length() - 1) {
      return get(x.mid, key, d + 1);
    } else {
      return x;
    }
  }
}
```

- We can build balanced TSTs via rotations to achieve _L + log(N)_ worst-case guarantees
- In summary, TST is as fast as hashing (for string keys) and is space efficient

- We can also have a TST with _R^2_ branching at root (hybrid of R-way trie and TST):
  - Do _R^2_-way branching at root
  - Each of _R^2_ root nodes points to a TST
- A hybrid R-way trie with a TST is faster than hashing

- In general for TST versus hashing:
  - **Hashing**:
    - Need to examine entire key
    - Search hits and misses cost about the same
    - Performance relies on hash function
    - Does not support ordered symbol table operations
  - **TSTs**:
    - Works only for strings (or digital keys)
    - Only examines just enough key characters
    - Search miss may involve only a few characters
    - Supports ordered symbol table operations (plus others!)
      **Bottom line**:
    - TSTs are faster than hashing (especially for search misses)
    - More flexible than red-black BSTs

### Character-based Operations

- Character-based operations: the string symbol table API supports several useful character-based operations:

  - Prefix match
  - Wildcard match
  - Longest prefix

- The string symbol table API could then be abstracted as follows:

```java
public class StringST<Value> {
  // Create a symbol table with string keys
  StringST() {}

  // Put key-value pair into the symbol table
  void put(String key, Value val) {}

  // Value paired with key
  Value get(String key) {}

  // Delete key and corresponding value
  void delete(String key) {}

  // All keys
  Iterable<String> keys() {}

  // Keys having s as a prefix
  Iterable<String> keysWithPrefix(String s) {}

  // Keys that match s (where . is a wildcard)
  Iterable<String> keysThatMatch(String s) {}

  // Longest key that is a prefix of s
  String longestPrefixOf(String s) {}
}
```

- We can also add the other ordered ST methods e.g., `floor()` and `rank()`

- For character-based operations, ordered iteration is done as follows:

  - Do in-order traversal of trie; add keys encountered to a queue
  - Maintain sequence of characters on path from root to node

- We can implement this in Java as follows:

```java
public Iterable<String> keys() {
  Queue<String> queue = new Queue<String>();
  collect(root, "", queue);

  return queue;
}

// Sequence of characters on path from root to x
private void collect(Node x, String prefix, Queue<String> q) {
  if (x == null) {
    return;
  }
  if (x.val != null) {
    q.enqueue(prefix);
  }
  for (char c = 0; c < R; c++) {
    collect(x.next[c], prefix + c, q);
  }
}
```

- Prefix matches is the idea of finding all keys in a symbol table starting with a given prefix:

```java
public Iterable<String> keysWithPrefix(String prefix) {
  Queue<String> queue = new Queue<String>();
  // Root of sub-trie for all strings beginning with given prefix
  Node x = get(root, prefix, 0);
  collect(x, prefix, queue);

  return queue;
}
```

- Longest prefix is the idea of finding the longest key in a symbol table that is a prefix of a query string:

  - Search for query string
  - Keep track of longest key encountered

- We can implement the longest prefix as follows:

```java
public String longestPrefixOf(String query) {
  int length = search(root, query, 0, 0);

  return query.substring(0, length);
}

private int search(Node x, String query, int d, int length) {
  if (x == null) {
    return length;
  }
  if (x.val != null) {
    length = d;
  }
  if (d == query.length()) {
    return length;
  }
  char c = query.charAt(d);

  return search(x.next[c], query, d + 1, length);
}
```

- A **Patricia** (Practical Algorithm to Retrieve Information Coded in Alphanumeric) **trie**:
  - Remove one-way branching
  - Each node represents a sequence of characters
  - Implementation: one step beyond this course
- Applications:
  - Database search
  - P2P network search
  - IP routing tables: find longest prefix match
  - Compressed quad-tree for N-body simulation
  - Efficiently storing and querying XML documents
- A Patricia trie is also known as: crit-bit tree or radix tree

- **Suffix tree**:
  - Patricia trie of suffixes of a string
  - Linear-time construction
- Applications:

  - Linear-time: longest repeated substring, longest common substring, longest palindromic substring, substring search, tandem repeats, etc.
  - Computational biology databases (BLAST, FASTA)

- String symbol table summary:
  - **Red-black BST**:
    - Performance guarantee: _log(N)_ key compares
    - Supports ordered symbol table API
  - **Hash tables**:
    - Performance guarantee: constant number of probes
    - Requires good hash function for key type
  - **Tries**:
    - Performance guarantee: _log(N)_ **characters** accessed
    - Supports character-based operations
- **Bottom line** - you can get at anything by examining 50-100 bits

## Week 4: Substring Search

> In this lecture we consider algorithms for searching for a substring in a piece of text. We begin with a brute-force algorithm, whose running time is quadratic in the worst case. Next, we consider the ingenious Knuth–Morris–Pratt algorithm whose running time is guaranteed to be linear in the worst case. Then, we introduce the Boyer–Moore algorithm, whose running time is sublinear on typical inputs. Finally, we consider the Rabin–Karp fingerprint algorithm, which uses hashing in a clever way to solve the substring search and related problems.

### Introduction to Substring Search

- **Goal** - find a pattern of length _M_ in a text of length _N_ (typically _N_ >> _M_)
- Applications of substring search:
  - **Computer forensics** - search memory or disks for signatures
  - **Identify patterns indicative of spam**
  - **Electronic surveillance**
  - **Screen scraping** - extract relevant data from web page

### Brute-force Substring Search

- Check for pattern starting at each text position
- Brute-force algorithm can be slow if text and pattern are repetitive
- Worst-case run-time: _M _ N\* character compares
- Brute-force is not always good enough:
  - **Theoretical challenge** - linear-time guarantee
  - **Practical challenge** - avoid backup in text stream

### Knuth-Morris-Pratt

- A clever method to always avoid backup, here is how it works:
  - Suppose we are searching in text for pattern `BAAAAAAAAA`
  - Suppose we match 5 chars in pattern, with mismatch on 6th char
  - We know previous 6 chars in text are BAAAAB
  - Don't need to back up text pointer!
- DFA (Deterministic Finite State Automaton) is an abstract string-searching machine:
  - Finite number of states (including start and halt)
  - Exactly one transition for each char in alphabet
  - Accept if sequence of transitions leads to halt state
- A key difference between Knuth-Morris-Pratt substring search and brute-force is that:
  - Need to precompute `dfa[][]` from pattern
  - Text pointer `i` never decrements
- DFA on text is at most _N_ character accesses

- We can also add an input stream for DFA:

```java
public int search(In in) {
  int i, j;
  for (i = 0, j = 0; !in.isEmpty() && j < M; i++) {
    // No backup
    j = dfa[in.readChar()][j];
  }
  if (j == M) {
    return i - M;
  } else {
    return NOT_FOUND;
  }
}
```

- Here is how we construct the DFA for KMP (Knuth-Morris-Pratt) substring search:

```java
public KMP(String pat) {
  this.pat = pat;
  M = pat.length();
  dfa = new int[R][M];
  dfa[pat.charAt(0)][0] = 1;
  for (int X = 0, j = 1; j < M; j++) {
    for (int c = 0; c < R; c++) {
      // Copy mismatch cases
      dfa[c][j] = dfa[c][X];
    }
    // Set match case
    dfa[pat.charAt(j)][j] = j + 1;
    // Update restart state
    X = dfa[pat.charAt(j)][X];
  }
}
```

- The run-time of DFA KMP substring search is _M_ character accesses (but space/time proportional to _R _ M\*)

- KMP substring search analysis:
  - KMP substring search accesses no more than _M + N_ chars to search for a pattern of length _M_ in a text of length _N_
  - KMP constructs `dfa[][]` in time and space proportional _R _ M\*
  - Improved version of KMP constructs `nfa[][]` in time and space proportional to _M_

### Boyer-Moore

- **Intuition**:

  - Scan characters in pattern from right to left
  - Can skip as many as _M_ text chars when finding one not in the pattern

- Below is a implementation of Boyer-Moore in Java:

```java
public int search(String txt) {
  int N = txt.length();
  int M = pat.length();
  int skip;
  for (int i = 0; i <= N - M; i += skip) {
    skip = 0;
    for (int j = M - 1; j >= 0; j--) {
      if (pat.charAt(j) != txt.charAt(i + j)) {
        // Use 1 in case other term is not positive and compute skip value
        skip = Math.max(1, j - right[txt.charAt(i + j)]);
        break;
      }
    }
    if (skip == 0) {
      // Match
      return i;
    }
  }
  return N;
}
```

- Run-time analysis of Boyer-Moore:
  - Substring search with the Boyer-Moore mismatched character heuristic takes about ~ _N / M_ character compares to search for a pattern of length _M_ in a text of length _N_
  - Worst-case can be as bad as ~ _M _ N\*
  - Boyer-Moore variant does improve worst case to ~ _3 _ N\* character compares by adding a KMP-like rule to guard against repetitive patterns

### Rabin-Karp

- **Basic idea = modular hashing**:

  - Compute a hash of pattern characters 0 to `M - 1`
  - For each `i`, compute a hash of text characters `i` to`M + i - 1`
  - If pattern hash = text substring hash, check for a match

- Rabin-Karp is implemented as follows:

```java
public class RabinKarp {
  // Pattern hash value
  private long patHash;
  // Pattern length
  private int M;
  // Modulus
  private long Q;
  // Radix
  private int R;
  // R^(M-1) % Q
  private long RM;

  public RabinKarp(String pat) {
    M = pat.length();
    R = 256;
    // A large prime (but avoid overflow)
    Q = longRandomPrime();

    // Precompute (R * RM) % Q
    RM = 1;
    for (int i = 1; i <= M - 1; i++) {
      RM = (R * RM) % Q;
    }
    patHash = hash(pat, M);
  }

  // Horner's linear-time method to evaluate degree-M polynomial
  private long hash(String key, int M) {
    long h = 0;
    for (int j = 0; j < M; j++) {
      h = (R * h + key.charAt(j)) % Q;
    }
    return h;
  }

  // Monte Carlo Search: return match if hash match
  public int search(String txt) {
    int N = txt.length();
    int txtHash = hash(txt, M);
    if (patHash == txtHash) {
      return 0;
    }
    for (int i = M; i < N; i++) {
      txtHash = (txtHash + Q - RM * txt.charAt(i - M) % Q) % Q;
      txtHash = (txtHash * R + txt.charAt(i)) % Q;
      // Las Vegas Monte Carlo: check for substring if hash match, continue search if false collision
      if (patHash == txtHash) {
       return i - M + 1;
      }
    }
    return N;
  }
}
```

- Rabin-Karp analysis:

  - If _Q_ is a sufficiently large random prime (about _M _ N^2*), then the probability of a false collision is about *1 / N\*
  - **Monte Carlo version**:
  - Always runs in linear time
  - Extremely likely to return correct answer (but not always!)
  - **Las Vegas version**:
    - Always returns correct answer
    - Extremely likely to run in linear time (but worst case is _M _ N\*)

- In summary for Rabin-Karp:
  - **Advantages**:
    - Extends to 2D patterns
    - Extends to finding multiple patterns
  - **Disadvantages**:
    - Arithmetic ops slower than character compares
    - Las Vegas version requires backup
    - Poor worst-case guarantee

## Week 5: Regular Expressions

> A regular expression is a method for specifying a set of strings. Our topic for this lecture is the famous grep algorithm that determines whether a given text contains any substring from the set. We examine an efficient implementation that makes use of our digraph reachability implementation from Week 1.

### Regular Expressions

- **Substring search** - find a single string in text
- **Pattern matching** - find one of a **specified set** of strings in text
- Some examples include syntax highlighting and google code search (see lecture slides for more applications)
- A **regular expression** is a notation to specify a set of strings (possibly infinite)

- Writing a RE (regular expression) is like writing a program:
  - Need to understand programming model
  - Can be easier to write than read
  - Can be difficult to debug
- In summary, REs are amazingly powerful and expressive, but using them in applications can be amazingly complex and error-prone

### REs and NFAs

- **RE** - concise way to describe a set of strings
- **DFA** - machine to recognize whether a given string is in a given set

- **Kleene's theorem**:

  - For any DFA, there exists a RE that describes the same set of strings
  - For any RE, there exists a DFA that recognizes the same set of strings

- Initially, pattern matching was attempted:
  - Use KMP, no backup in text input stream
  - Linear-time guarantee
  - Underlying abstraction: DFA (Deterministic Finite-state Automata)
- Basic plan was to apply Kleene's theorem:
  - Build DFA from RE
  - Simulate DFA with text as input
- The problem when implementing Kleene's theorem is DFA may have exponential number of states

- The pattern matching implementation was revised as follows:

  - Build NFA from RE
  - Simulate NFA with text as input

- NFA (Non-deterministic Finite-state Automata) has the following characteristics:
  - RE enclosed in parentheses
  - One state per RE character (start = 0, accept = _M_)
  - Red **ε-transition** (change state, but don't scan text)
  - Black match transition (change state and scan to next text char)
  - Accept if **any** sequence of transitions ends in accept state.
- **Non-determinism** implies the following:

  - One view: machine can guess the proper sequence of state transitions
  - Another view: sequence is a proof that the machine accepts the text

- How to determine whether a string is matched by an automaton?
  - DFA -> easy because exactly one applicable transition
  - NFA -> can be several applicable transitions; need to select the right one!
- How to simulate NFA?
  - Systematically consider **all** possible transition sequences

### NFA Simulation

- How to represent NFA?
  - **State names** - integers from 0 to _M_
  - **Match-transitions** - keep regular expression in array `re[]`
  - **ε-transitions** - store in a **digraph** _G_
- How to efficiently simulate an NFA?
  - Maintain set of **all** possible states that NFA could be after reading in the first _i_ text characters
- How to perform reachability?

  - Check whether input matches pattern
  - Check when there are no more input characters:
    - Accept if any state reachable is an accept state
    - Reject otherwise

- **Digraph reachability** - find all vertices reachable from a given source or **set** of vertices
- **Solution** - run DFS from each source, without un-marking vertices
- **Performance** - runs in time proportional to _E + V_

- Below is an abstraction of how directed DFS could be implemented:

```java
public class DirectedDFS {
  // Find vertices reachable from s
  DirectedDFS(Digraph G, int s) {}

  // Find vertices reachable from sources
  DirectedDFS(Digraph G, Iterable<Integer> s) {}

  // Is v reachable from source(s)?
  boolean marked(int v) {}
}
```

- Therefore we can implement NFA simulation in Java as follows:

```java
public class NFA {
  // Match transitions
  private char[] re;
  // Epsilon transition digraph
  private Digraph G;
  // Number of states
  private int M;

  public NFA(String regexp) {
    M = regexp.length();
    re = regexp.toCharArray();
    G = buildEpsilonTransitionDigraph();
  }

  public boolean recognizes(String txt) {
    // States reachable from start by epsilon transitions
    Bag<Integer> pc = new Bag<Integer>();
    DirectedDFS dfs = new DirectedDFS(G, 0);
    for (int v = 0; v < G.V(); v++) {
      if (dfs.marked(v)) {
        pc.add(v);
      }
    }

    for (int i = 0; i < txt.length(); i++) {
      // States reachable after scanning past txt.charAt(i)
      Bag<Integer> match = new Bag<Integer>();
      for (int v : pc) {
        if (v == M) {
          continue;
        }
        if ((re[v] == txt.charAt(i)) || re[v] == '.') {
          match.add(v + 1);
        }
      }
      // Follow epsilon transitions
      dfs = new DirectedDFS(G, match);
      pc = new Bag<Integer>();
      for (int v = 0; v < G.V(); v++) {
        if (dfs.marked(v)) {
          pc.add(v);
        }
      }
    }
    // Accept if can end in state M
    for (int v : pc) {
      if (v == M) {
        return true;
      }
    }
    return false;
  }

  public Digraph buildEpsilonTransitionDigraph() {
    /* stay tuned */
  }
}
```

- Analysis on NFA simulation shows that determining whether an _N_-character text is recognized by the NFA corresponding to an _M_-character pattern takes time proportional to _M _ N\* in the worst case

### NFA Construction

- To build an NFA corresponding to an RE:
  - Include a state for each symbol in the RE, plus an accept state
  - Add match-transition edge from state corresponding to characters int he alphabet to next state
  - Add _ε_-transition edge from parentheses to next state
  - Add three _ε_-transition edges for each `*` operator or add two _ε_-transition edges for each `|` operator
- To write a program to build the _ε_-transition digraph:

  - Maintain a stack:
    - `(` symbol: push `(` onto stack
    - `|` symbol: push `|` onto stack
    - `)` symbol: pop corresponding `(` and any intervening `|` add _ε_-transition edges for closure/or

- NFA construction is then implemented as follows:

```java
private Digraph buildEpsilonTransitionDigraph() {
  Digraph G = new Digraph(M + 1);
  Stack<Integer> ops = new Stack<Integer>();
  for (int i = 0; i < M; i++) {
    int lp = i;
    // Left parentheses and |
    if (re[i] == '(' || re[i] == '|') {
      ops.push(i);
    }
    // 2-way or
    else if (re[i] == ')') {
      int or = ops.pop();
      if (re[or] == '|') {
        lp = ops.pop();
        G.addEdge(lp, or + 1);
        G.addEdge(or, i);
      }
      else lp = or;
    }
    // Closure (needs 1-character lookahead)
    if (i < M - 1 && re[i + 1] == '*') {
      G.addEdge(lp, i + 1);
      G.addEdge(i + 1, lp);
    }
    // Metasymbols
    if (re[i] == '(' || re[i] == '*' || re[i] == ')')
    G.addEdge(i, i + 1);
  }

  return G;
}
```

- Analysis on NFA construction concludes that building the NFA corresponding to an _M_-character RE takes time and space proportional to _M_

### Regular Expression Applications

- **Grep** - (Generalized Regular Expression Print) takes a RE as a command-line argument and prints the lines from standard input having some substring that is matched by the RE

- An implementation of Grep in Java is shown below:

```java
public class GREP {
  public static void main(String[] args) {
    // Contains RE as a substring
    String re = "(.*" + args[0] + ".*)";
    NFA nfa = new NFA(re);
    while (StdIn.hasNextLine()) {
      String line = StdIn.readLine();
      if (nfa.recognizes(line)) {
        StdOut.println(line);
      }
    }
  }
}
```

- Worst-case run-time for grep (proportional to _M _ N\*) is that same as for brute-force substring search
- See lecture slides for more applications of RE

## Week 5: Data Compression

> We study and implement several classic data compression schemes, including run-length coding, Huffman compression, and LZW compression. We develop efficient implementations from first principles using a Java library for manipulating binary data that we developed for this purpose, based on priority queue and symbol table implementations from earlier lectures.

### Introduction to Data Compression

- Compression reduces the size of a file:

  - To save **space** when storing it
  - To save **time** when transmitting it
  - Most files have lots of redundancy
  - Refer to lecture slides for applications

- **Message** - binary data _B_ we want to compress
- **Compress** - generates a "compressed" representation _C(B)_
- **Expand** - reconstructs original bit-stream _B_
- **Compression ratio** - bits in _C(B)_ / bits in _B_

### Run-length Encoding

- **Simple type of redundancy in a bit-stream** - long runs of repeated bits
- **Representation** - 4-bit counts to represent alternating runs of 0s and 1s
- How many bits to store the counts?
  - We will use 8 bits
- What to do when run length exceeds max count?

  - If longer than 255, intersperse runs of length 0

- Run-length encoding can be implemented in Java as follows (see lecture slides for applications):

```java
public class RunLength {
  // Maximum run-length count
  private final static int R = 256;
  // Number of bits per count
  private final static int lgR = 8;
  public static void compress() {
    /* see textbook */
  }
  public static void expand() {
    boolean bit = false;
    while (!BinaryStdIn.isEmpty()) {
      // Read 8-bit count from standard input
      int run = BinaryStdIn.readInt(lgR);
      for (int i = 0; i < run; i++) {
        // Write 1 bit to standard output
        BinaryStdOut.write(bit);
      }
      bit = !bit;
    }
    // Pad 0s for byte alignment
    BinaryStdOut.close();
  }
}
```

### Huffman Compression

- There exists variable-length codes which use different number of bits to encode different chars (i.e., Morse code)
- In practice, we use a medium gap to separate code-words
- How do we avoid ambiguity?
  - Ensure that no codeword is a **prefix** of another
- How to represent the prefix-free code?

  - A binary trie!
    - Chars in leaves
    - Code-word is path from root to leaf

- Prefix-free codes utilize data compression and expansion as follows:

  - **Compression**:
    - Method 1: start at leaf; follow path up to the root; print bits in reverse
    - Method 2: create ST of key-value pairs
  - **Expansion**:
    - Start at root
    - Go left if bit is 0; go right if 1
    - If leaf node, print char and return to root

- We can look at how data compression and expansion is implemented for prefix-free codes by studying the Huffman trie node data type:

```java
private static class Node implements Comparable<Node> {
  // Used only for leaf nodes
  private final char ch;
  // Used only for compress
  private final int freq;
  private final Node left, right;

  // Initializing constructor
  public Node(char ch, int freq, Node left, Node right) {
    this.ch = ch;
    this.freq = freq;
    this.left = left;
    this.right = right;
  }

  // Is Node a leaf?
  public boolean isLeaf() {
    return left == null && right == null;
  }

  // Compare Nodes by frequency (stay tuned)
  public int compareTo(Node that) {
    return this.freq - that.freq;
  }
}
```

- Expansion is implemented as follows:

```java
public void expand() {
  // Read in encoding trie
  Node root = readTrie();
  // Read in number of chars
  int N = BinaryStdIn.readInt();

  for (int i = 0; i < N; i++) {
    // Expand codeword for ith char
    Node x = root;
    while (!x.isLeaf()) {
      if (!BinaryStdIn.readBoolean()) {
        x = x.left;
      } else {
        x = x.right;
      }
    }
    BinaryStdOut.write(x.ch, 8);
  }
  BinaryStdOut.close();
}
```

- Running time is linear in input size _N_
- How to write the trie?
  - Write preorder traversal of trie; mark leaf and internal nodes with a bit
- How to read in the trie?
  - Reconstruct from preorder traversal of trie
- How to find best prefix-free code?
  - Try the **Shannon-Fano algorithm**:
    - Partition symbols _S_ into two subsets _S_0_ and _S_1_ of (roughly) equal freq
    - Code-words for symbols in _S_0_ start with 0; for symbols in _S_1_ start with 1
    - Recur in _S_0_ and _S_1_
- There are two problems with the Shannon-Fano algorithm however:

  - How to divide up symbols?
  - Not optimal!

- The **Huffman algorithm** can be used to find the best prefix-free code:

  - Count frequency `freq[i]` for each char `i` in input
  - Start with one node corresponding to each char `i` (with weight `freq[i]`)
  - Repeat until single trie formed:
    - Select two tries with min weight `freq[i]` and `freq[j]`
    - Merge into single trie with weight `freq[i] + freq[j]`
  - See lecture slides for applications

- To construct a Huffman encoding trie:

```java
private static Node buildTrie(int[] freq) {
  // Initialize PQ with singleton tries
  MinPQ<Node> pq = new MinPQ<Node>();
  for (char i = 0; i < R; i++) {
    if (freq[i] > 0) {
      pq.insert(new Node(i, freq[i], null, null));
    }
  }

  // Merge two smallest tries
  while (pq.size() > 1) {
    Node x = pq.delMin();
    Node y = pq.delMin();
    Node parent = new Node('\0', x.freq + y.freq, x, y);
    pq.insert(parent);
  }

  return pq.delMin();
}
```

- The Huffman algorithm produces an optimal prefix-free code
- Using a binary heap we can get the run-time to be _N + R _ log(R)* where *N* is the input size and *R\* is the alphabet size
- Can we do better?

### LZW Compression

- Below are the different statistical methods we have:

  - **Static model** - same model for all texts
    - Fast
    - Not optimal: different texts have different statistical properties
    - Ex: ASCII, Morse code
  - **Dynamic model** - generate model based on text
    - Preliminary pass needed to generate model
    - Must transmit the model
    - Ex: Huffman code
  - **Adaptive model** - progressively learn and update model as you read text
    - More accurate modeling produces better compression
    - Decoding must start from beginning
    - Ex: LZW (Lempel-Ziv-Welch Compression)

- **LZW Compression**:

  - Create ST associating _W_-bit code-words with string keys
  - Initialize ST with code-words for single-char keys
  - Find longest string _s_ in ST that is a prefix of un-scanned part of input
  - Write the _W_-bit codeword associated with _s_
  - Add _s_ + _c_ to ST, where _c_ is next char in the input

- How to represent LZW compression code table?

  - A trie to support longest prefix match

- Below is an implementation of LZW compression in Java:

```java
public static void compress() {
  // Read in input as a string
  String input = BinaryStdIn.readString();

  TST<Integer> st = new TST<Integer>();
  // Code-words for single-char, radix R keys
  for (int i = 0; i < R; i++) {
    st.put("" + (char) i, i);
  }
  int code = R + 1;

  while (input.length() > 0) {
    // Find longest prefix match s
    String s = st.longestPrefixOf(input);
    // Write W-bit codeword for s
    BinaryStdOut.write(st.get(s), W);
    int t = s.length();
    if (t < input.length() && code < L) {
      // Add new codeword
      st.put(input.substring(0, t + 1), code++);
    }
    // Scan past s in input
    input = input.substring(t);
  }

  // Write "stop" codeword and close input stream
  BinaryStdOut.write(R, W);
  BinaryStdOut.close();
}
```

- **LZW Expansion**:
  - Create ST associating string values with _W_-bit keys
  - Initialize ST to contain single-char values
  - Read a _W_-bit key
  - Find associated string value in ST and write it out
  - Update ST
- How to represent a LZW expansion code table?

  - An array of size _2^W_

- In summary data compression can have lossless compression:
  - Represent fixed-length symbols with variable-length codes (Huffman)
  - Represent variable-length symbols with fixed-length codes (LZW)

## Week 6: Reductions

> In this lecture our goal is to develop ways to classify problems according to their computational requirements. We introduce the concept of reduction as a technique for studying the relationship among problems. People use reductions to design algorithms, establish lower bounds, and classify problems in terms of their computational requirements.

### Introduction to Reductions

- **Desiderata** - classify **problems** according to computational requirements
- The problem is that a huge number of problem have defied classification
- **Reduction** - problem _X_ **reduces to** problem _Y_ if you can use an algorithm that solves _Y_ to help solve _X_
- Cost of solving _X_ = total cost of solving _Y_ + cost of reduction

### Designing Algorithms

- **Design algorithm** - given algorithm for _Y_, you can also solve _X_ (see lecture slide for list of examples)
- Convex hull reduces to sorting:
  - **Sorting** - given _N_ distinct integers, rearrange them in ascending order
  - **Convex hull** - given N points in the plane, identify the extreme points of the convex hull (in counterclockwise order)
  - Solution: Graham scan algorithm
  - Run-time cost: _N _ log(N) + N\*, second term is reduction
- **Graham scan**:
  - Choose point p with smallest (or largest) y-coordinate
  - **Sort** points by polar angle with p to get simple polygon
  - Consider points in order, and discard those that would create a clockwise turn
- Shortest paths on edge-weighted graphs and digraphs
  - Undirected shortest paths (with non-negative weights) reduces to directed shortest path
  - Solution: replace each undirected edge by two directed edges
  - Run-time cost of undirected shortest paths: _E _ log(V) + E\*
- Shortest paths with negative weights:
  - Reduction is invalid for edge-weighted graphs with negative weights (even if no negative cycles)
  - Can still solve shortest-paths problem in undirected graphs (if no negative cycles), but need more sophisticated techniques
- Linear-time reductions involving familiar problems:
  - See lecture slides for diagram

### Establishing Lower Bounds

- Bird's-eye view:
  - **Goal** - prove that a problem requires a certain number of steps
- Linear-time reductions:
  - Problem _X_ linear-time reduces to problem _Y_ if _X_ can be solved with:
    - Linear number of standard computational steps
    - Constant number of calls to _Y_
  - **Establish lower bound**:
    - If _X_ takes _Ω(N _ log(N))* steps, then so does *Y\*
    - If _X_ takes _Ω(N^2)_ steps, then so does _Y_
  - **Mentality**:
    - If I could easily solve Y, then I could easily solve X
    - I can’t easily solve X
    - Therefore, I can’t easily solve Y
- Lower bound for convex hull:
  - In quadratic decision tree model, any algorithm for sorting _N_ integers requires _Ω(N _ log(N)))\* steps
  - Sorting linear-time reduces to convex hull
  - **Implication** - any ccw-based convex hull algorithm requires _Ω(N _ log(N))\* operations
- Sorting linear-time reduces to convex hull:
  - Region _{ x : x2 ≥ x }_ is convex ⇒ all points are on hull
  - Starting at point with most negative _x_, counterclockwise order of hull points yields integers in ascending order
- In summary, establishing lower bounds through reduction is an important tool in guiding algorithm design efforts
- How to convince yourself no linear-time convex hull algorithm exists?
  - Long futile search for a linear-time algorithm (hard way)
  - Linear-time reduction from sorting (easy way)

### Classifying Problems

- **Desiderata** - problem with algorithm that matches lower bound
- Example: sorting and convex hull have complexity _N _ log(N)\*
- Prove that two problems _X_ and _Y_ have the same complexity:
  - First, show that problem _X_ linear-time reduces to _Y_
  - Second, show that _Y_ linear-time reduces to _X_
  - Conclude that _X_ and _Y_ have the same complexity
- Integer arithmetic reductions:
  - **Integer multiplication** - given two N-bit integers compute their product
  - **Brute force** - _N^2_ bit operations
- Linear algebra reductions:
  - **Matrix multiplication** - given two N-by-N matrices, compute their product
  - **Brute force** - _N^3_ flops
- Bird's-eye review:
  - **Desiderata** - classify problems according to computational requirements:
    - Bad news: huge number of problems have defied classification
    - Good news: can put many problems into equivalence classes
- Complexity zoo:
  - **Complexity class** - set of problems sharing some computational property
  - **Bad news** - lots of complexity classes
- In summary:
  - **Reductions are important in theory to**:
    - Design algorithms
    - Establish lower bounds
    - Classify problems according to their computational requirements
  - **Reductions are important in practice to**:
    - Design algorithms
    - Design reusable software modules
      - Stacks, queues, priority queues, symbol tables, sets, graphs
      - sorting, regular expressions, Delaunay triangulation
      - MST, shortest path, max-flow, linear programming
    - Determine difficulty of your problem and choose the right tool

## Week 6: Linear Programming

> The quintessential problem-solving model is known as linear programming, and the simplex method for solving it is one of the most widely used algorithms. In this lecture, we given an overview of this central topic in operations research and describe its relationship to algorithms that we have considered.

### Introduction to Linear Programming

- Linear programming is a problem-solving model for optimal allocation of scarce resources, among a number of competing activities that encompasses:
  - Shortest paths, maxflow, MST, matching, assignment, ...
  - _A _ x = b\*, 2-person zero-sum games, ...
- Significance of linear programming (see lecture slides for applications):
  - Fast commercial solvers available
  - Widely applicable problem-solving model
  - Key subroutine for integer programming solvers

### Brewer's Problem

- **Scenario** - small brewery produces ale and beer:
  - Production limited by scare resources: corn, hops, barley malt
  - Recipes for ale and beer require different proportions of resources
- **Goal** - choose product mix to maximize profits
- **Linear programming formulation**:
  - Let _A_ be the number of barrels of ale
  - Let _B_ be the number of barrels of beer
- Inequalities define **halfplanes**; feasible region is a **convex polygon** (see lecture slides)
- Optimal solution occurs at an **extreme point**
- Standard form linear program:
  - **Goal** - Maximize linear objective function of _n_ non-negative variables, subject to _m_ linear equations:
    - Input: real numbers _a_ij, c_j, b_i_
    - Output: real numbers _x_j_
- Converting the brewer's problems to the standard form:
  - Add variable _Z_ and equation corresponding to objective function
  - Add **slack** variable to convert each inequality to an equality
  - Now a 6-dimensional problem
- Geometry:
  - Inequalities define **halfspaces**; feasible region is a **convex polyhedron**
  - A set is **convex** if for any two points _a_ and _b_ in the set, so is _(1/2) _ (a + b)\*
  - An extreme point of a set is a point in the set that can't be written as _(1/2) _ (a + b)*, where *a* and *b\* are two distinct points in the set
- **Extreme point property** - if there exists an optimal solution to (P), then there exists one that is an extreme point:
  - Good news: number of extreme points to consider is **finite**
  - Bad news : number of extreme points can be **exponential!**
- **Greed property** - extreme point optimal if and only if no better adjacent extreme point

### Simplex Algorithm

- **Simplex algorithm**:
  - Developed shortly after WWII in response to logistical problems, including Berlin airlift
  - Ranked as one of top 10 scientific algorithms of 20th century
- **Generic algorithm**:
  - Start at some extreme point
  - **Pivot** from one extreme point to an adjacent one
  - Repeat until optimal
- How do we implement either algorithm?
  - Linear algebra
- A **basis** is a subset of _m_ of the _n_ variables
- **Basic feasible solution (BFS)**:
  - Set _n – m_ nonbasic variables to 0, solve for remaining _m_ variables
  - Solve _m_ equations in _m_ unknowns
  - If unique and feasible ⇒ BFS
  - BFS ⇔ extreme point
- When to stop pivoting?
  - When no objective function coefficient is positive
- Why is resulting solution optimal?
  - Any feasible solution satisfies current system of equations

### Simplex Implementations

- **Simplex tableau** - encode standard form LP in single Java 2D array
- Simplex algorithm transforms initial 2D array into solution

- The simplex tableau is constructed as follows:

```java
public class Simplex {
  // Simplex tableaux
  private double[][] a;
  // M constraints, N variables
  private int m, n;

  public Simplex(double[][] A, double[] b, double[] c) {
    m = b.length;
    n = c.length;
    a = new double[m + 1][m + n + 1];
    // Put A[][] into tableau
    for (int i = 0; i < m; i++) {
      for (int j = 0; j < n; j++) {
        a[i][j] = A[i][j];
      }
    }
    // Put I[][] into tableau
    for (int j = n; j < m + n; j++) {
      a[j-n][j] = 1.0;
    }
    // Put c[] into tableau
    for (int j = 0; j < n; j++) {
      a[m][j] = c[j];
    }
    // Put b[] into tableau
    for (int i = 0; i < m; i++) {
      a[i][m+n] = b[i];
    }
  }

  // Find entering column q using Bland's rule
  // Index of first column whose objective function coefficient is positive
  private int bland() {
    for (int q = 0; q < m + n; q++) {
      if (a[M][q] > 0) {
        // Entering column q has positive objective function coefficient
        return q;
      }
    }
    // Optimal
    return -1;
  }

  // Find leaving row p using min ratio rule
  // (Bland's rule: if a tie, choose first such row)
  private int minRatioRule(int q) {
    // Leaving row
    int p = -1;
    for (int i = 0; i < m; i++) {
      // Consider only positive entries
      if (a[i][q] <= 0) {
        continue;
      } else if (p == -1) {
        p = i;
      } else if (a[i][m+n] / a[i][q] < a[p][m+n] / a[p][q]) {
        // Row p has min ratio so far
        p = i;
      }
    }
    return p;
  }

  // Pivot on element row p, column q
  public void pivot(int p, int q) {
    for (int i = 0; i <= m; i++) {
      for (int j = 0; j <= m+n; j++) {
        if (i != p && j != q) {
          // Scale all entries but row p and column q
          a[i][j] -= a[p][j] * a[i][q] / a[p][q];
        }
      }
    }
    for (int i = 0; i <= m; i++) {
      // Zero out column q
      if (i != p) {
        a[i][q] = 0.0;
      }
    }
    for (int j = 0; j <= m+n; j++) {
      // Scale row p
      if (j != q) {
        a[p][j] /= a[p][q];
      }
    }
    a[p][q] = 1.0;
  }

  // Execute the simplex algorithm
  public void solve() {
    while (true) {
      int q = bland();
      // Entering column q (optimal if -1)
      if (q == -1) {
        break;
      }

      int p = minRatioRule(q);
      // Leaving row p (unbounded if -1)
      if (p == -1) {
        // ...
      }

      // Pivot on row p, column q
      pivot(p, q);
    }
  }
}
```

- Property of simplex algorithm: in typical practical applications, simplex algorithm terminates after at most _2 _ (m + n)\* pivots
- **Pivoting rules**:
  - Carefully balance the cost of finding an entering variable with the number of pivots needed
  - No pivot rule is known that is guaranteed to be polynomial
  - Most pivot rules are known to be exponential (or worse) in worst-case
- **Degeneracy** - new basis, same extreme point
- **Cycling**:

  - Get stuck by cycling through different bases that all correspond to same extreme point
  - Doesn't occur in the wild
  - Bland's rule guarantees finite # of pivots

- To improve the bare-bones implementation:

  - Avoid stalling (requires artful engineering)
  - Maintain sparsity (requires fancy data structures)
  - Numerical stability (requires advanced math)
  - Detect infeasibility (run "phase I" simplex algorithm)
  - Detect unboundedness (no leaving row)

- **Best practice** -don't implement it yourself!
- **Basic implementations** - available in many programming environments
- **Industrial-strength solvers** - routinely solve LPs with **millions** of variables
- **Modeling languages** - simplify task of modeling problem as LP

### Linear Programming Reductions

- **Linear “programming” (1950s term) = reduction to LP (modern term)**:
  - Process of formulating an LP model for a problem
  - Solution to LP for a specific problem gives solution to the problem
- Identify **variables**
- Define **constraints** (inequalities and equations)
- Define **objective function**
- Convert to standard form (software usually performs this step automatically)
- There are many examples of models in the lecture slides

- Got an optimization problem?
  - Use a specialized algorithm to solve it
  - Use linear programming
- Is there a universal problem-solving model?
  - **Does P = NP?**
    - No universal problem-solving model exists unless **P = NP**

## Week 6: Intractability

> Is there a universal problem-solving model to which all problems that we would like to solve reduce and for which we know an efficient algorithm? You may be surprised to learn that we do no know the answer to this question. In this lecture we introduce the complexity classes P, NP, and NP-complete, pose the famous P = NP question, and consider implications in the context of algorithms that we have treated in this course.

### Introduction to Intractability

- What is a general-purpose computer?
- Are there limits on the power of digital computers?
- Are there limits on the power of machines we can build?
- Consider the tape model of computation:
  - **Tape**:
    - Stores input
    - One arbitrarily long strip, divided into cells
    - Finite alphabet of symbols
  - **Tape head**:
    - Points to one cell of tape
    - Reads a symbol from active cell
    - Moves one cell at a time
- Now consider Turing machines:
  - **Tape**:
    - Stores input, **output**, and **intermediate results**
    - One arbitrarily long strip, divided into cells
    - Finite alphabet of symbols
  - **Tape head**:
    - Points to one cell of tape
    - Reads a symbol from active cell
    - **Writes** a symbol to active cell
    - Moves one cell at a time
- Is there a more powerful model of computation?
  - No!
- Church-Turing thesis (1936):

  > Turing machines can compute any function that can be computed by a physically harnessable process of the natural world.

- **Implications**:
  - No need to seek more powerful machines or languages
  - Enables rigorous study of computation (in this universe)
- Turing machine is a **simple** and **universal** model of computation

- Which algorithms are useful in practice?
  - Measure running time as a function of input size _N_
  - Useful in practice ("efficient") = polynomial time for all inputs
- **Theory** - definition is broad and robust
- **Practice** - poly-time algorithms scale to huge problems
- Exponential growth dwarfs technological change:

  - Suppose you have a giant parallel computing device…
  - With as many processors as electrons in the universe…
  - And each processor has power of today's supercomputers…
  - And each processor works for the life of the universe…
  - Result: will not help solve 1,000 city TSP problem via brute force (see lecture slide)

- Which problems can we solve in practice?
  - Those with poly-time algorithms
- Which problems have poly-time algorithms?

  - Not so easy to know (see lecture)

- A problem is **intractable** if it can't be solved in polynomial time

### Search Problems

- Four fundamental search problems:
  - **LSOLVE** - given a system of **linear equations**, find a solution
  - **LP** - given a system of **linear inequalities**, find a solution
  - **ILP** - given a system of **linear inequalities**, find a 0-1 solution
  - **SAT** - given a system of **boolean equations**, find a binary solution
- Which of these problems have **poly-time** algorithms?
  - **LSOLVE** - yes, Gaussian elimination solves N-by-N system in N 3 time
  - **LP** - yes, Ellipsoid algorithm is poly-time
  - **ILP**, **SAT** - no poly-time algorithm known or believed to exist!
- **Search problem** - given an instance _I_ of a problem, **find** a solution _S_
- **Requirement** - must be able to efficiently **check** that _S_ is a solution

### P vs NP

- NP is the class of all search problems (see lecture slides for full list)
- **Significance** - what scientists and engineers **aspire to compute** feasibly
- P is the class of all search problems solvable in poly-time
- **Significance** - what scientists and engineers do compute feasibly
- Non-deterministic machine can **guess** the desired solution (see lecture slides for examples)
- Extended Church-Turing thesis:
  > P = search problems solvable in poly-time **in the natural world**.
- **Implication** - To make future computers more efficient, suffices to focus on improving implementation of existing designs
- So does **P = NP**?
  - **If P = NP** - poly-time algorithms for SAT, ILP, TSP, FACTOR, etc.
  - **If P != NP** - would learn something fundamental about our universe

### Classifying Problems

- **SAT** -given a system of boolean equations, find a solution
- **Key applications**:
  - Automatic verification systems for software
  - Electronic design automation (EDA) for hardware
  - Mean field diluted spin glass model in physics
- How to solve an instance of SAT with _n_ variables?
  - Exhaustive search: try all 2^_n_ truth assignments
- Can we do anything substantially more clever?

  - No poly-time algorithm for SAT

- Which search problems are in P?
  - No easy answers (we don't even known whether P = NP)
- Problem X **poly-time reduces** to problem _Y_ if _X_ can be solved with:
  - Polynomial number of standard computational steps
  - Polynomial number of calls to _Y_
- **Consequence** - if SAT poly-time reduces to _Y_, then we conclude that _Y_ is (probably) intractable
- SAT poly-time reduces to ILP (see full reduction tree in lecture slide), if SAT is intractable, a whole set of problems are intractable

### NP-completeness

- An NP problem is **NP-complete** if every problem in NP poly-time reduce to it
- SAT is NP complete (Cook 1971, Levin 1973)
- **Extremely brief proof sketch**:
  - Convert non-deterministic TM notation to SAT notation
  - If you can solve SAT, you can solve any problem in NP
- **Corollary** - poly-time algorithm for SAT if an only if **P = NP**
- **Implications** (see lecture slides for diagram):
  - Poly-time algorithm for SAT iff P = NP
  - No poly-time algorithm for some NP problem ⇒ none for SAT
- Overwhelming consensus: **P != NP**

- In summary:

  - **P** - class of search problems solvable in poly-time
  - **NP** - class of all search problems, some of which seem wickedly hard
  - **NP-complete** - hardest problems in NP (see lecture slides for examples)
  - **Intractable** - problem with no poly-time algorithm

- Use theory as a guide:
  - A poly-time algorithm for an NP-complete problem would be a stunning breakthrough (a proof that P = NP)
  - You will confront NP-complete problems in your career
  - Safe to assume that P ≠ NP and that such problems are intractable
  - Identify these situations and proceed accordingly

### Coping with Intractability

- **Modern cryptography**:
  - Send your credit card to Amazon
  - Digitally sign an e-document
  - Enables freedom of privacy, speech, press, political association
- **RSA crypto-system**:

  - To use: multiply two _n_-bit integers (poly-time)
  - To break: factor a 2 _n_-bit integer (unlikely poly-time)

- **FACTOR** - given an _n_-bit integer _x_, find a non-trivial factor
- What is complexity of FACTOR?
  - In NP, but not known (or believed) to be in P or NP-complete
- What if P = NP?
  - Poly-time algorithm for factoring; modern e-conomy collapses
- Can factor an _n_-bit integer in _n^3_ steps on a "quantum computer" (Shor 1994)
- Do we still believe the extended Church-Turing thesis?

- **Relax one of desired features**:

  - Solve arbitrary instances of the problem
  - Solve the problem to optimality
  - Solve the problem in poly-time

- **Special cases may be tractable**:

  - Ex. linear time algorithm for 2-SAT (at most two variables per equation)
  - Ex. linear time algorithm for Horn-SAT (at most one un-negated variable per equation)

- **Develop a heuristic, and hope it produces a good solution**:

  - No guarantees on quality of solution
  - Ex. TSP assignment heuristics
  - Ex. Metropolis algorithm, simulating annealing, genetic algorithms

- **Complexity theory deals with worst case behavior**:

  - Instance(s) you want to solve may be "easy"
  - Chaff solves real-world SAT instances with ~ 10K variable

- The goal of a Hamilton path is to find a simple path that visits every vertex exactly once
- Euler path is easy but Hamilton path is NP-complete

- Below is an implementation of Hamilton path in Java:

```java
public class HamiltonPath {
  // Vertices on current path
  private boolean[] marked;
  // Number of Hamiltonian paths
  private int count = 0;

  public HamiltonPath(Graph G) {
    marked = new boolean[G.V()];
    for (int v = 0; v < G.V(); v++) {
      dfs(G, v, 1);
    }
  }

  // Where depth is length of current path (depth of recursion)
  private void dfs(Graph G, int v, int depth) {
    marked[v] = true;
    // Found one
    if (depth == G.V()) {
      count++;
    }

    for (int w : G.adj(v)) {
      // Backtrack if w is already part of path
      if (!marked[w]) {
        dfs(G, w, depth + 1);
      }
    }
    // Clean up
    marked[v] = false;
  }
}
```
