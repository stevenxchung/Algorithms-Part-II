// Extensions to NFA
// Pseudo-code

public class ExtendedNFA {
  // Match transitions
  private char[] re; 
  // Epsilon transition digraph
  private Digraph G; 
  // Number of states
  private int M; 

  // Helper function to build transition diagraph
  private Digraph buildEpsilonTransitionDigraph() {}
 
  // Helper function to handle multi-way, wildcard, and +
  private Digraph handleExtendedOperators() {}

  // Build extended DFA
  public ExtendedNFA (String regexp) {}

  // Tests
  public static void main(String[] args) {}
}
