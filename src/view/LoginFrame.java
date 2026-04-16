package view;

import controller.AuthController;
import model.User;
import util.Theme;
import view.admin.AdminMainFrame;
import view.member.MemberMainFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {
    private final JTextField txtEmail = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JToggleButton btnMember = new JToggleButton("Login as Member", true);
    private final JToggleButton btnAdmin = new JToggleButton("Login as Admin");
    private final AuthController authController = new AuthController();

    public LoginFrame() {
        setTitle("Gymbrut - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 800);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        add(buildLeftPanel());
        add(buildRightPanel());
    }

    private JPanel buildLeftPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.BG);
        panel.setBorder(new EmptyBorder(90, 70, 90, 40));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("GYMBRUT");
        brand.setForeground(Theme.PRIMARY);
        brand.setFont(new Font("Monospaced", Font.BOLD, 22));

        JLabel title = new JLabel("UNLEASH");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 76));

        JLabel title2 = new JLabel("THE KINETIC");
        title2.setForeground(Theme.PRIMARY);
        title2.setFont(new Font("Monospaced", Font.BOLD, 64));

        JLabel title3 = new JLabel("VAULT");
        title3.setForeground(Theme.TEXT);
        title3.setFont(new Font("SansSerif", Font.BOLD, 76));

        JLabel desc = new JLabel("<html>Experience the next generation of elite fitness management.<br>Secure, precise, and engineered for high-performance results.</html>");
        desc.setForeground(Theme.MUTED);
        desc.setFont(Theme.FONT_PLAIN_14);

        panel.add(brand);
        panel.add(Box.createVerticalStrut(40));
        panel.add(title);
        panel.add(title2);
        panel.add(title3);
        panel.add(Box.createVerticalStrut(20));
        panel.add(desc);
        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(8, 12, 14));

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(450, 500));
        card.setBackground(new Color(28, 28, 28));
        card.setBorder(new EmptyBorder(30, 30, 30, 30));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Access Your Console");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));

        JLabel subtitle = new JLabel("Enter your credentials to manage your performance.");
        subtitle.setForeground(Theme.MUTED);

        ButtonGroup group = new ButtonGroup();
        group.add(btnMember);
        group.add(btnAdmin);
        styleToggle(btnMember);
        styleToggle(btnAdmin);

        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        togglePanel.setOpaque(false);
        togglePanel.add(btnMember);
        togglePanel.add(btnAdmin);

        styleField(txtEmail, "Email Address");
        styleField(txtPassword, "Security Key");

        JButton btnLogin = new JButton("INITIALIZE PROTOCOL →");
        btnLogin.setBackground(Theme.PRIMARY);
        btnLogin.setForeground(new Color(0, 50, 35));
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        btnLogin.addActionListener(e -> doLogin());

        JButton btnRegister = new JButton("New to the collective? Join Gymbrut");
        btnRegister.setBorderPainted(false);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setForeground(Theme.PRIMARY);
        btnRegister.addActionListener(e -> new RegisterFrame().setVisible(true));

        card.add(title);
        card.add(Box.createVerticalStrut(8));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(30));
        card.add(togglePanel);
        card.add(Box.createVerticalStrut(25));
        card.add(new JLabel("EMAIL ADDRESS") {{ setForeground(Theme.MUTED); }});
        card.add(Box.createVerticalStrut(6));
        card.add(txtEmail);
        card.add(Box.createVerticalStrut(20));
        card.add(new JLabel("SECURITY KEY") {{ setForeground(Theme.MUTED); }});
        card.add(Box.createVerticalStrut(6));
        card.add(txtPassword);
        card.add(Box.createVerticalStrut(25));
        card.add(btnLogin);
        card.add(Box.createVerticalStrut(20));
        card.add(btnRegister);

        wrapper.add(card);
        return wrapper;
    }

    private void styleField(JTextField field, String hint) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        field.setPreferredSize(new Dimension(320, 45));
        field.setBackground(Color.BLACK);
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(50, 50, 50)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        field.setToolTipText(hint);
    }

    private void styleToggle(AbstractButton button) {
        button.setBackground(Color.BLACK);
        button.setForeground(Theme.TEXT);
        button.setFocusPainted(false);
        button.addActionListener(e -> {
            btnMember.setBackground(btnMember.isSelected() ? Theme.PRIMARY : Color.BLACK);
            btnMember.setForeground(btnMember.isSelected() ? new Color(0, 55, 35) : Theme.TEXT);
            btnAdmin.setBackground(btnAdmin.isSelected() ? Theme.PRIMARY : Color.BLACK);
            btnAdmin.setForeground(btnAdmin.isSelected() ? new Color(0, 55, 35) : Theme.TEXT);
        });
        if (button == btnMember) {
            button.setBackground(Theme.PRIMARY);
            button.setForeground(new Color(0, 55, 35));
        }
    }

    private void doLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());
        boolean adminMode = btnAdmin.isSelected();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email dan password wajib diisi.", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = authController.login(email, password, adminMode);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Login gagal. Periksa email, password, dan role.", "Login", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Selamat datang, " + user.getName());
        if (user.getRoleId() == 1) {
            new AdminMainFrame(user).setVisible(true);
        } else {
            new MemberMainFrame(user).setVisible(true);
        }
        dispose();
    }
}
