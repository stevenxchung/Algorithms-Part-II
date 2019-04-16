## Week 4: Tries

> In this lecture we consider specialized algorithms for symbol tables with string keys. Our goal is a data structure that is as fast as hashing and even more flexible than binary search trees. We begin with multiway tries; next we consider ternary search tries. Finally, we consider character-based operations, including prefix match and longest prefix, and related applications.

### R-way Tries
* Can we do better than previous symbol table implementations (see lecture for full table on red-black BST and hash tables)?
  * Yes, we can do better than previous symbol table implementations by avoiding examining the entire key, as with string sorting

* An abstraction of the string symbol table API is as follows:
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

* **Goal** - we want a new symbol table which is faster than hashing and more flexible than BSTs

* The idea behind tries is to:
  * Store characters in nodes (not keys)
  * Each node has *R* children, one for each possible character
  * For now, we do not draw null links

* Search in a trie occurs by following links corresponding to each character in key:
  * **Search hit**: node where search ends has a non-null value
  * **Search miss**: reach null link or node where search ends has null value

* Insertion into a trie:
  * Encounter a null link: create new node
  * Encounter the last character of the key: set value in that node.

* The implementation for a node in a trie:
```java
private static class Node {
  // Use Object instead of Value since no generic array creation in Java
  private Object value;
  private Node[] next = new Node[R];
}
```

* R-way trie implementation in Java:
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

* Trie performance could be summarized as follows:
  * **Search hit** - Need to examine all *L* characters for equality
  * **Search miss** - Could have mismatch on first character or - more typically - examine only a few characters (sub-linear)
  * **Space** - *R* null links at each leaf (but sub-linear space possible if many short strings share common prefixes)
* Bottom line is that tries offer fast search hit and even faster search miss, however they waste space

* To delete in an R-way trie:
  * Find the node corresponding to key and set value to null
  * If node has null value and all null links, remove that node (and recur)

* A R-way trie is the method of choice for small *R* but takes up too much memory for large *R*

### Ternary Search Tree
* Store characters and values in nodes (not keys)
* Each node has three children: smaller (left), equal (middle), and larger (right)

* Search in a TST (Ternary Search Tree) is done by also following links corresponding to each character in the key:
  * If less, take left link; if greater, take right link
  * If equal, take the middle link and move to the next key character
* **Search hit** - Node where search ends has a non-null value
* **Search miss** - Reach a null link or node where search ends has null value

* The Java representation of a Node for TSTs:
```java
private class Node {
  private Value val;
  private char c;
  private Node left, mid, right;
}
```

* The Java representation of a TST is then:
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

* We can build balanced TSTs via rotations to achieve *L + log(N)* worst-case guarantees
* In summary, TST is as fast as hashing (for string keys) and is space efficient

* We can also have a TST with *R^2* branching at root (hybrid of R-way trie and TST):
  * Do *R^2*-way branching at root
  * Each of *R^2* root nodes points to a TST
* A hybrid R-way trie with a TST is faster than hashing

* In general for TST versus hashing:
  * **Hashing**:
    * Need to examine entire key
    * Search hits and misses cost about the same
    * Performance relies on hash function
    * Does not support ordered symbol table operations
  * **TSTs**:
    * Works only for strings (or digital keys)
    * Only examines just enough key characters
    * Search miss may involve only a few characters
    * Supports ordered symbol table operations (plus others!)
  **Bottom line**:
    * TSTs are faster than hashing (especially for search misses)
    * More flexible than red-black BSTs

### Character-based Operations
* Character-based operations: the string symbol table API supports several useful character-based operations:
  * Prefix match
  * Wildcard match
  * Longest prefix

* The string symbol table API could then be abstracted as follows:
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

* We can also add the other ordered ST methods e.g., `floor()` and `rank()`

* For character-based operations, ordered iteration is done as follows:
  * Do in-order traversal of trie; add keys encountered to a queue
  * Maintain sequence of characters on path from root to node

* We can implement this in Java as follows:
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

* Prefix matches is the idea of finding all keys in a symbol table starting with a given prefix:
```java
public Iterable<String> keysWithPrefix(String prefix) {
  Queue<String> queue = new Queue<String>();
  // Root of sub-trie for all strings beginning with given prefix
  Node x = get(root, prefix, 0);
  collect(x, prefix, queue);

  return queue;
}
```

* Longest prefix is the idea of finding the longest key in a symbol table that is a prefix of a query string:
  * Search for query string
  * Keep track of longest key encountered

* We can implement the longest prefix as follows:
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

* A **Patricia** (Practical Algorithm to Retrieve Information Coded in Alphanumeric) **trie**:
  * Remove one-way branching
  * Each node represents a sequence of characters
  * Implementation: one step beyond this course
* Applications:
  * Database search
  * P2P network search
  * IP routing tables: find longest prefix match
  * Compressed quad-tree for N-body simulation
  * Efficiently storing and querying XML documents
* A Patricia trie is also known as: crit-bit tree or radix tree

* **Suffix tree**:
  * Patricia trie of suffixes of a string
  * Linear-time construction
* Applications:
  * Linear-time: longest repeated substring, longest common substring, longest palindromic substring, substring search, tandem repeats, etc.
  * Computational biology databases (BLAST, FASTA)

* String symbol table summary:
  * **Red-black BST**:
    * Performance guarantee: *log(N)* key compares
    * Supports ordered symbol table API
  * **Hash tables**:
    * Performance guarantee: constant number of probes
    * Requires good hash function for key type
  * **Tries**:
    * Performance guarantee: *log(N)* **characters** accessed
    * Supports character-based operations
* **Bottom line** - you can get at anything by examining 50-100 bits

## Week 4: Substring Search

> In this lecture we consider algorithms for searching for a substring in a piece of text. We begin with a brute-force algorithm, whose running time is quadratic in the worst case. Next, we consider the ingenious Knuth–Morris–Pratt algorithm whose running time is guaranteed to be linear in the worst case. Then, we introduce the Boyer–Moore algorithm, whose running time is sublinear on typical inputs. Finally, we consider the Rabin–Karp fingerprint algorithm, which uses hashing in a clever way to solve the substring search and related problems.
