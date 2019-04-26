import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {
    // Refactor from last submission
    // Initialize ASCII characters
    private static final int ASCIICHARS = 256;

    // Helper function to sort character array
    private static void charSort(char[] charStore, int[] temp) {
        // Initialize character sort parameters
        int charStoreLength = charStore.length;
        int[] positionStore = new int[ASCIICHARS + 1];
        char[] tempCharStore = new char[charStoreLength];

        // Fill position store with each character position but offset by one
        for (int i = 0; i < charStoreLength; i++) {
            positionStore[charStore[i] + 1]++;
        }

        // Add index values to tempStore based on the ith ASCII character
        for (int aChar = 0; aChar < ASCIICHARS; aChar++) {
            positionStore[aChar + 1] += positionStore[aChar];
        }

        // Move positions based on the jth character in the character store
        for (int j = 0; j < charStoreLength; j++) {
            temp[positionStore[charStore[j]]] = j;
            tempCharStore[positionStore[charStore[j]]++] = charStore[j];
        }

        // Set character store to match temporary store
        for (int k = 0; k < charStoreLength; k++) {
            charStore[k] = tempCharStore[k];
        }

    }

    // Apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        // Initialize input string
        String inputString = BinaryStdIn.readString();

        // Initialize string parameters
        char[] encodedOutputs = inputString.toCharArray();

        // Initialize circular suffix array object
        CircularSuffixArray obj = new CircularSuffixArray(inputString);

        // Loop through length of string and write headers
        for (int i = 0; i < inputString.length(); i++) {
            // Write header positions if suffix index is at index 0
            if (obj.index(i) == 0) {
                BinaryStdOut.write(i);
            }
        }

        // Use this value to separately track length
        int trackedStrLength = inputString.length();
        // Loop through length of string and add encoded outputs
        for (int i = 0; i < inputString.length(); i++) {
            // Write encoded binaries
            int suffixIndex = obj.index(i);
            BinaryStdOut.write(encodedOutputs[(suffixIndex - 1 + trackedStrLength) % trackedStrLength]);
        }

        // Close read and writes
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // Apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        // Similar to encoding process
        // Initialize input integer
        int singularHead = BinaryStdIn.readInt();
        // Initialize input string
        String inputString = BinaryStdIn.readString();

        // Initialize string parameters
        char[] decodedOutputs = inputString.toCharArray();
        char[] header = inputString.toCharArray();
        int charStoreLength = decodedOutputs.length;
        int[] positionStore = new int[charStoreLength];

        // NOTE: this was a bad idea
        // A 16-bit code unit in the range D800(base 16) to DBFF(base 16)
        // used in UTF-16 as the leading code unit of a surrogate pair
        // Use this to check the header array
//        char max = Character.MAX_HIGH_SURROGATE;

        // NOTE: also a bad idea lol
        // Use array lists to track characters
//        ArrayList<Integer>[] charStore = new ArrayList[ASCIICHARS];

        // Sort header following position store
        charSort(header, positionStore);

        // Loop through length of string and write headers
        for (int i = 0; i < charStoreLength; i++) {
            BinaryStdOut.write(header[singularHead]);
            // Reset head
            singularHead = positionStore[singularHead];
        }

        // Close read and writes
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // If args[0] is '-', apply Burrows-Wheeler transform
    // If args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        // It can either be - or + so continue to the case that matches
        if (args[0].equals("-")) {
            transform();
        } else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
