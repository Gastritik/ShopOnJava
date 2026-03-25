package model;

import java.io.Serializable;
import javax.swing.ImageIcon;

public class Product implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private double price;
    private String description;
    private int stock;
    private String imagePath;
    private transient ImageIcon image;
    
    public Product(int id, String name, double price, String description, int stock, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.stock = stock;
        this.imagePath = imagePath;
        loadImage();
    }
    
    private void loadImage() {
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                this.image = new ImageIcon(getClass().getResource("/resources/images/" + imagePath));
                if (this.image.getIconWidth() == -1) {
                    // Если не нашли в ресурсах, пробуем из файловой системы
                    this.image = new ImageIcon(imagePath);
                }
            }
        } catch (Exception e) {
            System.err.println("Не удалось загрузить изображение: " + imagePath);
            this.image = null;
        }
    }
    
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getDescription() { return description; }
    public int getStock() { return stock; }
    public String getImagePath() { return imagePath; }
    public ImageIcon getImage() { 
        if (image == null) loadImage();
        return image; 
    }
    
    public void setStock(int stock) { this.stock = stock; }
    public void decreaseStock(int quantity) {
        if (quantity <= stock) stock -= quantity;
    }
    
    @Override
    public String toString() {
        return String.format("%s - %.2f руб.", name, price);
    }
}