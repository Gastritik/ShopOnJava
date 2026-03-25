package model;

import java.io.Serializable;

public class CartItem implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int productId;
    private String productName;
    private double productPrice;
    private int quantity;
    
    public CartItem() {}
    
    public CartItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.productPrice = product.getPrice();
        this.quantity = quantity;
    }
    
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public double getProductPrice() { return productPrice; }
    public int getQuantity() { return quantity; }
    
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotalPrice() { return productPrice * quantity; }
}