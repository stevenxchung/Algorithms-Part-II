import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
    // There are 256 ASCII characters
    private static final int ASCIICHARS = 256;

    // Helper function to swap array values
    private static void swap(int i, char c, char[] string) {
        if (i != 0) {
            // Copies an array from the specified source array, beginning at the
            // specified position, to the specified position of the destination array
            System.arraycopy(string, 0, string, 1, i);
        }
        string[0] = c;
    }

    // Helper function to build hex string from 256 characters
    private static char[] buildHexString() {
        char[] string = new char[ASCIICHARS];
        // Loop over length of string and set character
        for (int i = 0; i < string.length; i++) {
            // Cast char and add 0x0 hex
            string[i] = (char) (0x0 + i);
        }
        // Return string of hex characters
        return string;
    }

    // Apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        // Set string to newly built hex characters
        char[] string = buildHexString();

        while (!BinaryStdIn.isEmpty()) {
            // Initialize input and index
            char input = BinaryStdIn.readChar();
            char count = 0;
            // Loop until character matches
            for (int i = 0; i < string.length; i++, count++) {
                // Match found
                if (input == string[i]) {
                    break;
                }
            }
            // Swap string and write
            swap(count, input, string);
            BinaryStdOut.write(count);
        }
        // End input and output stream
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // Apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        // Set string to newly built hex characters
        char[] string = buildHexString();

        while (!BinaryStdIn.isEmpty()) {
            // Initialize input
            char input = BinaryStdIn.readChar();

            // Write and then swap
            BinaryStdOut.write(string[input]);
            swap(input, string[input], string);
        }
        // End input and output stream
        BinaryStdIn.close();
        BinaryStdOut.close();
    }

    // If args[0] is '-', apply move-to-front encoding
    // If args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        // It can either be - or + so continue to the case that matches
        if (args[0].equals("-")) {
            encode();
        } else if (args[0].equals("+")) {
            decode();
        }
    }
}
