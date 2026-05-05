package shared;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneManager {

    public static void changeScene(Node node, String fxmlPath, String title, int width, int height) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            Scene scene = new Scene(loader.load());

            Stage stage = (Stage) node.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.setMinWidth(width);
            stage.setMinHeight(height);
            stage.centerOnScreen();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}