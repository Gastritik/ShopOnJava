package gui;

import model.User;
import model.CartItem;
import model.Product;
import database.Database;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class CartPanel extends JPanel {
    private User currentUser;
    private MainFrame parent;
    private Database db;
    private JTable cartTable;
    private DefaultTableModel tableModel;
    private JLabel totalLabel;
    
    public CartPanel(User user, MainFrame parent) {
        this.currentUser = user;
        this.parent = parent;
        this.db = Database.getInstance();
        initUI();
        refreshCart();
    }
    
    private void initUI() {
        setLayout(new BorderLayout());
        
        // Создание таблицы
        String[] columns = {"Товар", "Цена", "Количество", "Сумма", "Действие"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 4;
            }
        };
        
        cartTable = new JTable(tableModel);
        cartTable.setRowHeight(30);
        
        // Настройка столбцов
        TableColumnModel columnModel = cartTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(200);
        columnModel.getColumn(1).setPreferredWidth(100);
        columnModel.getColumn(2).setPreferredWidth(80);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(80);
        
        // Обработчик изменения количества
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2 && e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int quantity = (Integer) tableModel.getValueAt(row, 2);
                int productId = (Integer) tableModel.getValueAt(row, 5);
                db.updateCartItemQuantity(currentUser, productId, quantity);
                refreshCart();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(cartTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Нижняя панель
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(new Color(240, 240, 240));
        
        totalLabel = new JLabel("Итого: 0.00 руб.");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(255, 69, 0));
        
        JButton checkoutBtn = new JButton("Оформить заказ");
        checkoutBtn.setBackground(new Color(50, 205, 50));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 14));
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.addActionListener(e -> checkout());
        
        JButton clearCartBtn = new JButton("Очистить корзину");
        clearCartBtn.addActionListener(e -> clearCart());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(clearCartBtn);
        buttonPanel.add(checkoutBtn);
        
        bottomPanel.add(totalLabel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    public void refreshCart() {
        tableModel.setRowCount(0);
        List<CartItem> cart = currentUser.getCart();
        
        for (CartItem item : cart) {
            Product product = db.getProductById(item.getProductId());
            if (product != null) {
                JSpinner spinner = new JSpinner(new SpinnerNumberModel(item.getQuantity(), 1, product.getStock(), 1));
                spinner.addChangeListener(e -> {
                    int newQuantity = (Integer) spinner.getValue();
                    db.updateCartItemQuantity(currentUser, item.getProductId(), newQuantity);
                    refreshCart();
                });
                
                Object[] row = {
                    item.getProductName(),
                    String.format("%.2f руб.", item.getProductPrice()),
                    item.getQuantity(),
                    String.format("%.2f руб.", item.getTotalPrice()),
                    "Удалить",
                    item.getProductId()
                };
                tableModel.addRow(row);
            }
        }
        
        // Обновляем итоговую сумму
        double total = db.getCartTotal(currentUser);
        totalLabel.setText(String.format("Итого: %.2f руб.", total));
        
        // Добавляем кнопки удаления
        cartTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        cartTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
    }
    
    private void checkout() {
        if (currentUser.getCart().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Корзина пуста!", 
                "Ошибка", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            String.format("Сумма заказа: %.2f руб.\nПодтвердить оформление?", 
                db.getCartTotal(currentUser)),
            "Оформление заказа",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                db.checkout(currentUser);
                JOptionPane.showMessageDialog(this, 
                    "Заказ успешно оформлен!\nСпасибо за покупку!", 
                    "Успех", 
                    JOptionPane.INFORMATION_MESSAGE);
                refreshCart();
                parent.refreshCart();
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, 
                    e.getMessage(), 
                    "Ошибка", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearCart() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Вы уверены, что хотите очистить корзину?", 
            "Подтверждение", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            List<CartItem> cart = currentUser.getCart();
            for (CartItem item : cart) {
                db.removeFromCart(currentUser, item.getProductId());
            }
            refreshCart();
            parent.refreshCart();
        }
    }
    
    // Рендерер для кнопок в таблице
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setText("Удалить");
            setBackground(Color.RED);
            setForeground(Color.WHITE);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    // Редактор для кнопок в таблице
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private int row;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton("Удалить");
            button.setBackground(Color.RED);
            button.setForeground(Color.WHITE);
            button.addActionListener(e -> {
                int productId = (Integer) tableModel.getValueAt(row, 5);
                db.removeFromCart(currentUser, productId);
                refreshCart();
                parent.refreshCart();
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.row = row;
            return button;
        }
    }
}