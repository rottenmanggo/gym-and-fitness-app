package view.member;

import model.User;
import util.Theme;

import javax.swing.*;
import java.awt.*;

public class MemberMainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);

    public MemberMainFrame(User user) {
        setTitle("Gymbrut - Member");
        setSize(1400, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(new MemberDashboardPanel(user), "dashboard");
        contentPanel.add(new WorkoutGuidePanel(), "workouts");
        contentPanel.add(new PlaceholderPanel("Membership Status"), "membership");
        contentPanel.add(new PlaceholderPanel("Payment History"), "payments");
        contentPanel.add(new PlaceholderPanel("Check-In"), "checkin");
        contentPanel.add(new PlaceholderPanel("Progress"), "progress");
        contentPanel.add(new PlaceholderPanel("Profile"), "profile");

        cardLayout.show(contentPanel, "dashboard");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new GridLayout(8, 1, 0, 12));
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(12, 12, 12));
        sidebar.add(menuButton("Dashboard", "dashboard"));
        sidebar.add(menuButton("Workout Guide", "workouts"));
        sidebar.add(menuButton("Membership", "membership"));
        sidebar.add(menuButton("Payments", "payments"));
        sidebar.add(menuButton("Check-In", "checkin"));
        sidebar.add(menuButton("Progress", "progress"));
        sidebar.add(menuButton("Profile", "profile"));
        sidebar.add(menuButton("Logout", "logout"));
        return sidebar;
    }

    private JButton menuButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setBackground(Theme.PANEL);
        btn.setForeground(Theme.TEXT);
        btn.setFocusPainted(false);
        btn.addActionListener(e -> {
            if ("logout".equals(card)) {
                dispose();
                new view.LoginFrame().setVisible(true);
            } else {
                cardLayout.show(contentPanel, card);
            }
        });
        return btn;
    }
}
