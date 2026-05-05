package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Load FXML login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/Login.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setScene(scene);
        stage.setTitle("GYMBRUT - Login");

        // Maksimalkan langsung
        stage.setMaximized(true);
        stage.setFullScreen(false); // true jika mau fullscreen tanpa taskbar

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}