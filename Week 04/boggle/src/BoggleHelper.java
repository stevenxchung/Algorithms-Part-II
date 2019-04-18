public class BoggleHelper {
    // Initialize parameters
    private static final char REF = 'A';
    private Node rootNode = new Node();

    // Define node
    private static class Node {
        // Initialize object store with size of 26
        private Node[] next = new Node[26];
        private boolean lastChar = false;
    }

    // Define helper method to get node value
    private Node getNode(Node node, String s, int inputStrLen) {
        // Check for null node
        if (node == null) {
            return null;
        }
        // Length matches string so return node
        if (inputStrLen == s.length()) {
            return node;
        }
        // Otherwise log character at i from reference
        int j = s.charAt(inputStrLen) - REF;
        // Recursively return node until one of the if gates trigger
        return getNode(node.next[j], s, inputStrLen + 1);
    }

    // Define helper method to append node
    private Node append(Node node, String s, int inputStrLen) {
        // Check for null node
        if (node == null) {
            node = new Node();
        }
        // Length matches string so return node
        if (inputStrLen == s.length()) {
            node.lastChar = true;
            return node;
        }
        // Otherwise log character at i from reference
        int j = s.charAt(inputStrLen) - REF;
        // Recursively set next node until one of the if gates trigger
        node.next[j] = append(node.next[j], s, inputStrLen + 1);
    }

    // Check if prefix exists in the word store
    public boolean prefixExists(String s) {
        Node node = getNode(rootNode, s, 0);

        // Prefix exists
        if (node != null) {
            return true;
        }
        // Otherwise, false
        return false;
    }

    // Check if prefix word in the word store
    public boolean wordExists(String s) {
        Node node = getNode(rootNode, s, 0);

        // Word exists
        if (node != null) {
            return node.lastChar;
        }

        return false;
    }

    // Add word to word store
    public void appendToStore(String s) {
        rootNode = append(rootNode, s, 0);
    }
}
