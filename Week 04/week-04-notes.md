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
