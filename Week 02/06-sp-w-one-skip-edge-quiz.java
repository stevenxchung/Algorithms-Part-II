// Shortest path with one skippable edge
// Pseudo-code

public class SPWithOneSkippableEdge {
  // Are weights strictly positive?
  private boolean checkWeightsPositive(Edge e) {}

  // Randomly set an edge to zero
  private int setToZero(int[] P) {}

  // Is P still the shortest path when an edge is set to zero?
  private boolean isSPWithOneSkippableEdge(int[] P, int s, int t) {}

  // Build SPT
  public SPWithOneSkippableEdge(EdgeWeightedDigraph G, int s, int t) {}

  // Tests
  public static void main(String[] args) {}
}
