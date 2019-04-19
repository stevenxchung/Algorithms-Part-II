public class BoggleHelper<Value> {
    // Initialize parameters
    // Switch to int based
    private static final int REF = 26;
    private Node rootNode;
    private int keySize;

    // Define node
    private static class Node {
        // Initialize object store with size of 26
        private Object nodeValue;
        private Node[] nextNode = new Node[REF];
    }

    // Define helper method to get node value
    private Node getNode(Node node, String key, int inputStrLen) {
        // Check for null node
        if (node == null) {
            return null;
        }
        // Length matches string so return node
        if (inputStrLen == key.length()) {
            return node;
        }
        // Otherwise log character at some length from reference
        char stringChar = (char) (key.charAt(inputStrLen) - 'A');
        // Recursively return node until one of the if gates trigger
        return getNode(node.nextNode[stringChar], key, inputStrLen + 1);
    }

    // Define helper method to append node
    private Node append(Node node, String key, Value value, int inputStrLen) {
        // Check for null node
        if (node == null) {
            node = new Node();
        }
        // Length matches string so return node
        if (inputStrLen == key.length()) {
            if (node.nodeValue == null) {
                keySize++;
            }
            node.nodeValue = value;
            return node;
        }
        // Otherwise log character at some length from reference
        char stringChar = (char) (key.charAt(inputStrLen) - 'A');
        // Recursively set next node until one of the if gates trigger
        node.nextNode[stringChar] = append(node.nextNode[stringChar], key, value, inputStrLen + 1);

        return node;
    }

    // Create empty SST (string symbol table)
    public BoggleHelper() {
    }

    // Check key size
    public int getKeySize() {
        return keySize;
    }

    // Check if SST empty
    public boolean isEmpty() {
        return getKeySize() == 0;
    }

    // Check if key prefix is in SST
    public boolean doesKeyPrefixExist(String keyPrefix) {
        // Initialize a node starting at root
        Node node = getNode(rootNode, keyPrefix, 0);

        return !(node == null);
    }

    // Check if key is in SST
    public boolean doesKeyExist(String key) {
        return getValue(key) != null;
    }

    // Returns value of the key based on key-value pair
    public Value getValue(String key) {
        // Initialize a node starting at root
        Node node = getNode(rootNode, key, 0);
        // Check if null
        if (node == null) {
            return null;
        }

        return (Value) node.nodeValue;
    }

    // Insert into SST
    public void appendToSST(String key, Value value) {
        rootNode = append(rootNode, key, value, 0);
    }
}