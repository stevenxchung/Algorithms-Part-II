import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;

public class BaseballElimination {
    // Initialize parameters
    private static final int W = 0;
    private static final int L = 1;
    private static final int remainingGames = 2;
    private int nTeams;
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
        for (int j = 0, maxPossibleWins = gamesStore[i][W] + gamesStore[i][remainingGames]; j < nTeams; j++) {
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
        int sourceLength = 1 + nTeams + (nTeams - 1) * (nTeams - 2) / 2;
        int sink = teamKeyStore.get(team);
        // Initialize vertices
        FlowNetwork teamNet = new FlowNetwork(sourceLength);
        // Add edges to each vertex that is not the sink
        for (int vertex = 0, totals = gamesStore[sink][W] + gamesStore[sink][remainingGames]; vertex < nTeams; vertex++) {
            if (vertex != sink) {
                // Add a corresponding edge to that vertex
                teamNet.addEdge(new FlowEdge(vertex, sink, totals - gamesStore[vertex][W]));
            }
        }
        int i = nTeams;
        int j = 0;
        int k = j + 1;
        // Add edges until all vertices are connected
        while (i < sourceLength - 1) {
            // Check if k has reached nTeams limit
            if (k == nTeams) {
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
                // Move forward to next iteration
                continue;
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
    private FordFulkerson ffElimination(String team) {
        // Initialize the source and sink
        int source = nTeams + (nTeams - 1) * (nTeams - 2) / 2;
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
        // Read input and assign it to nTeams
        In in = new In(filename);
        nTeams = in.readInt();

        // Initialize parameters
        gamesStore = new int[nTeams][3];
        versusStore = new int[nTeams][nTeams];
        teamKeys = new String[nTeams];
        teamKeyStore = new HashMap<>();

        // Until input is not empty, build games store
        for (int i = 0; !in.isEmpty(); i++) {
            String key = in.readString();
            teamKeys[i] = key;
            teamKeyStore.put(key, i);
            gamesStore[i][W] = in.readInt();
            gamesStore[i][L] = in.readInt();
            gamesStore[i][remainingGames] = in.readInt();
            // Populate versus store and add to combined remaining games
            for (int j = 0; j < nTeams; j++) {
                versusStore[i][j] = in.readInt();
                combinedRemainingGames += versusStore[i][j];
            }
        }
        // We split by two to find true combined remaining games (get rid of redundant counts
        combinedRemainingGames = combinedRemainingGames / 2;
    }

    // Number of teams
    public int numberOfTeams() {
        return nTeams;
    }

    // All teams
    public Iterable<String> teams() {
        // Generate for all teams
        ArrayList<String> teams = new ArrayList<>();
        for (int i = 0; i < nTeams; i++) {
            // Pull from teamKeys array
            teams.add(teamKeys[i]);
        }

        return teams;
    }

    // Number of wins for given team
    public int wins(String team) {
        // Check for invalid teams
        isInvalid(team);

        return gamesStore[teamKeyStore.get(team)][W];
    }

    // Number of losses for given team
    public int losses(String team) {
        // Check for invalid teams
        isInvalid(team);

        return gamesStore[teamKeyStore.get(team)][L];
    }

    // Number of remaining games for given team
    public int remaining(String team) {
        // Check for invalid teams
        isInvalid(team);

        return gamesStore[teamKeyStore.get(team)][remainingGames];
    }

    // Number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        // Check for invalid teams for first input
        isInvalid(team1);
        // Check for invalid teams for second input
        isInvalid(team2);

        return versusStore[teamKeyStore.get(team1)][teamKeyStore.get(team2)];
    }

    // Is given team eliminated?
    public boolean isEliminated(String team) {
        // Check for invalid teams
        isInvalid(team);
        // Check for base elimination case
        if (baseElimination(team) != null) {
            return true;
        }
        // If pass, use FF to determine if team is still running
        FordFulkerson eliminatedTeam = ffElimination(team);

        // Figure out remaining games left for team
        int reaminingGamesForTeam = combinedRemainingGames;
        for (int i = 0; i < nTeams; i++) {
            reaminingGamesForTeam -= versusStore[teamKeyStore.get(team)][i];
        }
        // Check if remaining games are equal to the elimination value from FF
        return reaminingGamesForTeam != eliminatedTeam.value();
    }

    // Subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        // Check for invalid teams
        isInvalid(team);
        // Check for base elimination case and add to array list
        String key = baseElimination(team);
        ArrayList<String> teams = new ArrayList<>();
        if (key != null) {
            teams.add(key);
        } else {
            // Use FF algorithm to determine if team is eliminated
            FordFulkerson eliminatedTeam = ffElimination(team);
            // Work out remaining games to generate a certificate of teams which would
            // eliminate input team
            int remainingGamesForTeam = combinedRemainingGames;
            for (int i = 0; i < nTeams; i++) {
                remainingGamesForTeam -= versusStore[teamKeyStore.get(team)][i];
            }
            // If remaining games is equal to the FF value then return null (team is not eliminated)
            if (remainingGamesForTeam == eliminatedTeam.value()) {
                return null;
            }
            // Otherwise generate list of teams which eliminated the input team
            for (int i = 0; i < nTeams; i++) {
                // Get team from mincut
                if (eliminatedTeam.inCut(i)) {
                    teams.add(teamKeys[i]);
                }
            }
        }
        // Finally, return the list of teams
        return teams;
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
