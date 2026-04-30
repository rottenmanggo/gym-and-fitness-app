package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
// import javafx.scene.layout.*;
// import javafx.scene.control.*;
// import javafx.geometry.Insets;
// import javafx.geometry.Pos;

// import java.io.IOException;
// import java.net.URL;
// import shared.Session;

/**
 * Main.java — Entry point untuk testing standalone bagian /payment (Carlos)
 *
 * Layout:
 * ┌─────────────────────────────────────────────┐
 * │ GYMBRUT Admin Panel (simulasi sidebar) │
 * ├──────────┬──────────────────────────────────┤
 * │ Sidebar │ CENTER (Membership/Payment) │
 * └──────────┴──────────────────────────────────┘
 */
public class Main extends Application {

    // private BorderPane root;

    @Override
public void start(Stage stage) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/layout/MainLayout.fxml"));
        Scene scene = new Scene(loader.load(), 1200, 720);

        stage.setTitle("GYMBRUT — Admin Panel");
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // @Override
    // public void start(Stage stage) {
    // try {
    // FXMLLoader loader = new
    // FXMLLoader(getClass().getResource("/auth/Login.fxml"));
    // Scene scene = new Scene(loader.load(), 1000, 650);

    // stage.setTitle("GYMBRUT - Login");
    // stage.setScene(scene);
    // stage.setMaximized(true);
    // stage.show();

    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

    // public void showLogin(Stage stage) {
    //     try {
    //         FXMLLoader loader = new FXMLLoader(getClass().getResource("/auth/Login.fxml"));
    //         Scene scene = new Scene(loader.load(), 1200, 720);

    //         stage.setTitle("GYMBRUT - Login");
    //         stage.setScene(scene);
    //         stage.setMaximized(true);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    // }

    // public void showDashboard(Stage stage) {
    //     root = new BorderPane();
    //     root.setLeft(buildSidebar());
    //     loadPage("Dashboard");

    //     Scene scene = new Scene(root, 1200, 720);
    //     stage.setTitle("GYMBRUT — Admin Panel");
    //     stage.setScene(scene);
    //     stage.setFullScreen(true);
    // }

    // // ============================================================
    // // Sidebar sederhana untuk simulasi layout utama
    // // ============================================================

    // private VBox buildSidebar() {
    //     VBox sidebar = new VBox(0);
    //     sidebar.setPrefWidth(235);
    //     sidebar.setStyle(
    //             "-fx-background-color: white;" +
    //                     "-fx-font-family: 'Segoe UI', Arial, sans-serif;" +
    //                     "-fx-border-color: #e5e7eb;" +
    //                     "-fx-border-width: 0 1 0 0;");

    //     // Logo
    //     VBox logoBox = new VBox(4);
    //     logoBox.setPadding(new Insets(30, 24, 28, 24));
    //     logoBox.setStyle("-fx-border-color: #2d2d4a; -fx-border-width: 0 0 1 0;");

    //     HBox logoRow = new HBox(12);
    //     logoRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    //     Label logoIcon = new Label("⚡");
    //     logoIcon.setStyle(
    //             "-fx-font-size: 24px;" +
    //                     "-fx-background-color: #ff8500;" +
    //                     "-fx-background-radius: 14;" +
    //                     "-fx-padding: 10 14;" +
    //                     "-fx-text-fill: white;");

    //     VBox logoText = new VBox(2);
    //     Label logoTitle = new Label("GYMBRUT");
    //     logoTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");
    //     Label logoSub = new Label("ADMIN PANEL");
    //     logoSub.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
    //     logoText.getChildren().addAll(logoTitle, logoSub);

    //     logoRow.getChildren().addAll(logoIcon, logoText);
    //     logoBox.getChildren().add(logoRow);

    //     // Menu label

    //     Label menuLabel = new Label("MAIN MENU");
    //     menuLabel.setStyle(
    //             "-fx-font-size: 10px;" +
    //                     "-fx-font-weight: bold;" +
    //                     "-fx-text-fill: #94a3b8;" +
    //                     "-fx-padding: 12 24 10 24;");

    //     // Nav items
    //     VBox nav = new VBox(8);
    //     nav.setPadding(new Insets(0, 14, 0, 14));

