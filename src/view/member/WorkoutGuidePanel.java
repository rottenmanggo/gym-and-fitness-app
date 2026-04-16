package view.member;

import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WorkoutGuidePanel extends JPanel {
    public WorkoutGuidePanel() {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("WORKOUT GUIDE");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 44));

        JPanel grid = new JPanel(new GridLayout(2, 2, 20, 20));
        grid.setOpaque(false);
        grid.add(workoutCard("Posterior Chain Obliteration", "Advanced", "75 Min", "950 KCAL"));
        grid.add(workoutCard("Upper Body Crush", "Hypertrophy", "60 Min", "780 KCAL"));
        grid.add(workoutCard("Elite Leg Engine", "Elite", "90 Min", "1,050 KCAL"));
        grid.add(workoutCard("Vascular Engine", "Endurance", "45 Min", "420 KCAL"));

        add(title, BorderLayout.NORTH);
        add(grid, BorderLayout.CENTER);
    }

    private JPanel workoutCard(String name, String level, String duration, String kcal) {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.PANEL);
        panel.setBorder(new EmptyBorder(20,20,20,20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblLevel = new JLabel(level.toUpperCase());
        lblLevel.setForeground(Theme.PRIMARY);
        JLabel lblName = new JLabel("<html>" + name + "</html>");
        lblName.setForeground(Theme.TEXT);
        lblName.setFont(new Font("SansSerif", Font.BOLD, 28));
        JLabel lblInfo = new JLabel(duration + "  •  " + kcal);
        lblInfo.setForeground(Theme.MUTED);
        JButton btn = new JButton("Start Workout");
        btn.setBackground(Theme.PRIMARY);
        btn.setForeground(new Color(0,55,35));
        btn.setFocusPainted(false);

        panel.add(lblLevel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(lblName);
        panel.add(Box.createVerticalStrut(10));
        panel.add(lblInfo);
        panel.add(Box.createVerticalGlue());
        panel.add(btn);
        return panel;
    }
}
