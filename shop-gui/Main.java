import gui.LoginFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Устанавливаем Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Запускаем приложение
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}