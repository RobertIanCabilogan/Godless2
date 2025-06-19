package com.badlogic.Godless;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class Leaderboard extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;

    public Leaderboard(List<PlayerStats> statslist) {
        statslist.sort((a, b) -> {
            if (b.getKills() != a.getKills()) {
                return Integer.compare(b.getKills(), a.getKills());
            } else {
                return Float.compare(b.getTimeSurvived(), a.getTimeSurvived());
            }
        });

        setTitle("Lost Souls");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 🧾 Set up table model and data
        String[] columns = {"Name", "Kills", "Time"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setRowHeight(28);

        for (PlayerStats stats : statslist) {
            Object[] row = {
                stats.getName(),
                stats.getKills(),
                formatTime(stats.getTimeSurvived())
            };
            tableModel.addRow(row);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // 🔘 Reset button setup
        JButton resetButton = new JButton("Reset Leaderboard");
        resetButton.addActionListener(e -> {
            tableModel.setRowCount(0); // Clear table

            try {
                new PrintWriter("Leaderboard.txt").close(); // Clear file
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            JOptionPane.showMessageDialog(this, "Leaderboard has been reset.", "Reset", JOptionPane.INFORMATION_MESSAGE);
        });

        // 👇 Add button to bottom panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }



    private String formatTime(float seconds) {
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%02d:%02d", minutes, secs);
    }
}
