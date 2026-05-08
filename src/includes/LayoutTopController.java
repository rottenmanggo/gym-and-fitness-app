package includes;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import shared.Session;

/**
 * LayoutTopController - Controller sentral untuk layout utama (single-shell).
 *
 * Bertanggung jawab untuk:
 * 1. Memuat sidebar yang sesuai berdasarkan role user (Admin/Member).
 * 2. Menampilkan Topbar dengan data user dari session (via TopbarController).
 * 3. Me-load / swap konten halaman ke contentArea secara dinamis
 * TANPA me-reload sidebar maupun topbar.
 */
public class LayoutTopController {

    @FXML
    private StackPane sidebarContainer;

    // fx:include auto-inject: fx:id="topbar" → topbar (Node) + topbarController
    // (Controller)
    @FXML
    private HBox topbar;

    @FXML
    private TopbarController topbarController;

    @FXML
    private ScrollPane contentScrollPane;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        if (topbarController != null) {
            topbarController.setLayoutController(this);
        }

        // Tentukan role dari session, lalu load sidebar + konten default
        if (Session.isLoggedIn() && Session.getUser() != null) {
            if (Session.getUser().isAdmin()) {
                loadSidebar("/includes/SidebarAdmin.fxml");
                navigateTo("/admin/dashboard/Dashboard.fxml",
                        "Dashboard Admin",
                        "Ringkasan operasional GYMBRUT hari ini.");
            } else {
                loadSidebar("/includes/SidebarMember.fxml");
                navigateTo("/member/dashboard/MemberDashboard.fxml",
                        "Dashboard Member",
                        "Selamat datang di GYMBRUT.");
            }
        }
    }

    // ===================================================================
    // NAVIGASI UTAMA — dipanggil oleh SidebarController
    // ===================================================================

    /**
     * Navigasi ke halaman baru: load FXML ke contentArea + update topbar.
     *
     * @param fxmlPath Path absolut FXML konten (misal "/admin/member/Member.fxml")
     * @param title    Judul halaman untuk topbar
     * @param subtitle Subtitle halaman untuk topbar
     */
    public void navigateTo(String fxmlPath, String title, String subtitle) {
        loadContent(fxmlPath);
        if (topbarController != null) {
            topbarController.setPageInfo(title, subtitle);
        }
        // Scroll ke atas setiap ganti halaman
        if (contentScrollPane != null) {
            contentScrollPane.setVvalue(0);
        }
    }

    // ===================================================================
    // LOAD SIDEBAR
    // ===================================================================

    /**
     * Load sidebar dari FXML dan inject referensi LayoutTopController ke
     * sidebar controller agar sidebar bisa memanggil navigateTo().
     *
     * @param fxmlPath Path FXML sidebar ("/includes/SidebarAdmin.fxml" dll)
     */
    private void loadSidebar(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node sidebar = loader.load();

            // Pass referensi diri sendiri ke sidebar controller
            Object ctrl = loader.getController();
            if (ctrl instanceof SidebarAdminController) {
                ((SidebarAdminController) ctrl).setLayoutController(this);
            } else if (ctrl instanceof SidebarMemberController) {
                ((SidebarMemberController) ctrl).setLayoutController(this);
            }

            sidebarContainer.getChildren().clear();
            sidebarContainer.getChildren().add(sidebar);
        } catch (Exception e) {
            System.err.println("Gagal memuat sidebar: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ===================================================================
    // LOAD CONTENT
    // ===================================================================

    /**
     * Load FXML konten ke dalam contentArea (StackPane).
     * Sidebar dan topbar TIDAK di-reload.
     *
     * @param fxmlPath Path FXML konten
     */
    private void loadContent(String fxmlPath) {
        try {
            System.out.println("[LayoutTop] Loading content: " + fxmlPath);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
            System.out.println("[LayoutTop] Content loaded OK: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("Gagal memuat konten: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // ===================================================================
    // GETTERS (untuk akses dari luar jika diperlukan)
    // ===================================================================

    public StackPane getSidebarContainer() {
        return sidebarContainer;
    }

    public StackPane getContentArea() {
        return contentArea;
    }

    public TopbarController getTopbarController() {
        return topbarController;
    }
}
