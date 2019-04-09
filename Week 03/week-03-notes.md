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

### Ford-Fulkerson Algorithm
* **Initialization** - start with 0 flow
* **Augmenting path** - find an undirected path from *s* to *t* such that:
  * Can increase flow on forward edges (not full)
  * Can decrease flow on backward edge (not empty)
* **Termination** - all paths from *s* to *t* are blocked by either a:
  * Full forward edge
  * Empty backward edge

### Maxflow-mincut Theorem
* The **net flow across** a cut (*A*, *B*) is the sum of the flows on its edges from *A* to *B* minus the sum of the flows on its edges from from *B* to *A*
* **Flow-value lemma** - let *f* be any flow and let (*A*, *B*) be any cut. Then, the net
flow across (*A*, *B*) equals the value of *f*
* **Augmenting path theorem** - a flow *f* is a maxflow if and only if no augmenting paths
* **Maxflow-mincut theorem** - value of the maxflow = capacity of mincut
* To compute mincut (*A*, *B*) from maxflow *f*:
  * By augmenting path theorem, no augmenting paths with respect to *f*
  * Compute *A* = set of vertices connected to *s* by an undirected path with no full forward or empty backward edges

### Running Time Analysis
* Several questions emerge for maxflow and mincut:
  * How to compute a mincut? Easy
  * How to find an augmenting path? BFS works well
  * If FF terminates, does it always compute a maxflow? Yes
  * Does FF always terminate? If so, after how many augmentations? Yes, FF (Ford-Fulkerson) always terminates provided edge capacities are integers (or augmenting paths are chosen carefully), augmentations requires clever analysis
* Special case for Ford-Fulkerson algorithm: edge capacities are integers between 1 and *U*
* **Integrality theorem** - There exists an integer-valued maxflow (FF finds one)
* The bad news for Ford-Fulkerson algorithm is that even when edge capacities are integers, number of augmenting paths could be equal to the value of the maxflow (although case is easily avoided)
* In summary, Ford-Fulkerson algorithm depends on choice of augmenting paths:

| augmenting path | number of paths     | implementation   |
| --------------- | ------------------- | ---------------- |
| shortest path   | <= *(1/2) * E * V*  | queue (BFS)      |
| fattest path    | <= *E * log(E * U)* | priority queue   |
| random path     | <= *E * U*          | randomized queue |
| DFS path        | <= *E * U*          | stack (DFS)      |

### Java Implementation
* The flow edge API is abstracted as follows:
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

* Flow edge could then be implemented as:
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

* The flow network API can be abstracted as:
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

* Flow network could then be implemented as:
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

* Using *FlowEdge* we can implement the Ford-Fulkerson algorithm in Java
```java
public class FordFulkerson {
  // True if s->v path in residual network
  private boolean[] marked;
  // Last edge on s->v path
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
* Maxflow/mincut is a widely applicable problem-solving model (see lecture slides for full list)
* In summary:
  * Mincut problem tries to find an *st*-cut of minimum capacity
  * Maxflow problem tries to find an *st*-flow of maximum value
  * Duality is when the value of the maxflow = capacity of mincut
* Proven successful approaches to the maxflow/mincut problem includes:
  * Ford-Fulkerson (various augmenting-path strategies
  * Preflow-push (various versions)
* Still there remain challenges for this category of problems:
  * Practice: solve real-world maxflow/mincut problems in linear time
  * Theory: prove it for worst-case inputs
  * Conclusion: there is still much to be learned for maxflow/mincut

## Week 2: Radix Sorts

> In this lecture we consider specialized sorting algorithms for strings and related objects. We begin with a subroutine to sort integers in a small range. We then consider two classic radix sorting algorithms—LSD and MSD radix sorts. Next, we consider an especially efficient variant, which is a hybrid of MSD radix sort and quicksort known as 3-way radix quicksort. We conclude with suffix sorting and related applications.

### Strings in Java
* Strings are a sequence of characters
* The `String` data type in Java is immutable (see lecture slides for implementation)
* Underlying implementation is immutable `char[]` array, offset, and length
* Some operations on strings include:
  * **Length** - number of characters
  * **Indexing** - get the *ith* character
  * **Substring extraction** - get a contiguous subsequence of characters
  * **String concatenation** - append one character to end of another string.
* Unlike `String`, the `StringBuilder` data type is mutable
* Underlying implementation is resizing `char[]` array and length
* For alphabets, strings can be:
  * **Digital key** - sequence of digits over fixed alphabet
  * **Radix** - number of digits *R* in alphabet

### Key-indexed Counting
* Frequency of operations = key compares
* Some assumptions about keys:
  * Keys are integers between 0 and *R - 1*
  * Can use key as an array index
* Applications of keys extend to:
  * Sort string by first letter
  * Sort class roster by section
  * Sort phone numbers by area code
  * Subroutine in a sorting algorithm
* Note: keys may have associated data which means that we can't just count up number of keys of each value

* For key-indexed counting we want to sort an array `a[]` of *N* integers between 0 and *R - 1* by performing the following:
  * Count frequencies of each letter using key as index
  * Compute frequency cumulates which specify destinations
  * Access cumulates using key as index to move items
  * Copy back into original array

* The Java implementation of key-indexed counting is as follows:
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

* In summary, for key-indexed counting:
  * Key-indexed counting uses ~ *11 * N + 4 * R* array accesses to sort *N* items whose keys are integers between 0 and *R - 1*
  * Key-indexed counting uses extra space proportional to *N + R*

### LSD Radix Sort
* LSD (least-significant-digit-first string sort) works as follows:
  * Consider characters from right to left
  * Stably sort using *dth* character as the key (using key-indexed counting)
* LSD sorts fixed-length strings in ascending order

* LSD string sort can be implemented in Java as follows:
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

* LSD sort serves as the foundation for applications such as:
  * 1890: census used sorting machine developed by Herman Hollerith to automate sorting
  * 1900s-19050s: punch cards were used for data entry, storage, and processing
  * Mainframe
  * Line printer

### MSD Radix Sort
* MSD (most-significant-digit-first string sort) works as follows:
  * Partition array into *R* pieces according to first character (use key-indexed counting)
  * Recursively sort all strings that start with each character (key-indexed counts delineate sub-arrays to sort)

* MSD string sort can be implemented in Java as follows:
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

* MSD has a potential for disastrous performance:
  * Much too slow for small sub-arrays, each function call needs its own `count[]` array
  * Huge number of small sub-arrays because of recursion

* We can cutoff MSD to insertion sort for small sub-arrays:
  * Insertion sort, but start at *dth* character
  * Implement `less()` so that it compares starting at *dth* character

* The performance of MSD can be summarized as follows:
  * MSD examines just enough characters to sort the keys
  * Number of characters examined depends on keys
  * Can be sub-linear in input size

* MSD string sort vs quicksort for strings:
  * MSD sort:
    * Extra space for `aux[]`
    * Extra space for `count[]`
    * Inner loop has a lot of instructions
    * Accesses memory "randomly" (cache inefficient)
  * Quicksort:
    * Linearithmic number of string compares (not linear)
    * Has to re-scan many characters in keys with long prefix matches
* We can actually combine the advantages of MSD and quicksort however (stay tuned)
