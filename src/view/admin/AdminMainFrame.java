package view.admin;

import model.User;
import util.Theme;

import javax.swing.*;
import java.awt.*;

public class AdminMainFrame extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final User user;

    public AdminMainFrame(User user) {
        this.user = user;
        setTitle("Gymbrut - Admin");
        setSize(1400, 860);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        contentPanel.add(new AdminDashboardPanel(user), "dashboard");
        contentPanel.add(new ManageMembersPanel(), "members");
        contentPanel.add(new PlaceholderPanel("Manage Membership"), "membership");
        contentPanel.add(new PlaceholderPanel("Manage Payments"), "payments");
        contentPanel.add(new PlaceholderPanel("Manage Workout"), "workouts");
        contentPanel.add(new PlaceholderPanel("Reports"), "reports");

        cardLayout.show(contentPanel, "dashboard");
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(new Color(12, 12, 12));
        sidebar.setLayout(new GridLayout(8, 1, 0, 12));

        sidebar.add(menuButton("Dashboard", "dashboard"));
        sidebar.add(menuButton("Members", "members"));
        sidebar.add(menuButton("Membership", "membership"));
        sidebar.add(menuButton("Payments", "payments"));
        sidebar.add(menuButton("Workout", "workouts"));
        sidebar.add(menuButton("Reports", "reports"));
        sidebar.add(new JLabel());
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
