package view.admin;

import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddEditMemberDialog extends JDialog {
    public AddEditMemberDialog(Window owner) {
        super(owner, "Add / Edit Member", ModalityType.APPLICATION_MODAL);
        setSize(420, 360);
        setLocationRelativeTo(owner);

        JPanel root = new JPanel();
        root.setBackground(Theme.BG);
        root.setBorder(new EmptyBorder(20,20,20,20));
        root.setLayout(new GridLayout(0, 1, 10, 10));

        JTextField txtName = field();
        JTextField txtEmail = field();
        JTextField txtPhone = field();
        JComboBox<String> cmbPlan = new JComboBox<>(new String[]{"Elite Performance", "Power Lifter Pro", "Standard Build"});
        JButton btnSave = new JButton("Save Member");
        btnSave.setBackground(Theme.PRIMARY);
        btnSave.setForeground(new Color(0,55,35));
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Contoh dialog berhasil dibuka. Logic simpan bisa disambungkan ke DAO.");
            dispose();
        });

        root.add(new JLabel("Nama") {{ setForeground(Theme.TEXT); }});
        root.add(txtName);
        root.add(new JLabel("Email") {{ setForeground(Theme.TEXT); }});
        root.add(txtEmail);
        root.add(new JLabel("Phone") {{ setForeground(Theme.TEXT); }});
        root.add(txtPhone);
        root.add(new JLabel("Plan") {{ setForeground(Theme.TEXT); }});
        root.add(cmbPlan);
        root.add(btnSave);

        setContentPane(root);
    }

    private JTextField field() {
        JTextField f = new JTextField();
        f.setBackground(Theme.PANEL);
        f.setForeground(Theme.TEXT);
        return f;
    }
}
