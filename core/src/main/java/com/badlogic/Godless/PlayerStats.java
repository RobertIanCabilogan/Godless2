package com.badlogic.Godless;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PlayerStats{
    private String name;
    private int kills;
    private float timeSurvived;

    public PlayerStats(String name, int kills, float timeSurvived){
        this.name = name;
        this.kills = kills;
        this.timeSurvived = timeSurvived;
    }
    @Override
    public String toString() {
        return name + "," + kills + "," + timeSurvived;
    }

    public static PlayerStats fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length != 3) return null;
        return new PlayerStats(parts[0], Integer.parseInt(parts[1]), Float.parseFloat(parts[2]));
    }

    public static void saveLeaderboard(List<PlayerStats> leaderboard) {
        try (PrintWriter out = new PrintWriter(new FileWriter("leaderboard.txt"))) {
            for (PlayerStats stats : leaderboard) {
                out.println(stats.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PlayerStats> loadLeaderboard() {
        List<PlayerStats> leaderboard = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("leaderboard.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                PlayerStats stats = PlayerStats.fromString(line);
                if (stats != null) leaderboard.add(stats);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return leaderboard;
    }




    public String getName(){
        return name;
    }

    public int getKills(){
        return kills;
    }
    public float getTimeSurvived(){
        return timeSurvived;
    }
}
