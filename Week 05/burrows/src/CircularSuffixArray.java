import edu.princeton.cs.algs4.StdOut;

public class CircularSuffixArray {
    // NOTE: actually not needed
    // Initialize cutoff for insertion sort
    // Part of Quick3string.java from Princeton
//    private static final int CUTOFF = 15;

    // Array size
    private int size;
    // Initialize suffix helper class
    private Quick3StringHelper suffixHelper;

    // Helper function to check for null arguments
    private void isNull(String s) {
        if (s == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // NOTE: not needed
//    // Helper function to check if suffix at position i is less than suffix at position j, start at string offset
//    private boolean isSuffixLess(String s, int stringOffset, int i, int j) {
//        // Initialize two position arrays
//        int a = stringPosition[i];
//        int b = stringPosition[j];
//        for (int k = 0; stringOffset < stringPosition.length; stringOffset++) {
//            // Initialize positions
//            int suffixA = customCharAt(s, a, stringOffset);
//            int suffixB = customCharAt(s, b, stringOffset);
//            // Check if suffix A is less than suffix B
//            if (suffixA < suffixB) {
//                return true;
//            } else if (suffixA > suffixB) {
//                // Might need to change, but will test initial run
//                return false;
//            }
//        }
//        // Return false by default
//        return false;
//    }

    // NOTE: not needed
//    // Helper function to sort from s in range [lo, hi], starting at the string offset
//    // Variation of Quick3string.java from Princeton
//    private void quick3StringInsertion(String s, int lo, int hi, int stringOffset) {
//        // First loop until upper bound is reached
//        for (int i = lo; i <= hi; i++) {
//            // Second loop executes swap until first suffix is greater than the second or j < lo
//            for (int j = i; j > lo && isSuffixLess(s, stringOffset, j, j - 1); j--) {
//                swapPositions(j, j - 1);
//            }
//        }
//    }

    // NOTE: not needed
//    // Helper function which returns the character at some index or position
//    private int customCharAt(String s, int suffixPosition, int stringOffset) {
//        // Check suffix position with offset
//        if (suffixPosition + stringOffset >= 2 * s.length()) {
//            // Cast as char
//            return (char) -1;
//        }
//        // By default, always return the string position at an in-bound position
//        return s.charAt((suffixPosition + stringOffset) % s.length());
//    }

    // Easier to create another class for suffix and then instantiate when building circular suffix array
    private class Quick3StringHelper {
        // Initialize parameters
        private int charStoreLength;
        private int[] positionStore;
        private char[] charStore;

        // Note: moved into private class
        // Helper function implementing a classical swap, here we swap string positions
        private void swapPositions(int i, int j) {
            int temp = positionStore[i];
            positionStore[i] = positionStore[j];
            positionStore[j] = temp;
        }

        // Note: moved into private class
        // 3-way string quicksort from in range [lo, hi] starting at the string offset
        // Variation of Quick3string.java from Princeton
        private void quick3StringSort(int lo, int hi, int stringOffset) {
            // First check to break
            if (hi <= lo) {
                return;
            }
            // Second check to break
            if (stringOffset >= charStoreLength) {
                return;
            }

            // Rest of the sort logic remains the same as before
            // Initialize intermediate boundaries
            int lt = lo, gt = hi;
            int v = charStore[(positionStore[lo] + stringOffset) % charStoreLength];
            int i = lo + 1;
            // When intermediate low is less than or equal to intermediate upper bound
            while (i <= gt) {
                int t = charStore[(positionStore[i] + stringOffset) % charStoreLength];
                if (t < v) {
                    // If character index is less than pivot point
                    swapPositions(lt++, i++);
                } else if (t > v) {
                    // If character index is greater than pivot point
                    swapPositions(i, gt--);
                } else {
                    // Otherwise, increase intermediate lower bound
                    i++;
                }
            }

            // Recursively implement 3-way quicksort
            quick3StringSort(lo, lt - 1, stringOffset);
            if (v >= 0) {
                // If pivot point is greater than or equal to 0
                quick3StringSort(lt, gt, stringOffset + 1);
            }
            quick3StringSort(gt + 1, hi, stringOffset);
        }

        // Build constructor
        public Quick3StringHelper(String s) {
            // Re initialize parameters for suffix helper class
            charStoreLength = s.length();
            this.charStore = s.toCharArray();
            this.positionStore = new int[charStoreLength];
            // Loop through length of string and reset position store
            for (int i = 0; i < charStoreLength; i++) {
                positionStore[i] = i;
            }
            // By default, implement 3-way string quicksort
            quick3StringSort(0, charStoreLength - 1, 0);
        }

        // Return ith suffix index
        public int suffixIndex(int i) {
            if (i < 0 || i >= charStoreLength) {
                throw new java.lang.IllegalArgumentException();
            }
            // By default, return suffix index
            return positionStore[i];
        }
    }

    // Circular suffix array of s
    public CircularSuffixArray(String s) {
        // Check for null arguments
        isNull(s);
        // Initialize position array to string length
        size = s.length();
        // Use suffix helper to build circular suffix array
        suffixHelper = new Quick3StringHelper(s);

        // Note: refactored in suffix helper class
//        // Loop for the length of s and add to string position array
//        for (int i = 0; i < s.length(); i++) {
//            stringPosition[i] = i;
//        }
//        // Initial sort at lower bound of zero and offset of zero
//        quick3StringSort(s, 0, s.length() - 1, 0);
    }

    // Length of s
    public int length() {
        return size;
    }

    // Returns index of ith sorted suffix
    public int index(int i) {
        // Check if index out of bounds
        if (i > size - 1) {
            throw new java.lang.IllegalArgumentException();
        }
        return suffixHelper.suffixIndex(i);
    }

    // Unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray obj = new CircularSuffixArray("ABRACADABRA!");
        StdOut.println("Length of s: " + obj.length());
        for (int i = 0; i < obj.length(); i++) {
            StdOut.println("Index of ith sorted suffix: " + obj.index(i));
        }
    }
}
