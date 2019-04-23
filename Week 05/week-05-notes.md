## Week 5: Regular Expressions

> A regular expression is a method for specifying a set of strings. Our topic for this lecture is the famous grep algorithm that determines whether a given text contains any substring from the set. We examine an efficient implementation that makes use of our digraph reachability implementation from Week 1.

### Regular Expressions
* **Substring search** - find a single string in text
* **Pattern matching** - find one of a **specified set** of strings in text
* Some examples include syntax highlighting and google code search (see lecture slides for more applications)
* A **regular expression** is a notation to specify a set of strings (possibly infinite)

* Writing a RE (regular expression) is like writing a program:
  * Need to understand programming model
  * Can be easier to write than read
  * Can be difficult to debug
* In summary, REs are amazingly powerful and expressive, but using them in applications can be amazingly complex and error-prone

### REs and NFAs
* **RE** - concise way to describe a set of strings
* **DFA** - machine to recognize whether a given string is in a given set

* **Kleene's theorem**:
  * For any DFA, there exists a RE that describes the same set of strings
  * For any RE, there exists a DFA that recognizes the same set of strings

* Initially, pattern matching was attempted:
  * Use KMP, no backup in text input stream
  * Linear-time guarantee
  * Underlying abstraction: DFA (Deterministic Finite-state Automata)
* Basic plan was to apply Kleene's theorem:
  * Build DFA from RE
  * Simulate DFA with text as input
* The problem when implementing Kleene's theorem is DFA may have exponential number of states
  
* The pattern matching implementation was revised as follows:
  * Build NFA from RE
  * Simulate NFA with text as input

* NFA (Non-deterministic Finite-state Automata) has the following characteristics:
  * RE enclosed in parentheses
  * One state per RE character (start = 0, accept = *M*)
  * Red **ε-transition** (change state, but don't scan text)
  * Black match transition (change state and scan to next text char)
  * Accept if **any** sequence of transitions ends in accept state.
* **Non-determinism** implies the following:
  * One view: machine can guess the proper sequence of state transitions
  * Another view: sequence is a proof that the machine accepts the text

* How to determine whether a string is matched by an automaton?
  * DFA -> easy because exactly one applicable transition
  * NFA -> can be several applicable transitions; need to select the right one!
* How to simulate NFA?
  * Systematically consider **all** possible transition sequences

### NFA Simulation
* How to represent NFA?
  * **State names** - integers from 0 to *M*
  * **Match-transitions** - keep regular expression in array `re[]`
  * **ε-transitions** - store in a **digraph** *G*
* How to efficiently simulate an NFA?
  * Maintain set of **all** possible states that NFA could be after reading in the first *i* text characters
* How to perform reachability?
  * Check whether input matches pattern
  * Check when there are no more input characters:
    * Accept if any state reachable is an accept state
    * Reject otherwise

* **Digraph reachability** - find all vertices reachable from a given source or **set** of vertices
* **Solution** - run DFS from each source, without un-marking vertices
* **Performance** - runs in time proportional to *E + V*

* Below is an abstraction of how directed DFS could be implemented:
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

* Therefore we can implement NFA simulation in Java as follows:
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

* Analysis on NFA simulation shows that determining whether an *N*-character text is recognized by the NFA corresponding to an *M*-character pattern takes time proportional to *M * N* in the worst case

### NFA Construction
* To build an NFA corresponding to an RE:
  * Include a state for each symbol in the RE, plus an accept state
  * Add match-transition edge from state corresponding to characters int he alphabet to next state
  * Add *ε*-transition edge from parentheses to next state
  * Add three *ε*-transition edges for each `*` operator or add two *ε*-transition edges for each `|` operator
* To write a program to build the *ε*-transition digraph:
  * Maintain a stack:
    * `(` symbol: push `(` onto stack
    * `|` symbol: push `|` onto stack
    * `)` symbol: pop corresponding `(` and any intervening `|` add *ε*-transition edges for closure/or

* NFA construction is then implemented as follows:
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

* Analysis on NFA construction concludes that building the NFA corresponding to an *M*-character RE takes time and space proportional to *M*

### Regular Expression Applications
* **Grep** - (Generalized Regular Expression Print) takes a RE as a command-line argument and prints the lines from standard input having some substring that is matched by the RE

* An implementation of Grep in Java is shown below:
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

* Worst-case run-time for grep (proportional to *M * N*) is that same as for brute-force substring search
* See lecture slides for more applications of RE