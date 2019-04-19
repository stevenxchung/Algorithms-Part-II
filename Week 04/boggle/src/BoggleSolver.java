import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BoggleSolver {
    // Bring in helper functions from BoggleHelper (word store) class and create a word store
    private BoggleHelper<Integer> wordStore;
    private BoggleBoard board;

    // Switch to using arrays and hash maps
    private ArrayList<String> wordList;
    private HashMap<String, Integer> wordMap;

    // Helper function to check for word length and return corresponding scores
    private int checkScore(String word) {
        if (word.length() < 3) {
            return 0;
        } else if (word.length() < 5) {
            return 1;
        } else if (word.length() == 5) {
            return 2;
        } else if (word.length() == 6) {
            return 3;
        } else if (word.length() == 7) {
            return 5;
        }
        // Otherwise return 11
        return 11;
    }

    // Helper function to check all panels
    private void checkPanels(int row, int col, int count, String prefix, boolean prefixChecked, boolean[][] traversed) {
        // Has prefix been checked or does the key prefix already exist in the word store?
        if (prefixChecked || wordStore.doesKeyPrefixExist(prefix)) {
            // Now go through all possible adjacent panels
            // Check left panel
            if (col - 1 >= 0 && !traversed[row][col - 1]) {
                depthFirstSearch(row, col - 1, count, prefix, traversed);
            }
            // Check right panel
            if (col + 1 < board.cols() && !traversed[row][col + 1]) {
                depthFirstSearch(row, col + 1, count, prefix, traversed);
            }
            // Rows is trickier, need to go through each column in row
            // Check bottom panel
            if (row - 1 >= 0) {
                // Check left panel in row
                if (col - 1 >= 0 && !traversed[row - 1][col - 1]) {
                    depthFirstSearch(row - 1, col - 1, count, prefix, traversed);
                }
                // Check middle panel in row
                if (!traversed[row - 1][col]) {
                    depthFirstSearch(row - 1, col, count, prefix, traversed);
                }
                // Check right panel in row
                if (col + 1 < board.cols() && !traversed[row - 1][col + 1]) {
                    depthFirstSearch(row - 1, col + 1, count, prefix, traversed);
                }
            }
            // Check top panel
            if (row + 1 < board.rows()) {
                // Check left panel in row
                if (col - 1 >= 0 && !traversed[row + 1][col - 1]) {
                    depthFirstSearch(row + 1, col - 1, count, prefix, traversed);
                }
                // Check middle panel in row
                if (!traversed[row + 1][col]) {
                    depthFirstSearch(row + 1, col, count, prefix, traversed);
                }
                // Check right panel in row
                if (col + 1 < board.cols() && !traversed[row + 1][col + 1]) {
                    depthFirstSearch(row + 1, col + 1, count, prefix, traversed);
                }
            }
        }
    }

    // Helper function to perform DFS
    private void depthFirstSearch(int row, int col, int count, String prefix, boolean[][] traversed) {
        // Find letter using methods from board class
        char character = board.getLetter(row, col);
        // Handle 'Qu' case
        if (character == 'Q') {
            prefix += "QU";
            count += 2;
        } else {
            prefix += character;
            count += 1;
        }

        // If we are here then this panel has been traversed
        traversed[row][col] = true;

        // Boolean to check prefix
        boolean prefixChecked = false;
        if (wordStore.doesKeyExist(prefix)) {
            prefixChecked = true;
            if (count >= 3 && !wordMap.containsKey(prefix)) {
                // Add to map and list
                wordMap.put(prefix, checkScore(prefix));
                wordList.add(prefix);
            }
        }
        // Now check all panels
        checkPanels(row, col, count, prefix, prefixChecked, traversed);

        // If we are here then reset this panel
        traversed[row][col] = false;
    }

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        // Initialize new word store
        wordStore = new BoggleHelper<>();
        for (int i = 0; i < dictionary.length; i++) {
            wordStore.appendToSST(dictionary[i], checkScore(dictionary[i]));
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // Initialize parameters, pull from global
        this.board = board;
        this.wordMap = new HashMap<>();
        this.wordList = new ArrayList<>();
        // Initialize boolean array for traversed panels
        boolean[][] traversed = new boolean[board.rows()][board.cols()];
        // Loop each panel and perform DFS
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                depthFirstSearch(row, col, 0, "", traversed);
            }
        }
        // Finally, return all valid words
        return wordList;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        // Check if word is in store
        if (wordStore.doesKeyExist(word)) {
            return wordStore.getValue(word);
        }
        // Otherwise, return 0
        return 0;
    }

    // Given in assignment
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

}
