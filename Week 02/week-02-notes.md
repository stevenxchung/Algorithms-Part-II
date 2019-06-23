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