    //     String[][] items = {
    //             { "▦", "Dashboard", "Dashboard" },
    //             { "♟", "Members", "Members" },
    //             { "▤", "Memberships", "Membership" },
    //             { "▰", "Payments", "Payment" },
    //             { "▟", "Reports", "Reports" },
    //             { "♨", "Workouts", "Workouts" },
    //             { "☻", "Profile", "Profile" },
    //     };

    //     for (String[] item : items) {
    //         Button btn = buildNavBtn(item[0], item[1], item[2]);
    //         nav.getChildren().add(btn);
    //     }

    //     // Spacer
    //     Region spacer = new Region();
    //     VBox.setVgrow(spacer, Priority.ALWAYS);

    //     // User & Logout
    //     HBox userBox = new HBox(12);
    //     userBox.setPadding(new Insets(18, 24, 12, 24));
    //     userBox.setStyle("-fx-border-color: #e5e7eb; -fx-border-width: 1 0 0 0;");
    //     userBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

    //     Label avatar = new Label("O");
    //     avatar.setStyle(
    //             "-fx-background-color: #fff3e6;" +
    //                     "-fx-background-radius: 14;" +
    //                     "-fx-min-width: 42;" +
    //                     "-fx-min-height: 42;" +
    //                     "-fx-max-width: 42;" +
    //                     "-fx-max-height: 42;" +
    //                     "-fx-font-weight: bold;" +
    //                     "-fx-text-fill: #0f172a;" +
    //                     "-fx-alignment: center;");

    //     VBox userInfo = new VBox(2);
    //     Label uName = new Label("obama");
    //     uName.setStyle("-fx-font-size: 13px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");
    //     Label uRole = new Label("Administrator");
    //     uRole.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");
    //     userInfo.getChildren().addAll(uName, uRole);

    //     userBox.getChildren().addAll(avatar, userInfo);

    //     Button logoutBtn = new Button("Logout");
    //     logoutBtn.setPrefWidth(140);
    //     logoutBtn.setStyle(logoutStyle());

    //     logoutBtn.setOnMouseEntered(e -> logoutBtn.setStyle(logoutHoverStyle()));
    //     logoutBtn.setOnMouseExited(e -> logoutBtn.setStyle(logoutStyle()));

    //     logoutBtn.setOnAction(e -> {
    //         Session.logout();
    //         showLogin((Stage) logoutBtn.getScene().getWindow());
    //     });

    //     HBox logoutWrapper = new HBox(logoutBtn);
    //     logoutWrapper.setAlignment(Pos.CENTER);

    //     VBox.setMargin(logoutWrapper, new Insets(8, 0, 20, 0));

    //     sidebar.getChildren().addAll(logoBox, menuLabel, nav, spacer, userBox, logoutWrapper);
    //     return sidebar;
    // }

    // private Button activeButton = null;

    // private Button buildNavBtn(String icon, String label, String page) {
    //     Button btn = new Button(icon + "  " + label);
    //     btn.setMaxWidth(Double.MAX_VALUE);
    //     btn.setStyle(navNormalStyle());

    //     btn.setOnMouseEntered(e -> {
    //         if (btn != activeButton) {
    //             btn.setStyle(navHoverStyle());
    //         }
    //     });

    //     btn.setOnMouseExited(e -> {
    //         if (btn != activeButton) {
    //             btn.setStyle(navNormalStyle());
    //         }
    //     });

    //     btn.setOnAction(e -> {
    //         if (activeButton != null) {
    //             activeButton.setStyle(navNormalStyle());
    //         }

    //         activeButton = btn;
    //         activeButton.setStyle(navActiveStyle());

    //         loadPage(page);
    //     });

    //     if (page.equals("Dashboard")) {
    //         activeButton = btn;
    //         btn.setStyle(navActiveStyle());
    //     }

    //     return btn;
    // }

    // private String navNormalStyle() {
    //     return "-fx-background-color: transparent;" +
    //             "-fx-text-fill: #334155;" +
    //             "-fx-font-size: 14px;" +
    //             "-fx-font-weight: bold;" +
    //             "-fx-alignment: CENTER_LEFT;" +
    //             "-fx-padding: 13 16;" +
    //             "-fx-background-radius: 12;" +
    //             "-fx-cursor: hand;";
    // }

    // private String navHoverStyle() {
    //     return "-fx-background-color: #fff7ed;" +
    //             "-fx-text-fill: #ea580c;" +
    //             "-fx-font-size: 14px;" +
    //             "-fx-font-weight: bold;" +
    //             "-fx-alignment: CENTER_LEFT;" +
    //             "-fx-padding: 13 16;" +
    //             "-fx-background-radius: 12;" +
    //             "-fx-cursor: hand;";
    // }

