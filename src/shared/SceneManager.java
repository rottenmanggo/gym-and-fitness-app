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

            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Parent root = loader.load();

            Scene currentScene = stage.getScene();

            if (currentScene != null) {
                currentScene.setRoot(root);
            } else {
                stage.setScene(new Scene(root, width, height));
            }

            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}