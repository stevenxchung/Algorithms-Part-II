import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    // Initialize digraph data type
    private Digraph digraph;

    // Helper function to check for null arguments
    private void isNull(Object arg) {
        if (arg == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // Helper function to check Iterable null arguments
    private void isNullDualArgs(Iterable<Integer> arg1, Iterable<Integer> arg2) {
        if (arg1 == null || arg2 == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // Helper function to check if vertex is out of bounds
    private void isOutOfBounds(int arg) {
        if (arg < 0 || arg > digraph.V()) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // Helper function which checks if BFS has path and if length is greater than temp placeholder
    private int[] checkPaths(BreadthFirstDirectedPaths aBFS, BreadthFirstDirectedPaths bBFS) {
        int ancestor = 0;
        int len = Integer.MAX_VALUE;
        for (int i = 0; i < digraph.V(); i++) {
            if (aBFS.hasPathTo(i) && bBFS.hasPathTo(i)) {
                // Set temp
                int temp = aBFS.distTo(i) + bBFS.distTo(i);
                // Run through check
                if (len > temp) {
                    len = temp;
                    ancestor = i;
                }
            }
        }

        return new int[]{len, ancestor};
    }

    // Invert both array entries if first entry is max
    private int[] inverter(int[] arr) {
        // Check for max
        if (arr[0] == Integer.MAX_VALUE) {
            arr[0] = -1;
            arr[1] = -1;
        }

        return arr;
    }

    // Constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        // Check null
        isNull(G);
        digraph = new Digraph(G);
    }

    // Length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        // Run through boundary checks
        isOutOfBounds(v);
        isOutOfBounds(w);
        // Create new BFS
        BreadthFirstDirectedPaths aBFS = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bBFS = new BreadthFirstDirectedPaths(digraph, w);
        // Return the first value in the array (length)
        return inverter(checkPaths(aBFS, bBFS))[0];
    }

    // A common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        // Run through boundary checks
        isOutOfBounds(v);
        isOutOfBounds(w);
        // Create new BFS
        BreadthFirstDirectedPaths aBFS = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bBFS = new BreadthFirstDirectedPaths(digraph, w);
        // Return the second value in the array (ancestor)
        return inverter(checkPaths(aBFS, bBFS))[1];
    }

    // Length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        // Check null for double args
        isNullDualArgs(v, w);
        // Create new BFS
        BreadthFirstDirectedPaths aBFS = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bBFS = new BreadthFirstDirectedPaths(digraph, w);
        // Return the first value in the array (length)
        return inverter(checkPaths(aBFS, bBFS))[0];
    }

    // A common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        // Check null for double args
        isNullDualArgs(v, w);
        // Create new BFS
        BreadthFirstDirectedPaths aBFS = new BreadthFirstDirectedPaths(digraph, v);
        BreadthFirstDirectedPaths bBFS = new BreadthFirstDirectedPaths(digraph, w);
        // Return the second value in the array (ancestor)
        return inverter(checkPaths(aBFS, bBFS))[1];
    }

    // Do unit testing of this class (provided in assignment)
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
