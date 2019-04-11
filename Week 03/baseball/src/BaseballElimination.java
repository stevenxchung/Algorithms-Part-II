import java.util.Map;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    // Initialize parameters
    private static int W = 0;
    private static int L = 1;
    private static int remainingGames = 2;
    private int num;
    private int combinedRemainingGames;
    private int[][] gamesStore;
    private int[][] versusStore;
    private String[] teamKeys;
    private Map<String, Integer> teamKeyStore;

    // Helper function to check if one or both of the input arguments are invalid teams
    private void isInvalid(String team) {
        if (teamKeyStore.get(team) == null) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    // Helper function to deduce base elimination case as noted in assignment
    private String baseElimination(String team) {
        int i = teamKeyStore.get(team);
        // As shown in assignment, base elimination case totals up all wins and
        // remaining games from teams and compare to wins of current team
        for (int j = 0, maxPossibleWins = gamesStore[i][W] + gamesStore[i][remainingGames]; j < num; j++) {
            // Compare wins of each team to max possible wins for current team
            if (gamesStore[j][W] > maxPossibleWins) {
                return teamKeys[j];
            }
        }
        // Otherwise, return null
        return null;
    }

    // Helper function to build flow network of edges and vertices for the teams
    private FlowNetwork teamNet(String team) {
        // Initialize the source and sink, the total number of vertices is +1 source in total
        int sourceLength = 1 + num + (num - 1) * (num - 2) / 2;
        int sink = teamKeyStore.get(team);
        // Initialize vertices
        FlowNetwork teamNet = new FlowNetwork(sourceLength);
        // Add edges to each vertex that is not the sink
        for (int vertex = 0, totals = gamesStore[sink][W] + gamesStore[sink][remainingGames]; vertex < num; vertex++) {
            if (vertex != sink) {
                // Add a corresponding edge to that vertex
                teamNet.addEdge(new FlowEdge(vertex, sink, totals - gamesStore[vertex][W]));
            }
        }
        int i = num;
        int j = 0;
        int k = j + 1;
        // Add edges until all vertices are connected
        while (i < sourceLength - 1) {
            // Check if k has reached num limit
            if (k == num) {
                j++;
                k = j + 1;
                // Move forward to next iteration
                continue;
            }
            // Check if j has reached the sink value
            if (j == sink) {
                j++;
                k = j + 1;
                // Move forward to next iteration
                continue;
            }
            // Finally, check if k has reached the sink value
            if (k == sink) {
                k++;
            }
            // Add edges to vertices otherwise
            teamNet.addEdge(new FlowEdge(sourceLength - 1, i, versusStore[j][k]));
            // Based on assignment diagram after initial edges are added
            teamNet.addEdge(new FlowEdge(i, j, Double.POSITIVE_INFINITY));
            teamNet.addEdge(new FlowEdge(i, k++, Double.POSITIVE_INFINITY));

            // Increment
            i++;
        }
        // Return completed team network
        return teamNet;
    }

    // Helper function to use Ford-Fulkerson algorithm and determine eliminated team
    private FordFulkerson FFElimination(String team) {
        // Initialize the source and sink
        int source = num + (num - 1) * (num - 2) / 2;
        int sink = teamKeyStore.get(team);
        // Build flow network of edges and vertices using helper function teamNet()
        FlowNetwork teamNet = teamNet(team);
        // Feed FF the network the source and sink
        FordFulkerson eliminatedTeam = new FordFulkerson(teamNet, source, sink);
        // Return eliminated team
        return eliminatedTeam;
    }

    // Create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
    }

    // Number of teams
    public int numberOfTeams() {
    }

    // All teams
    public Iterable<String> teams() {
    }

    // Number of wins for given team
    public int wins(String team) {
    }

    // Number of losses for given team
    public int losses(String team) {
    }

    // Number of remaining games for given team
    public int remaining(String team) {
    }

    // Number of remaining games between team1 and team2
    public int against(String team1, String team2) {
    }

    // Is given team eliminated?
    public boolean isEliminated(String team) {
    }

    // Subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
    }

    // From assignment
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            } else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
