package com.gymbrut.auth;

import javafx.scene.Node;
import com.gymbrut.shared.SceneManager;
import com.gymbrut.shared.Session;

public class LogoutController {

    public static void logout(Node node) {
        Session.clear();
        SceneManager.changeScene(node, "/com/gymbrut/resources/fxml/auth/Login.fxml", "GYMBRUT - Login", 1100, 720);
    }
}