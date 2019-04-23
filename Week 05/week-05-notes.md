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