    // private String navActiveStyle() {
    //     return "-fx-background-color: #fff3e6;" +
    //             "-fx-border-color: #ffb36b;" +
    //             "-fx-border-width: 1;" +
    //             "-fx-border-radius: 12;" +
    //             "-fx-text-fill: #f97316;" +
    //             "-fx-font-size: 14px;" +
    //             "-fx-font-weight: 900;" +
    //             "-fx-alignment: CENTER_LEFT;" +
    //             "-fx-padding: 13 16;" +
    //             "-fx-background-radius: 12;" +
    //             "-fx-cursor: hand;";
    // }

    // private String logoutStyle() {
    //     return "-fx-background-color: #ff8500;" +
    //             "-fx-text-fill: white;" +
    //             "-fx-font-weight: bold;" +
    //             "-fx-font-size: 12px;" +
    //             "-fx-padding: 8 10;" +
    //             "-fx-background-radius: 8;" +
    //             "-fx-cursor: hand;";
    // }

    // private String logoutHoverStyle() {
    //     return "-fx-background-color: #ea580c;" +
    //             "-fx-text-fill: white;" +
    //             "-fx-font-weight: bold;" +
    //             "-fx-font-size: 12px;" +
    //             "-fx-padding: 8 10;" +
    //             "-fx-background-radius: 8;" +
    //             "-fx-cursor: hand;";
    // }

    // // ============================================================
    // // Page Loader — setCenter() pattern
    // // ============================================================

    // private void loadPage(String page) {
    //     try {
    //         String fxmlFile;
    //         switch (page) {
    //             case "Dashboard" -> fxmlFile = "/dashboard/Dashboard.fxml";
    //             case "Membership" -> fxmlFile = "/payment/Membership.fxml";
    //             case "Payment" -> fxmlFile = "/payment/Payment.fxml";
    //             default -> {
    //                 root.setCenter(buildPlaceholder(page + " Page"));
    //                 return;
    //             }
    //         }

    //         URL url = getClass().getResource(fxmlFile);
    //         if (url == null) {
    //             root.setCenter(buildPlaceholder("FXML tidak ditemukan: " + fxmlFile));
    //             return;
    //         }

    //         FXMLLoader loader = new FXMLLoader(url);
    //         root.setCenter(loader.load());

    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         root.setCenter(buildPlaceholder("Error memuat halaman: " + e.getMessage()));
    //     }
    // }

    // private StackPane buildPlaceholder(String label) {
    //     StackPane wrapper = new StackPane();
    //     wrapper.setStyle("-fx-background-color: #f4f6fb;");
    //     wrapper.setPadding(new Insets(32));

    //     VBox card = new VBox(14);
    //     card.setMaxWidth(520);
    //     card.setPadding(new Insets(34));
    //     card.setStyle(
    //             "-fx-background-color: white;" +
    //                     "-fx-background-radius: 18;" +
    //                     "-fx-effect: dropshadow(gaussian, rgba(15,23,42,0.10), 22, 0.25, 0, 8);");

    //     Label icon = new Label("🚧");
    //     icon.setStyle("-fx-font-size: 42px;");

    //     Label title = new Label(label);
    //     title.setStyle("-fx-font-size: 26px; -fx-font-weight: 900; -fx-text-fill: #0f172a;");

    //     Label desc = new Label(
    //             "Halaman ini masih dalam tahap pengembangan dan akan dikerjakan oleh anggota tim terkait.");
    //     desc.setWrapText(true);
    //     desc.setStyle("-fx-font-size: 14px; -fx-text-fill: #64748b; -fx-line-spacing: 4;");

    //     Label status = new Label("Coming Soon");
    //     status.setStyle(
    //             "-fx-background-color: #fff3e6;" +
    //                     "-fx-text-fill: #f97316;" +
    //                     "-fx-font-weight: bold;" +
    //                     "-fx-background-radius: 99;" +
    //                     "-fx-padding: 8 16;");

    //     card.getChildren().addAll(icon, title, desc, status);
    //     wrapper.getChildren().add(card);

    //     return wrapper;
    // }

    public static void main(String[] args) {
        launch(args);
    }
}