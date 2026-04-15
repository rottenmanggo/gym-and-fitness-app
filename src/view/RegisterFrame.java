package view;

import controller.AuthController;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private final JTextField txtName = new JTextField();
    private final JTextField txtEmail = new JTextField();
    private final JTextField txtPhone = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final AuthController authController = new AuthController();

    public RegisterFrame() {
        setTitle("Gymbrut - Register Member");
        setSize(520, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel root = new JPanel();
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(25, 25, 25, 25));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Register Member");
        title.setForeground(Theme.TEXT);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));

        root.add(title);
        root.add(Box.createVerticalStrut(20));
        root.add(label("Nama Lengkap"));
        root.add(field(txtName));
        root.add(Box.createVerticalStrut(12));
        root.add(label("Email"));
        root.add(field(txtEmail));
        root.add(Box.createVerticalStrut(12));
        root.add(label("No. Telepon"));
        root.add(field(txtPhone));
        root.add(Box.createVerticalStrut(12));
        root.add(label("Password"));
        root.add(field(txtPassword));
        root.add(Box.createVerticalStrut(22));

        JButton btnSubmit = new JButton("Create Member Account");
        btnSubmit.setBackground(Theme.PRIMARY);
        btnSubmit.setForeground(new Color(0, 55, 35));
        btnSubmit.setFocusPainted(false);
        btnSubmit.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSubmit.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSubmit.addActionListener(e -> doRegister());
        root.add(btnSubmit);

        setContentPane(root);
    }

    private JLabel label(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JComponent field(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        field.setBackground(Theme.PANEL);
        field.setForeground(Theme.TEXT);
        field.setCaretColor(Theme.PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 55, 55)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private void doRegister() {
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama, email, dan password wajib diisi.");
            return;
        }

        if (authController.registerMember(name, email, password, phone)) {
            JOptionPane.showMessageDialog(this, "Registrasi member berhasil.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registrasi gagal. Email mungkin sudah digunakan.");
        }
    }
}
