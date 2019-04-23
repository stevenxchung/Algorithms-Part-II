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
