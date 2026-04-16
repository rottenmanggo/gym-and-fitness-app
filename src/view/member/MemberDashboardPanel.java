package view.member;

import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MemberDashboardPanel extends JPanel {
    public MemberDashboardPanel(User user) {
        setBackground(Theme.BG);
        setLayout(new BorderLayout(20, 20));
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("WELCOME BACK, " + user.getName().toUpperCase());
        title.setForeground(Theme.ACCENT);
        title.setFont(new Font("Monospaced", Font.BOLD, 22));

        JLabel pulse = new JLabel("PULSE: 98%");
        pulse.setForeground(Theme.TEXT);
        pulse.setFont(new Font("SansSerif", Font.BOLD, 54));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(title);
        header.add(Box.createVerticalStrut(10));
        header.add(pulse);

        JPanel cards = new JPanel(new GridLayout(2, 2, 20, 20));
        cards.setOpaque(false);
        cards.add(card("Membership Status", "ACTIVE ELITE"));
        cards.add(card("Workout Recommendation", "Hypertrophy Phase II"));
        cards.add(card("Weekly Hours", "12.5h"));
        cards.add(card("Achievements", "24"));

        add(header, BorderLayout.NORTH);
        add(cards, BorderLayout.CENTER);
    }

    private JPanel card(String title, String value) {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.PANEL);
        panel.setBorder(new EmptyBorder(20,20,20,20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setForeground(Theme.MUTED);
        JLabel lblValue = new JLabel(value);
        lblValue.setForeground(Theme.TEXT);
        lblValue.setFont(new Font("SansSerif", Font.BOLD, 28));
        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(12));
        panel.add(lblValue);
        return panel;
    }
}
