import edu.princeton.cs.algs4.Picture;

import java.awt.Color;

public class SeamCarver {
    // Initialize parameters
    private boolean flippedY;
    private boolean flippedX;
    private double[][] energyStore;
    private Color[][] colorStore;
    // Height and width of image respectively
    private int h;
    private int w;

    // Helper function to check boundaries
//    private void isInBounds(int[] seam) {
//        // Check height and width
//        if (height() <= 1 || width() <= 1) {
//            throw new java.lang.IllegalArgumentException();
//        }
//        // Check seam length
//        if (seam.length <= 1) {
//            throw new java.lang.IllegalArgumentException();
//        }
//        // Check row index
//        for (int i = 0; i < seam.length - 1; i++) {
//            if (1 < Math.abs(seam[i] - seam[i + 1])) {
//                throw new java.lang.IllegalArgumentException();
//            }
//        }
//    }

    // Helper function to return correct height depending if vertical is transposed
    private int legitHeight() {
        if (flippedY) {
            return width();
        } else {
            return height();
        }
    }

    // Helper function to return correct width depending if vertical is transposed
    private int legitWidth() {
        if (flippedY) {
            return height();
        } else {
            return width();
        }
    }

    // Helper function to compute energy of a pixel
    private void dualGradientEnergy(int a, int b) {
        // Check borders first
        if (a == 0 || a == legitHeight() - 1 || b == 0 || b == legitWidth() - 1) {
            energyStore[a][b] = 1000.0;
        } else {
            // Get values for each red, green, and blue for horizontal and vertical components
            int rX = colorStore[a][b - 1].getRed() - colorStore[a][b + 1].getRed();
            int rY = colorStore[a - 1][b].getRed() - colorStore[a + 1][b].getRed();
            int gX = colorStore[a][b - 1].getGreen() - colorStore[a][b + 1].getGreen();
            int gY = colorStore[a - 1][b].getGreen() - colorStore[a + 1][b].getGreen();
            int bX = colorStore[a][b - 1].getBlue() - colorStore[a][b + 1].getBlue();
            int bY = colorStore[a - 1][b].getBlue() - colorStore[a + 1][b].getBlue();

            // Apply dual-gradient formula given in assignment
            energyStore[a][b] = Math.sqrt((double) (rX * rX + rY * rY + gX * gX + gY * gY + bX * bX + bY * bY));
        }
    }

    // Helper function to revert vertical transpose of an image
    private void revertFlip() {
        flippedY = !flippedY;
        double[][] flippedEnergyStore = new double[legitHeight()][legitWidth()];
        Color[][] flippedColorStore = new Color[legitHeight()][legitWidth()];
        // Loop through each pixel and flip the result
        for (int i = 0; i < legitWidth(); i++) {
            for (int j = 0; j < legitHeight(); j++) {
                flippedColorStore[j][i] = colorStore[i][j];
                flippedEnergyStore[j][i] = energyStore[i][j];
            }
        }
        // Set stores after swap
        colorStore = flippedColorStore;
        energyStore = flippedEnergyStore;
    }

    // Helper function to relax edges, see lecture notes on edge relaxation
    private void relaxEdge(double[][] distTo, int[][] edgeTo, int a, int b) {
        // Check if shortest path by input (a, b) with energy store at (a + 1, b - 1) is less than path length a + 1 to b -1
        if (distTo[a][b] + energyStore[a + 1][b - 1] < distTo[a + 1][b - 1]) {
            edgeTo[a + 1][b - 1] = b;
            distTo[a + 1][b - 1] = distTo[a][b] + energyStore[a + 1][b - 1];
        }
        // Check if shortest path by input (a, b) with energy store at (a + 1, b) is less than path length a + 1 to b
        if (distTo[a][b] + energyStore[a + 1][b] < distTo[a + 1][b]) {
            edgeTo[a + 1][b] = b;
            distTo[a + 1][b] = distTo[a][b] + energyStore[a + 1][b];
        }
        // Check if shortest path by input (a, b) with energy store at (a + 1, b + 1) is less than path length a + 1 to b + 1
        if (distTo[a][b] + energyStore[a + 1][b + 1] < distTo[a + 1][b + 1]) {
            edgeTo[a + 1][b + 1] = b;
            distTo[a + 1][b + 1] = distTo[a][b] + energyStore[a + 1][b + 1];
        }
    }

    // Helper function to clear stores
    private void clearStores() {
        Color[][] tempColorStore = new Color[legitHeight()][legitWidth()];
        double[][] tempEnergyStore = new double[legitHeight()][legitWidth()];
        for (int i = 0; i < legitHeight(); i++) {
            for (int j = 0; j < legitWidth(); j++) {
                tempColorStore[i][j] = colorStore[i][j];
                tempEnergyStore[i][j] = energyStore[i][j];
            }
        }
        colorStore = tempColorStore;
        energyStore = tempEnergyStore;
    }

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        // Create stores and image property values
        colorStore = new Color[picture.height()][picture.width()];
        energyStore = new double[picture.height()][picture.width()];
        h = picture.height();
        w = picture.width();
        // Initialize vertical transpose to false
        flippedY = false;

