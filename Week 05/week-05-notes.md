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

## Week 5: Data Compression

> We study and implement several classic data compression schemes, including run-length coding, Huffman compression, and LZW compression. We develop efficient implementations from first principles using a Java library for manipulating binary data that we developed for this purpose, based on priority queue and symbol table implementations from earlier lectures.

### Introduction to Data Compression
* Compression reduces the size of a file:
  * To save **space** when storing it
  * To save **time** when transmitting it
  * Most files have lots of redundancy
  * Refer to lecture slides for applications

* **Message** - binary data *B* we want to compress
* **Compress** - generates a "compressed" representation *C(B)*
* **Expand** - reconstructs original bit-stream *B*
* **Compression ratio** - bits in *C(B)* / bits in *B*

### Run-length Encoding
* **Simple type of redundancy in a bit-stream** - long runs of repeated bits
* **Representation** - 4-bit counts to represent alternating runs of 0s and 1s
* How many bits to store the counts?
  * We will use 8 bits
* What to do when run length exceeds max count?
  * If longer than 255, intersperse runs of length 0

* Run-length encoding can be implemented in Java as follows (see lecture slides for applications):
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
* There exists variable-length codes which use different number of bits to encode different chars (i.e., Morse code)
* In practice, we use a medium gap to separate code-words
* How do we avoid ambiguity?
  * Ensure that no codeword is a **prefix** of another
* How to represent the prefix-free code?
  * A binary trie!
    * Chars in leaves
    * Code-word is path from root to leaf

* Prefix-free codes utilize data compression and expansion as follows:
  * **Compression**:
    * Method 1: start at leaf; follow path up to the root; print bits in reverse
    * Method 2: create ST of key-value pairs
  * **Expansion**:
    * Start at root
    * Go left if bit is 0; go right if 1
    * If leaf node, print char and return to root

* We can look at how data compression and expansion is implemented for prefix-free codes by studying the Huffman trie node data type:
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

* Expansion is implemented as follows:
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

* Running time is linear in input size *N*
* How to write the trie?
  * Write preorder traversal of trie; mark leaf and internal nodes with a bit
* How to read in the trie?
  * Reconstruct from preorder traversal of trie
* How to find best prefix-free code?
  * Try the **Shannon-Fano algorithm**:
    * Partition symbols *S* into two subsets *S_0* and *S_1* of (roughly) equal freq
    * Code-words for symbols in *S_0* start with 0; for symbols in *S_1* start with 1
    * Recur in *S_0* and *S_1*
* There are two problems with the Shannon-Fano algorithm however:
  * How to divide up symbols?
  * Not optimal!

* The **Huffman algorithm** can be used to find the best prefix-free code:
  * Count frequency `freq[i]` for each char `i` in input
  * Start with one node corresponding to each char `i` (with weight `freq[i]`)
  * Repeat until single trie formed:
    * Select two tries with min weight `freq[i]` and `freq[j]`
    * Merge into single trie with weight `freq[i] + freq[j]`
  * See lecture slides for applications

* To construct a Huffman encoding trie:
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

* The Huffman algorithm produces an optimal prefix-free code
* Using a binary heap we can get the run-time to be *N + R * log(R)* where *N* is the input size and *R* is the alphabet size 
* Can we do better?

### LZW Compression
* Below are the different statistical methods we have:
  * **Static model** - same model for all texts
    * Fast
    * Not optimal: different texts have different statistical properties
    * Ex: ASCII, Morse code
  * **Dynamic model** - generate model based on text
    * Preliminary pass needed to generate model
    * Must transmit the model
    * Ex: Huffman code
  * **Adaptive model** - progressively learn and update model as you read text
    * More accurate modeling produces better compression
    * Decoding must start from beginning
    * Ex: LZW (Lempel-Ziv-Welch Compression)

* **LZW Compression**:
  * Create ST associating *W*-bit code-words with string keys
  * Initialize ST with code-words for single-char keys
  * Find longest string *s* in ST that is a prefix of un-scanned part of input
  * Write the *W*-bit codeword associated with *s*
  * Add *s* + *c* to ST, where *c* is next char in the input

* How to represent LZW compression code table?
  * A trie to support longest prefix match

* Below is an implementation of LZW compression in Java:
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

* **LZW Expansion**:
  * Create ST associating string values with *W*-bit keys
  * Initialize ST to contain single-char values
  * Read a *W*-bit key
  * Find associated string value in ST and write it out
  * Update ST
* How to represent a LZW expansion code table?
  * An array of size *2^W*

* In summary data compression can have lossless compression:
  * Represent fixed-length symbols with variable-length codes (Huffman)
  * Represent variable-length symbols with fixed-length codes (LZW)