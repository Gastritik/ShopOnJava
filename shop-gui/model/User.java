package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String username;
    private String password;
    private String email;
    private List<CartItem> cart;
    
    public User(int id, String username, String password, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.cart = new ArrayList<>();
    }
    
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public List<CartItem> getCart() { return cart; }
    
    public void setCart(List<CartItem> cart) { this.cart = cart; }
}