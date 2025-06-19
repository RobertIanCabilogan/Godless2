package com.badlogic.Godless;
import java.io.*;
import java.util.*;



public class LeaderboardUtil {
    private static final String FILE_PATH = "leaderboard.txt";

    public static void saveLeaderboard(List<PlayerStats> leaderboard) {
        try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (PlayerStats stats : leaderboard) {
                out.println(stats.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PlayerStats> loadLeaderboard() {
        List<PlayerStats> leaderboard = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PlayerStats stats = PlayerStats.fromString(line);
                if (stats != null) leaderboard.add(stats);
            }
        } catch (IOException e) {
            // Ignore if file doesn't exist yet
        }
        return leaderboard;
    }

    public static void clearLeaderboard() {
        try {
            new PrintWriter(FILE_PATH).close(); // Overwrites with empty file
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

