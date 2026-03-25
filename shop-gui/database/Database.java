package database;

import model.User;
import model.Product;
import model.CartItem;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {
    private static Database instance;
    private Map<Integer, User> users;
    private Map<Integer, Product> products;
    private AtomicInteger userIdCounter;
    private AtomicInteger productIdCounter;
    private static final String DATA_FILE = "shop_data.ser";
    
    private Database() {
        users = new HashMap<>();
        products = new HashMap<>();
        userIdCounter = new AtomicInteger(1);
        productIdCounter = new AtomicInteger(1);
        loadData();
        if (products.isEmpty()) {
            initDefaultProducts();
        }
    }
    
    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }
    
    private void initDefaultProducts() {
        addProduct(new Product(productIdCounter.getAndIncrement(), 
            "Игровой Ноутбук", 45000.0, "Мощный ноутбук для игр и работы", 10, "laptop.png"));
        addProduct(new Product(productIdCounter.getAndIncrement(), 
            "Смартфон", 25000.0, "Современный смартфон с отличной камерой", 15, "phone.png"));
        addProduct(new Product(productIdCounter.getAndIncrement(), 
            "Беспроводные Наушники", 3000.0, "Качественный звук без проводов", 20, "headphones.png"));
        addProduct(new Product(productIdCounter.getAndIncrement(), 
            "Механическая Клавиатура", 2000.0, "Удобная клавиатура для геймеров", 8, "keyboard.png"));
        addProduct(new Product(productIdCounter.getAndIncrement(), 
            "Игровая Мышь", 1500.0, "Высокая точность и эргономика", 12, "mouse.png"));
        addProduct(new Product(productIdCounter.getAndIncrement(), 
            "Монитор 27\"", 18000.0, "4K дисплей для профессиональной работы", 5, "monitor.png"));
    }
    
    public void addProduct(Product product) {
        products.put(product.getId(), product);
        saveData();
    }
    
    public User addUser(String username, String password, String email) {
        int id = userIdCounter.getAndIncrement();
        User user = new User(id, username, password, email);
        users.put(id, user);
        saveData();
        return user;
    }
    
    public User getUserByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equals(username)) return user;
        }
        return null;
    }
    
    public boolean authenticate(String username, String password) {
        User user = getUserByUsername(username);
        return user != null && user.getPassword().equals(password);
    }
    
    public Product getProductById(int id) {
        return products.get(id);
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public void updateProduct(Product product) {
        products.put(product.getId(), product);
        saveData();
    }
    
    public void addToCart(User user, Product product, int quantity) {
        if (product.getStock() >= quantity) {
            for (CartItem item : user.getCart()) {
                if (item.getProductId() == product.getId()) {
                    item.setQuantity(item.getQuantity() + quantity);
                    saveData();
                    return;
                }
            }
            user.getCart().add(new CartItem(product, quantity));
            saveData();
        } else {
            throw new IllegalArgumentException("Недостаточно товара на складе");
        }
    }
    
    public void removeFromCart(User user, int productId) {
        user.getCart().removeIf(item -> item.getProductId() == productId);
        saveData();
    }
    
    public void updateCartItemQuantity(User user, int productId, int quantity) {
        for (CartItem item : user.getCart()) {
            if (item.getProductId() == productId) {
                if (quantity > 0) {
                    item.setQuantity(quantity);
                } else {
                    user.getCart().remove(item);
                }
                break;
            }
        }
        saveData();
    }
    
    public double getCartTotal(User user) {
        double total = 0;
        for (CartItem item : user.getCart()) {
            total += item.getTotalPrice();
        }
        return total;
    }
    
    public void checkout(User user) {
        for (CartItem item : user.getCart()) {
            Product product = getProductById(item.getProductId());
            if (product != null && product.getStock() >= item.getQuantity()) {
                product.decreaseStock(item.getQuantity());
                updateProduct(product);
            } else {
                throw new IllegalArgumentException(
                    String.format("Недостаточно товара '%s' на складе", item.getProductName())
                );
            }
        }
        user.getCart().clear();
        saveData();
    }
    
    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(users);
            oos.writeObject(products);
            oos.writeInt(userIdCounter.get());
            oos.writeInt(productIdCounter.get());
        } catch (IOException e) {
            System.err.println("Ошибка при сохранении данных: " + e.getMessage());
        }
    }
    
    @SuppressWarnings("unchecked")
    private void loadData() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            users = (Map<Integer, User>) ois.readObject();
            products = (Map<Integer, Product>) ois.readObject();
            userIdCounter = new AtomicInteger(ois.readInt());
            productIdCounter = new AtomicInteger(ois.readInt());
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
        }
    }
}