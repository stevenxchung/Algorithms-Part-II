// Boggle
// Pseudo-code

public class Boggle {
  // Create a symbol table with string keys
  private Boggle() {}

  // Helper function return queue of keys
  private Iterable<String> keys() {}

  // Helper function to build the queue of keys through in-order trie traversal from root to node
  private void collect(Node x, String prefix, Queue<String> q) {}

  // Helper function check if two tiles are adjacent
  private boolean isAdjacent(String s1, String s2) {}

  // Returns all words that can be made by following adjacent tiles
  public String[] boggleComplete(Tiles[] t) {}

  // Tests
  public static void main(String[] args) {}
}
