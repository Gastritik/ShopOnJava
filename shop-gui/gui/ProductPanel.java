package gui;

import model.User;
import model.Product;
import database.Database;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProductPanel extends JPanel {
    private User currentUser;
    private MainFrame parent;
    private Database db;
    private JPanel productsPanel;
    private JScrollPane scrollPane;
    
    public ProductPanel(User user, MainFrame parent) {
        this.currentUser = user;
        this.parent = parent;
        this.db = Database.getInstance();
        initUI();
        loadProducts();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        // Верхняя панель с поиском
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Поиск:");
        JTextField searchField = new JTextField(20);
        JButton searchBtn = new JButton("Найти");
        JButton clearBtn = new JButton("Сброс");
        
        searchBtn.addActionListener(e -> searchProducts(searchField.getText()));
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            loadProducts();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        
        add(searchPanel, BorderLayout.NORTH);
        
        // Панель для товаров
        productsPanel = new JPanel(new GridBagLayout());
        productsPanel.setBackground(Color.WHITE);
        scrollPane = new JScrollPane(productsPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private void loadProducts() {
        loadProducts(db.getAllProducts());
    }
    
    private void searchProducts(String query) {
        if (query.isEmpty()) {
            loadProducts();
            return;
        }
        
        List<Product> allProducts = db.getAllProducts();
        List<Product> filtered = allProducts.stream()
            .filter(p -> p.getName().toLowerCase().contains(query.toLowerCase()))
            .toList();
        
        loadProducts(filtered);
    }
    
    private void loadProducts(List<Product> products) {
        productsPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        int row = 0;
        int col = 0;
        
        for (Product product : products) {
            JPanel card = createProductCard(product);
            gbc.gridx = col;
            gbc.gridy = row;
            gbc.gridwidth = 1;
            productsPanel.add(card, gbc);
            
            col++;
            if (col >= 2) {
                col = 0;
                row++;
            }
        }
        
        productsPanel.revalidate();
        productsPanel.repaint();
    }
    
    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(400, 250));
        
        // Изображение
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(Color.WHITE);
        if (product.getImage() != null) {
            ImageIcon icon = product.getImage();
            Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(img));
            imagePanel.add(imageLabel);
        } else {
            JLabel noImageLabel = new JLabel("📦", SwingConstants.CENTER);
            noImageLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
            imagePanel.add(noImageLabel);
        }
        
        // Информация о товаре
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridy = 0;
        infoPanel.add(nameLabel, gbc);
        
        JLabel priceLabel = new JLabel(String.format("Цена: %.2f руб.", product.getPrice()));
        priceLabel.setForeground(new Color(255, 69, 0));
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 1;
        infoPanel.add(priceLabel, gbc);
        
        JLabel stockLabel = new JLabel("В наличии: " + product.getStock());
        stockLabel.setForeground(product.getStock() > 0 ? Color.GREEN : Color.RED);
        gbc.gridy = 2;
        infoPanel.add(stockLabel, gbc);
        
        JLabel descLabel = new JLabel("<html>" + product.getDescription() + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        gbc.gridy = 3;
        infoPanel.add(descLabel, gbc);
        
        // Кнопка добавления в корзину
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, product.getStock(), 1));
        JButton addBtn = new JButton("В корзину");
        addBtn.setBackground(new Color(70, 130, 200));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        
        addBtn.addActionListener(e -> {
            int quantity = (Integer) quantitySpinner.getValue();
            try {
                db.addToCart(currentUser, product, quantity);
                JOptionPane.showMessageDialog(this, 
                    "Товар добавлен в корзину!", 
                    "Успех", 
                    JOptionPane.INFORMATION_MESSAGE);
                parent.refreshCart();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getMessage(), 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(quantitySpinner);
        buttonPanel.add(addBtn);
        gbc.gridy = 4;
        infoPanel.add(buttonPanel, gbc);
        
        card.add(imagePanel, BorderLayout.WEST);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
}