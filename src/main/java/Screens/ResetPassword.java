package Screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ResetPassword {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ResetPassword().createAndShowGUI());
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("Reset Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo
        ImageIcon rawLogo = new ImageIcon("ae37f58d-efc0-47bc-95cd-19c058c6f708.png");
        Image scaledImage = rawLogo.getImage().getScaledInstance(300, 70, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel title = new JLabel("Payroll Management System");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emailLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        RoundedTextField emailField = new RoundedTextField(20);
        emailField.setMaximumSize(new Dimension(300, 40));
        emailField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(emailField);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        RoundedButton sendButton = new RoundedButton("Send Reset Code", 20);
        sendButton.setBackground(new Color(46, 204, 113));
        sendButton.setForeground(Color.WHITE);
        sendButton.setMaximumSize(new Dimension(300, 40));
        sendButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        sendButton.addActionListener(e -> {
            String email = emailField.getText();
            if (email.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your email.");
            } else {
                frame.dispose();
                ResetPassword2.showNewPasswordScreen();
            }
        });
        panel.add(sendButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}

// ----------- ResetPassword2 Class ------------
class ResetPassword2 {
    public static void showNewPasswordScreen() {
        JFrame frame = new JFrame("New Password");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setLayout(new GridBagLayout());

        JPanel panel = new JPanel();
        panel.setBackground(Color.LIGHT_GRAY);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Logo
        ImageIcon rawLogo = new ImageIcon("ae37f58d-efc0-47bc-95cd-19c058c6f708.png");
        Image scaledImage = rawLogo.getImage().getScaledInstance(300, 70, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel title = new JLabel("Payroll Management System");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel newPassLabel = new JLabel("New Password");
        newPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(newPassLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        RoundedPasswordField newPasswordField = new RoundedPasswordField(20);
        newPasswordField.setMaximumSize(new Dimension(300, 40));
        newPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(newPasswordField);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel confirmPassLabel = new JLabel("Confirm Password");
        confirmPassLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(confirmPassLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        RoundedPasswordField confirmPasswordField = new RoundedPasswordField(20);
        confirmPasswordField.setMaximumSize(new Dimension(300, 40));
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(confirmPasswordField);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        RoundedButton resetButton = new RoundedButton("Reset Password", 20);
        resetButton.setBackground(new Color(46, 204, 113));
        resetButton.setForeground(Color.WHITE);
        resetButton.setMaximumSize(new Dimension(300, 40));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> {
            String newPass = String.valueOf(newPasswordField.getPassword());
            String confirmPass = String.valueOf(confirmPasswordField.getPassword());

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in both fields.");
            } else if (!newPass.equals(confirmPass)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match.");
            } else {
                JOptionPane.showMessageDialog(frame, "Password has been reset!");
                frame.dispose();
            }
        });

        panel.add(resetButton);

        frame.add(panel);
        frame.setVisible(true);
    }
}

// ----------- RoundedButton Class ------------
class RoundedButton extends JButton {
    private int cornerRadius;

    public RoundedButton(String text, int radius) {
        super(text);
        cornerRadius = radius;
        setContentAreaFilled(false);
        setFocusPainted(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getForeground());
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }

    @Override
    public boolean isOpaque() {
        return false;
    }
}

// ----------- RoundedTextField Class ------------
class RoundedTextField extends JTextField {
    private int cornerRadius = 15;

    public RoundedTextField(int columns) {
        super(columns);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }
}

// ----------- RoundedPasswordField Class ------------
class RoundedPasswordField extends JPasswordField {
    private int cornerRadius = 15;

    public RoundedPasswordField(int columns) {
        super(columns);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(Color.GRAY);
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
        g2.dispose();
    }
}
