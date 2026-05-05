package auth;

import javafx.scene.Node;
import shared.SceneManager;
import shared.Session;

public class LogoutController {

    public static void logout(Node node) {
        Session.clear();
        SceneManager.changeScene(node, "/auth/Login.fxml", "GYMBRUT - Login", 1100, 720);
    }
}