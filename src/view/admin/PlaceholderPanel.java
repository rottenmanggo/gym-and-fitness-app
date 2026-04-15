package view.admin;

import util.Theme;

import javax.swing.*;
import java.awt.*;

public class PlaceholderPanel extends JPanel {
    public PlaceholderPanel(String title) {
        setBackground(Theme.BG);
        setLayout(new GridBagLayout());
        JLabel label = new JLabel(title + " - Coming Soon");
        label.setForeground(Theme.TEXT);
        label.setFont(new Font("SansSerif", Font.BOLD, 30));
        add(label);
    }
}
