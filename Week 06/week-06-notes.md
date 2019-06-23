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
