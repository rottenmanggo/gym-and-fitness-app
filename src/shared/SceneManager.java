package shared;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    public static void changeScene(Node node, String fxmlPath, String title, int width, int height) {
        try {
            if (node == null || node.getScene() == null || node.getScene().getWindow() == null) {
                System.err.println("Node is not properly attached to scene/window");
                return;
            }

            Stage stage = (Stage) node.getScene().getWindow();
            System.out.println("Loading FXML: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();
            System.out.println("FXML loaded successfully");

            Scene scene = new Scene(root, width, height);
            stage.setScene(scene);
            stage.setTitle(title);

            // Hanya set maximized jika bukan dari login/register
            if (!fxmlPath.contains("auth")) {
                stage.setMaximized(true);
            } else {
                stage.setMaximized(false);
                stage.setWidth(width);
                stage.setHeight(height);
            }
            stage.show();
            System.out.println("Scene changed to: " + title);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error loading FXML: " + fxmlPath);
            System.err.println("Exception: " + e.getMessage());
        }
    }
}