package gui;

import database.Database;
import model.User;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private Database db;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    public LoginFrame() {
        db = Database.getInstance();
        initUI();
    }
    
    private void initUI() {
        setTitle("Вход в магазин");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Основная панель
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Заголовок
        JLabel titleLabel = new JLabel("Добро пожаловать в магазин!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        // Поле логина
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Логин:"), gbc);
        
        usernameField = new JTextField(15);
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Поле пароля
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Пароль:"), gbc);
        
        passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginBtn = new JButton("Вход");
        JButton registerBtn = new JButton("Регистрация");
        
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> openRegister());
        
        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel);
    }
    
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        if (db.authenticate(username, password)) {
            User user = db.getUserByUsername(username);
            new MainFrame(user).setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Неверный логин или пароль!", 
                "Ошибка", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openRegister() {
        new RegisterFrame().setVisible(true);
        dispose();
    }
}