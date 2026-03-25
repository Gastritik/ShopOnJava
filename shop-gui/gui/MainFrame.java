package gui;

import model.User;
import database.Database;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private User currentUser;
    private Database db;
    private JTabbedPane tabbedPane;
    private ProductPanel productPanel;
    private CartPanel cartPanel;
    
    public MainFrame(User user) {
        this.currentUser = user;
        this.db = Database.getInstance();
        initUI();
    }
    
    private void initUI() {
        setTitle("Магазин - Добро пожаловать, " + currentUser.getUsername());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Создаем панели
        productPanel = new ProductPanel(currentUser, this);
        cartPanel = new CartPanel(currentUser, this);
        
        // Создаем вкладки
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Товары", productPanel);
        tabbedPane.addTab("Корзина", cartPanel);
        
        // Верхняя панель с информацией о пользователе
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(70, 130, 200));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel welcomeLabel = new JLabel("Пользователь: " + currentUser.getUsername());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton logoutBtn = new JButton("Выйти");
        logoutBtn.addActionListener(e -> logout());
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
        
        // Обновляем корзину при загрузке
        refreshCart();
    }
    
    public void refreshCart() {
        if (cartPanel != null) {
            cartPanel.refreshCart();
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите выйти?", 
            "Выход", 
            JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            dispose();
        }
    }
}