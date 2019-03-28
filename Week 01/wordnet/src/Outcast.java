import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    // Initialize WordNet data type
    private WordNet wordNet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        String outcast = null;
        int currMaxDist = 0;
        // Loop over nouns to find outcast
        for (int dist = 0, i = 0; i < nouns.length; dist = 0, i++) {
            // Set temp
            String temp = nouns[i];
            for (String noun : nouns) {
                dist += wordNet.distance(temp, noun);
            }
            // Check if current max distance is less than distance value
            if (currMaxDist < dist) {
                currMaxDist = dist;
                outcast = temp;
            }
        }

        return outcast;
    }

    // Provided in assignment
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