        // NOTE: Tempting to simplify but need to split this into two loops the way energy is computed
        // Construct color store
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                colorStore[i][j] = picture.get(j, i);
            }
        }

        // Construct energy store
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                dualGradientEnergy(i, j);
            }
        }
    }

    // Current picture
    public Picture picture() {
        Picture tempPicture = new Picture(width(), height());
        // Check if image is transposed vertically
        if (flippedY) {
            // Revert the image
            revertFlip();
        }
        // Loop through each pixel and set colors
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                tempPicture.set(i, j, colorStore[j][i]);
            }
        }

        return tempPicture;
    }

    // Width of current picture
    public int width() {
        return w;
    }

    // Height of current picture
    public int height() {
        return h;
    }

    // Energy of pixel at column x and row y
    public double energy(int x, int y) {
        // Check bounds
        if (x < 0 || x > width() - 1 || y < 0 || y > height() - 1) {
            throw new java.lang.IllegalArgumentException();
        }

        // Only return energy at (x, y) if image is vertically transposed
        if (!flippedY) {
            return energyStore[y][x];
        } else {
            return energyStore[x][y];
        }
    }

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        // First we check if there is any vertical transpose
        if (!flippedY) {
            revertFlip();
        }
        // Set horizontal transpose to true
        flippedX = true;
        // This allows vertical seam to skip the first check and run through
        int[] tempSeam = findVerticalSeam();
        // Now we reset horizontal transpose to false
        flippedX = false;

        return tempSeam;
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        // First we check if there is any transpose and if so, revert
        if (flippedY && !flippedX) {
            revertFlip();
        }
        // Second, we check corresponding widths (depends if image is vertically transposed)
        if (legitWidth() == 1 || legitWidth() == 2) {
            int[] verticalSeam = new int[legitHeight()];
            for (int i = legitHeight() - 1; i >= 0; i--) {
                verticalSeam[i] = 0;
            }
            // We return seam array if the actual width or height of the image is 1 or 2
            return verticalSeam;
        }

        // Initialize method parameters
        double[][] distTo = new double[legitHeight()][legitWidth()];
        double lowerBound = Double.POSITIVE_INFINITY;
        int[][] edgeTo = new int[legitHeight()][legitWidth()];
        int[] verticalSeam = new int[legitHeight()];
        int lowerBoundIndex = 0;

        // Loop through each pixel in image
        for (int i = 0; i < legitHeight(); i++) {
            for (int j = 0; j < legitWidth(); j++) {
                // Two choices for shortest known path
                if (i == 0) {
                    distTo[i][j] = 1000.00;
                } else {
                    distTo[i][j] = Double.POSITIVE_INFINITY;
                }
                // Last edge becomes -1
                edgeTo[i][j] = -1;
            }
        }

        // Next we relax all edges in image
        for (int i = 0; i < legitHeight() - 1; i++) {
            for (int j = 1; j < legitWidth() - 1; j++) {
                relaxEdge(distTo, edgeTo, i, j);
            }
        }

        // Then we check out lower bounds for seam
        for (int i = 0; i < legitWidth(); i++) {
            if (lowerBound > distTo[legitHeight() - 1][i]) {
                lowerBound = distTo[legitHeight() - 1][i];
                lowerBoundIndex = i;
            }
        }

        // We use the lower bound index to determine edges on the vertical seam
        for (int i = legitHeight() - 1; i >= 0; i--) {
            verticalSeam[i] = lowerBoundIndex;
            if (lowerBoundIndex != -1) {
                lowerBoundIndex = edgeTo[i][lowerBoundIndex];
            }
        }

        return verticalSeam;
    }

    // Remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        // Check bounds
        // isInBounds(seam);
        // Very similar to finding the horizontal seam, still check for vertical transpose
        if (!flippedY) {
            revertFlip();
        }
        flippedX = true;
        // Use vertical seam logic to remove horizontal seam
        removeVerticalSeam(seam);
        flippedX = false;
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        // Check bounds
        // isInBounds(seam);
        // First we check if there is any transpose and if so, revert
        if (flippedY && !flippedX) {
            revertFlip();
        }
        // Run through corner cases given in assignment
        // Check for null
        if (seam == null) {
            throw new java.lang.IllegalArgumentException();
        }
        // Check for invalid height
        if (seam.length != legitHeight()) {
            throw new java.lang.IllegalArgumentException();
        }
        // Run through seam length
        for (int i = 0; i < seam.length; i++) {
            // Check seam value against image width or height or if value is less than 0
            if (seam[i] >= legitWidth() || seam[i] < 0) {
                throw new java.lang.IllegalArgumentException();
            }
            // Check if every entry after the first has a difference greater than 1
            if (i != 0 && Math.abs(seam[i] - seam[i - 1]) > 1) {
                throw new java.lang.IllegalArgumentException();
            }
            // If not equal, copy over values from energy and color store
            if (seam[i] != legitWidth() - 1) {
                System.arraycopy(colorStore[i], seam[i] + 1, colorStore[i], seam[i], legitWidth() - seam[i] - 1);
                System.arraycopy(energyStore[i], seam[i] + 1, energyStore[i], seam[i], legitWidth() - seam[i] - 1);
            }
            colorStore[i][legitWidth() - 1] = null;
            energyStore[i][legitWidth() - 1] = -1.0;
        }
        // Now we check for horizontal transpose and then decrement either height or width
        if (flippedX) {
            h--;
        } else {
            w--;
        }

        // Loop through seam and set every depending on the seam value
        for (int i = 0; i < seam.length; i++) {
            if (0 <= seam[i] - 1) {
                dualGradientEnergy(i, seam[i] - 1);
            }
            if (legitWidth() > seam[i]) {
                dualGradientEnergy(i, seam[i]);
            }
        }
        clearStores();
    }
}
