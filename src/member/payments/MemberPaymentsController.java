package member.payments;

import config.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import shared.SceneManager;
import shared.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MemberPaymentsController {

    @FXML
    private TableView<Payment> paymentTable;

    @FXML
    private TableColumn<Payment, Integer> colId;

    @FXML
    private TableColumn<Payment, Double> colAmount;

    @FXML
    private TableColumn<Payment, String> colStatus;

    private ObservableList<Payment> paymentList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadPayments();
    }

    private void loadPayments() {
        try {
            Connection conn = Database.getConnection();

            int userId = Session.getUser().getId();

            String query = """
                SELECT p.id, p.amount, p.status
                FROM payments p
                JOIN memberships m ON p.membership_id = m.id
                WHERE m.user_id = ?
            """;

            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, userId);

            ResultSet rs = ps.executeQuery();

            paymentList.clear();

            while (rs.next()) {
                paymentList.add(new Payment(
                        rs.getInt("id"),
                        rs.getDouble("amount"),
                        rs.getString("status")
                ));
            }

            paymentTable.setItems(paymentList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToUpload() {
        SceneManager.changeScene(
                paymentTable,
                "/member/payments/UploadPayment.fxml",
                "Upload Payment",
                600,
                400
        );
    }
}