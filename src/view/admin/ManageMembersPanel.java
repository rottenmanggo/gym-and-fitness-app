package view.admin;

import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ManageMembersPanel extends JPanel {
    public ManageMembersPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Members");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 42));

        JButton addMember = new JButton("+ Add Member");
        addMember.setBackground(Theme.PRIMARY);
        addMember.setForeground(new Color(0, 55, 35));
        addMember.setFocusPainted(false);
        addMember.addActionListener(e -> new AddEditMemberDialog(SwingUtilities.getWindowAncestor(this)).setVisible(true));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(addMember, BorderLayout.EAST);

        String[] cols = {"Name", "Athlete ID", "Contact", "Membership Plan", "Status", "Actions"};
        Object[][] rows = {
                {"Elena Vance", "GB-2024-9912", "e.vance@kinetic.com", "Elite Performance", "ACTIVE", "Edit | Delete"},
                {"Marcus Thorne", "GB-2024-1182", "m.thorne@vault.io", "Power Lifter Pro", "ACTIVE", "Edit | Delete"},
                {"Sarah Chen", "GB-2023-8841", "chen.sarah@gmail.com", "Elite Performance", "INACTIVE", "Edit | Delete"},
                {"Julian Pierce", "GB-2024-0045", "j.pierce@brut.com", "Standard Build", "ACTIVE", "Edit | Delete"}
        };
        JTable table = new JTable(new DefaultTableModel(rows, cols));
        table.setBackground(Theme.PANEL);
        table.setForeground(Theme.TEXT);
        table.setGridColor(new Color(45,45,45));
        table.setRowHeight(34);
        table.getTableHeader().setBackground(Theme.PANEL_ALT);
        table.getTableHeader().setForeground(Theme.TEXT);

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Theme.PANEL);

        add(top, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
}
