package view.admin;

import model.User;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AdminDashboardPanel extends JPanel {
    public AdminDashboardPanel(User user) {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        add(buildHeader(user), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
    }

    private JPanel buildHeader(User user) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("ANALYTICS VAULT");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 44));

        JLabel subtitle = new JLabel("Real-time performance metrics and member engagement tracking for Gymbrut.");
        subtitle.setForeground(Theme.MUTED);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(title);
        left.add(subtitle);

        JLabel admin = new JLabel("Admin: " + user.getName());
        admin.setForeground(Theme.PRIMARY);
        admin.setFont(Theme.FONT_BOLD_18);

        panel.add(left, BorderLayout.WEST);
        panel.add(admin, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildCenter() {
        JPanel root = new JPanel(new BorderLayout(20, 20));
        root.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 5, 15, 15));
        stats.setOpaque(false);
        stats.add(statCard("Active Members", "1,248"));
        stats.add(statCard("Expired Plans", "42"));
        stats.add(statCard("Pending Payments", "15"));
        stats.add(statCard("Total Revenue", "$84.2k"));
        stats.add(statCard("Today's Attendance", "312"));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 20, 20));
        bottom.setOpaque(false);
        bottom.add(chartPlaceholder());
        bottom.add(transactionsTable());

        root.add(stats, BorderLayout.NORTH);
        root.add(bottom, BorderLayout.CENTER);
        return root;
    }

    private JPanel statCard(String label, String value) {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.PANEL);
        panel.setBorder(new EmptyBorder(18, 18, 18, 18));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setForeground(Theme.MUTED);
        JLabel val = new JLabel(value);
        val.setForeground(Theme.TEXT);
        val.setFont(new Font("SansSerif", Font.BOLD, 34));
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(15));
        panel.add(val);
        return panel;
    }

    private JPanel chartPlaceholder() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Theme.PANEL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        JLabel lbl = new JLabel("Monthly Growth Trend");
        lbl.setForeground(Theme.TEXT);
        lbl.setFont(Theme.FONT_BOLD_18);
        JTextArea area = new JTextArea("Chart area placeholder.\nNanti bisa diganti dengan JFreeChart untuk revenue/member growth.");
        area.setEditable(false);
        area.setBackground(Theme.PANEL);
        area.setForeground(Theme.MUTED);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(area, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane transactionsTable() {
        String[] cols = {"Member", "Plan", "Amount", "Date", "Status"};
        Object[][] rows = {
                {"Sarah Jenkins", "Elite Yearly", "$1,200", "2024-06-14", "PAID"},
                {"David Chen", "Pro Monthly", "$149", "2024-06-14", "OVERDUE"},
                {"Elena Rodriguez", "Body Transformation", "$450", "2024-06-13", "PAID"}
        };
        JTable table = new JTable(new DefaultTableModel(rows, cols));
        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(new Color(45, 45, 45));
        table.setRowHeight(32);
        table.getTableHeader().setBackground(Theme.PANEL_ALT);
        table.getTableHeader().setForeground(Theme.TEXT);
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.PANEL);
        return scroll;
    }
}
