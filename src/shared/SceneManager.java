package shared;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    /**
     * Ganti scene dan otomatis fullscreen
     *
     * @param node     Node mana saja dari scene saat ini
     * @param fxmlPath Path FXML
     * @param title    Judul window
     * @param width    Lebar default (optional, tidak akan terlalu penting)
     * @param height   Tinggi default
     */
    public static void changeScene(Node node, String fxmlPath, String title, int width, int height) {
        try {
            // Ambil stage dari node
            Stage stage = (Stage) node.getScene().getWindow();

            // Load FXML
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            // Buat scene baru
            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);

            // 🔥 Maksimalkan otomatis full desktop
            stage.setMaximized(true);

            // Tampilkan
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}