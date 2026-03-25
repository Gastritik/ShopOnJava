package gui;

import database.Database;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private Database db;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;
    
    public RegisterFrame() {
        db = Database.getInstance();
        initUI();
    }
    
    private void initUI() {
        setTitle("Регистрация");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        JLabel titleLabel = new JLabel("Регистрация нового пользователя");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Логин:"), gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Пароль:"), gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Email:"), gbc);
        
        emailField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(emailField, gbc);
        
        JButton registerBtn = new JButton("Зарегистрироваться");
        JButton backBtn = new JButton("Назад");
        
        registerBtn.addActionListener(e -> register());
        backBtn.addActionListener(e -> backToLogin());
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(registerBtn);
        buttonPanel.add(backBtn);
        
        gbc.gridy = 4;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void register() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String email = emailField.getText();
        
        if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Заполните все поля!", 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (db.getUserByUsername(username) != null) {
            JOptionPane.showMessageDialog(this, "Пользователь уже существует!", 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        db.addUser(username, password, email);
        JOptionPane.showMessageDialog(this, "Регистрация успешна!", 
            "Успех", JOptionPane.INFORMATION_MESSAGE);
        backToLogin();
    }
    
    private void backToLogin() {
        new LoginFrame().setVisible(true);
        dispose();
    }
}