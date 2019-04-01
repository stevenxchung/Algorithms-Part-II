import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.TreeMap;

public class WordNet {
    // Initialize parameters and data types
    private ArrayList<String> arrList;
    private TreeMap<String, ArrayList<Integer>> treeMap;
    private Digraph digraph;
    private SAP sap;
    private int V;

    // Helper function to check for null arguments
    private void isNull(Object arg) {
        if (arg == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // Helper function to setup synsets
    private void getSynsets(String arg) {
        // Initialize new class objects and parameters
        In in = new In(arg);
        arrList = new ArrayList<>();
        treeMap = new TreeMap<>();
        V = 0;

        int value;
        ArrayList<Integer> tempTree;
        String[] entries;
        String[] keys;
        String key;

        // Loop over string until null
        for (String a = in.readLine(); a != null; a = in.readLine(), V++) {
            // Get entries by splitting at commas
            entries = a.split(",");
            // Get keys by splitting at spaces at second slot in array
            keys = entries[1].split(" ");
            // Value becomes the first entry
            value = Integer.parseInt(entries[0]);

            // Loop over keys, determine which key is inserted into tree
            for (int i = 0; i < keys.length; i++) {
                key = keys[i];
                // Check if tree contains key
                if (treeMap.containsKey(key)) {
                    tempTree = treeMap.get(key);
                    // Check if value added
                    if (tempTree.add(value)) {
                        treeMap.put(key, tempTree);
                    }
                } else {
                    // Secondary check if tree does not contain key
                    tempTree = new ArrayList<>();
                    if (tempTree.add(value)) {
                        treeMap.put(key, tempTree);
                    }
                }
            }
            // Add keys to array list
            arrList.add(entries[1]);
        }
    }

    // Helper function to setup hypernyms
    private void getHypernyms(String arg) {
        // Initialize new class objects and parameters
        digraph = new Digraph(V);
        In in = new In(arg);
        String[] entries;

        // Similar to before
        for (String a = in.readLine(); a != null; a = in.readLine()) {
            // Get entries by splitting at commas
            entries = a.split(",");
            // Add edges to digraph, convert strings to integers
            for (int i = 1, j = Integer.parseInt(entries[0]); i < entries.length; i++) {
                digraph.addEdge(j, Integer.parseInt(entries[i]));
            }
        }
    }

    // Get distance between two strings
    private int[] getDistance(String arg1, String arg2) {
        // Use max value again, similar to checkPaths() in SAP.java
        int ancestor = 0;
        int temp = 0;
        int len = Integer.MAX_VALUE;

        // Loop through each tree
        for (int i : treeMap.get(arg1)) {
            for (int j : treeMap.get(arg2)) {
                // Set temp
                temp = sap.length(i, j);
                // Run through check
                if (len > temp) {
                    len = temp;
                    ancestor = sap.ancestor(i, j);
                }
            }
        }
        // Return array
        return new int[]{len, ancestor};
    }

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        // Check null
        isNull(synsets);
        isNull(hypernyms);
        // Set up synsets and hypernyms
        getSynsets(synsets);
        getHypernyms(hypernyms);
        // Check if digraph is indeed a DAG
        DirectedCycle dirCycle = new DirectedCycle(digraph);
        if (dirCycle.hasCycle()) {
            throw new java.lang.IllegalArgumentException();
        }
        // Check if input to the constructor does not correspond to a rooted DAG
        for (int i = 0, n = 0; i < V; i++) {
            if (digraph.outdegree(i) == 0) {
                if (n == 0) {
                    n++;
                } else {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }
        // Create a new SAP
        sap = new SAP(digraph);
    }

    // Returns all WordNet nouns
    public Iterable<String> nouns() {
        return treeMap.keySet();
    }

    // Is the word a WordNet noun?
    public boolean isNoun(String word) {
        // Check null
        isNull(word);

        return treeMap.containsKey(word);
    }

    // Distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }

        return getDistance(nounA, nounB)[0];
    }

    // A synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }

        return arrList.get(getDistance(nounA, nounB)[1]);
    }

    // Do unit testing of this class
    public static void main(String[] args) {
    }
}
