import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    // Initialize parameters
    private boolean flippedY;
    private boolean flippedX;
    private double[][] energyStore;
    private int[][] colorStore;
    // Height and width of image respectively
    private int h;
    private int w;

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
        if (a == 0 || b == 0 || a == legitHeight() - 1 || b == legitWidth() - 1) {
            energyStore[a][b] = 1000.0;
        } else {
            // Get values for each red, green, and blue for horizontal and vertical components
            int rX = ((colorStore[a][b - 1] & (0xff << 16)) >> 16) - ((colorStore[a][b + 1] & (0xff << 16)) >> 16);
            int rY = ((colorStore[a - 1][b] & (0xff << 16)) >> 16) - ((colorStore[a + 1][b] & (0xff << 16)) >> 16);
            int gX = ((colorStore[a][b - 1] & (0xff << 8)) >> 8) - ((colorStore[a][b + 1] & (0xff << 8)) >> 8);
            int gY = ((colorStore[a - 1][b] & (0xff << 8)) >> 8) - ((colorStore[a + 1][b] & (0xff << 8)) >> 8);
            int bX = (colorStore[a][b - 1] & (0xff)) - (colorStore[a][b + 1] & (0xff));
            int bY = (colorStore[a - 1][b] & (0xff)) - (colorStore[a + 1][b] & (0xff));

            // Apply dual-gradient formula given in assignment
            energyStore[a][b] = Math.sqrt((Math.pow(rX, 2) + Math.pow(rY, 2) + Math.pow(gX, 2) + Math.pow(gY, 2) + Math.pow(bX, 2) + Math.pow(bY, 2)));
        }
    }

    // Helper function to revert vertical transpose of an image
    private void revertFlip() {
        flippedY = !flippedY;
        double[][] flippedEnergyStore = new double[legitHeight()][legitWidth()];
        int[][] flippedColorStore = new int[legitHeight()][legitWidth()];
        // Loop through each pixel and flip the result
        for (int i = 0; i < legitWidth(); i++) {
            for (int j = 0; j < legitHeight(); j++) {
                flippedEnergyStore[j][i] = energyStore[i][j];
                flippedColorStore[j][i] = colorStore[i][j];
            }
        }
        // Set stores after swap
        energyStore = flippedEnergyStore;
        colorStore = flippedColorStore;
    }

    // Create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        // Create stores and image property values
        h = picture.height();
        w = picture.width();
        energyStore = new double[h][w];
        colorStore = new int[h][w];
        // Initialize vertical transpose to false
        flippedY = false;

        // Construct energy and color stores
        for (int i = 0; i < width(); i++) {
            for (int j = 0; j < height(); j++) {
                colorStore[i][j] = picture.get(i, j).getRGB();
                // NOTE: May need to split this into two loops the way energy is computed
                dualGradientEnergy(i, j);
            }
        }
    }

    // Current picture
    public Picture picture() {
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
    }

    // Sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
    }

    // Sequence of indices for vertical seam
    public int[] findVerticalSeam() {
    }

    // Remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
    }

    // Remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
    }
}
