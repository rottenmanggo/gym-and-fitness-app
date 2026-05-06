package member.profile;

import config.Database;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import shared.Session;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MemberProfileController {

    @FXML
    private Label completionLabel;
    @FXML
    private Label completionNameLabel;
    @FXML
    private Label messageLabel;

    @FXML
    private ImageView photoImageView;
    @FXML
    private Label profileNameTitleLabel;
    @FXML
    private Label profileEmailTitleLabel;

    @FXML
    private Label statNameLabel;
    @FXML
    private Label statEmailLabel;
    @FXML
    private Label statTargetLabel;
    @FXML
    private Label statBmiLabel;
    @FXML
    private Label statBmiStatusLabel;
    @FXML
    private Label statCreatedLabel;

    @FXML
    private TextField nameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private ComboBox<String> genderCombo;
    @FXML
    private TextField ageField;
    @FXML
    private TextField heightField;
    @FXML
    private TextField weightField;
    @FXML
    private ComboBox<String> targetCombo;

    @FXML
    private Label summaryNameLabel;
    @FXML
    private Label summaryEmailLabel;
    @FXML
    private Label summaryPhoneLabel;
    @FXML
    private Label summaryGenderLabel;
    @FXML
    private Label summaryAgeLabel;
    @FXML
    private Label summaryHeightLabel;
    @FXML
    private Label summaryWeightLabel;
    @FXML
    private Label summaryTargetLabel;

    @FXML
    private PasswordField oldPasswordField;
    @FXML
    private PasswordField newPasswordField;
    @FXML
    private PasswordField confirmPasswordField;

    private int userId = 0;
    private String currentPhoto = null;
    private File selectedPhotoFile = null;

    private final DateTimeFormatter displayDate = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final DecimalFormat decimalFormat = new DecimalFormat("#0.0");

    @FXML
    public void initialize() {
        if (Session.isLoggedIn() && Session.getUser() != null) {
            userId = Session.getUser().getId();
        }

        genderCombo.setItems(FXCollections.observableArrayList(
                "",
                "Laki-laki",
                "Perempuan"));

        targetCombo.setItems(FXCollections.observableArrayList(
                "",
                "Bulking",
                "Cutting",
                "Maintain",
                "Fat Loss",
                "Strength"));

        setDefaultValues();
        loadProfile();
    }

    private void setDefaultValues() {
        completionLabel.setText("0%");
        completionNameLabel.setText("Member");

        profileNameTitleLabel.setText("Member");
        profileEmailTitleLabel.setText("-");

        statNameLabel.setText("-");
        statEmailLabel.setText("-");
        statTargetLabel.setText("-");
        statBmiLabel.setText("-");
        statBmiStatusLabel.setText("-");
        statCreatedLabel.setText("-");

        summaryNameLabel.setText("-");
        summaryEmailLabel.setText("-");
        summaryPhoneLabel.setText("-");
        summaryGenderLabel.setText("-");
        summaryAgeLabel.setText("-");
        summaryHeightLabel.setText("-");
        summaryWeightLabel.setText("-");
        summaryTargetLabel.setText("-");

        loadDefaultPhoto();
    }

    private void loadProfile() {
        if (userId <= 0) {
            showMessage("Session member tidak ditemukan. Silakan login ulang.", "danger");
            return;
        }

        String sql = """
                    SELECT
                        user_id,
                        name,
                        email,
                        phone,
                        gender,
                        age,
                        height,
                        weight,
                        target_fitness,
                        photo,
                        created_at
                    FROM users
                    WHERE user_id = ?
                    LIMIT 1
                """;

        try (
                Connection conn = Database.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    showMessage("Data user tidak ditemukan.", "danger");
                    return;
                }

                String name = safe(rs.getString("name"));
                String email = safe(rs.getString("email"));
                String phone = safe(rs.getString("phone"));
                String gender = safe(rs.getString("gender"));
                String target = safe(rs.getString("target_fitness"));

                int age = rs.getInt("age");
                boolean ageNull = rs.wasNull();

                double height = rs.getDouble("height");
                boolean heightNull = rs.wasNull();

                double weight = rs.getDouble("weight");
                boolean weightNull = rs.wasNull();

                currentPhoto = rs.getString("photo");

                Timestamp createdAt = rs.getTimestamp("created_at");
                String createdText = "-";

                if (createdAt != null) {
                    LocalDateTime createdDate = createdAt.toLocalDateTime();
                    createdText = createdDate.format(displayDate);
                }

                String ageText = ageNull ? "-" : age + " tahun";
                String heightText = heightNull ? "-" : decimalFormat.format(height) + " cm";
                String weightText = weightNull ? "-" : decimalFormat.format(weight) + " kg";
                String targetText = target.isBlank() ? "-" : target;

                String bmiText = "-";
                String bmiStatus = "-";

                if (!heightNull && !weightNull && height > 0) {
                    double heightMeter = height / 100;
                    double bmi = weight / (heightMeter * heightMeter);
                    bmiText = decimalFormat.format(bmi);
                    bmiStatus = getBmiStatus(bmi);
                }

                int completion = calculateCompletion(
                        name,
                        email,
                        phone,
                        gender,
                        ageNull ? "" : String.valueOf(age),
                        heightNull ? "" : String.valueOf(height),
                        weightNull ? "" : String.valueOf(weight),
                        target);

                completionLabel.setText(completion + "%");
                completionNameLabel.setText(name.isBlank() ? "Member" : name);

                profileNameTitleLabel.setText(name.isBlank() ? "Member" : name);
                profileEmailTitleLabel.setText(email.isBlank() ? "-" : email);

                statNameLabel.setText(name.isBlank() ? "-" : name);
                statEmailLabel.setText(email.isBlank() ? "-" : email);
                statTargetLabel.setText(targetText);
                statBmiLabel.setText(bmiText);
                statBmiStatusLabel.setText(bmiStatus);
                statCreatedLabel.setText(createdText);

                nameField.setText(name);
                emailField.setText(email);
                phoneField.setText(phone);
                genderCombo.setValue(gender);
                ageField.setText(ageNull ? "" : String.valueOf(age));
                heightField.setText(heightNull ? "" : decimalFormat.format(height).replace(",", "."));
                weightField.setText(weightNull ? "" : decimalFormat.format(weight).replace(",", "."));
                targetCombo.setValue(target);

                summaryNameLabel.setText(name.isBlank() ? "-" : name);
                summaryEmailLabel.setText(email.isBlank() ? "-" : email);
                summaryPhoneLabel.setText(phone.isBlank() ? "-" : phone);
                summaryGenderLabel.setText(gender.isBlank() ? "-" : gender);
                summaryAgeLabel.setText(ageText);
                summaryHeightLabel.setText(heightText);
                summaryWeightLabel.setText(weightText);
                summaryTargetLabel.setText(targetText);

                loadProfilePhoto(currentPhoto);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal memuat profile.", "danger");
        }
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih Foto Profile");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image File", "*.jpg", "*.jpeg", "*.png", "*.webp"),
                new FileChooser.ExtensionFilter("JPG / JPEG", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("WEBP", "*.webp"));

        File file = fileChooser.showOpenDialog(photoImageView.getScene().getWindow());

        if (file == null) {
            return;
        }

        long maxSize = 2L * 1024L * 1024L;

        if (file.length() > maxSize) {
            showMessage("Ukuran foto maksimal 2MB.", "danger");
            return;
        }

        String ext = getExtension(file.getName());

        if (!isImageExtension(ext)) {
            showMessage("Format foto harus JPG, JPEG, PNG, atau WEBP.", "danger");
            return;
        }

        selectedPhotoFile = file;
        photoImageView.setImage(new Image(file.toURI().toString()));
        showMessage("Foto dipilih. Klik Simpan Foto untuk menyimpan.", "success");
    }

    @FXML
    private void handleSavePhoto() {
        if (selectedPhotoFile == null) {
            showMessage("Pilih foto terlebih dahulu.", "danger");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            String ext = getExtension(selectedPhotoFile.getName());
            String newFileName = "profile_" + userId + "_" + System.currentTimeMillis() + "." + ext;

            Path uploadDir = Path.of("assets", "uploads", "profile");
            Files.createDirectories(uploadDir);

            Path targetPath = uploadDir.resolve(newFileName);
            Files.copy(selectedPhotoFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            String sql = """
                        UPDATE users
                        SET photo = ?
                        WHERE user_id = ?
                    """;

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, newFileName);
                stmt.setInt(2, userId);

                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    currentPhoto = newFileName;
                    selectedPhotoFile = null;
                    showMessage("Foto profile berhasil diperbarui.", "success");
                    loadProfilePhoto(currentPhoto);
                } else {
                    showMessage("Foto profile gagal disimpan.", "danger");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal menyimpan foto profile.", "danger");
        }
    }

    @FXML
    private void handleSaveProfile() {
        String name = value(nameField.getText());
        String email = value(emailField.getText());
        String phone = value(phoneField.getText());
        String gender = value(genderCombo.getValue());
        String target = value(targetCombo.getValue());

        int age = 0;
        double height = 0;
        double weight = 0;

        if (name.isBlank()) {
            showMessage("Nama wajib diisi.", "danger");
            return;
        }

        if (email.isBlank()) {
            showMessage("Email wajib diisi.", "danger");
            return;
        }

        if (!email.contains("@")) {
            showMessage("Format email tidak valid.", "danger");
            return;
        }

        try {
            if (!value(ageField.getText()).isBlank()) {
                age = Integer.parseInt(value(ageField.getText()));

                if (age < 0) {
                    showMessage("Umur tidak boleh negatif.", "danger");
                    return;
                }
            }

            if (!value(heightField.getText()).isBlank()) {
                height = Double.parseDouble(value(heightField.getText()));

                if (height < 0) {
                    showMessage("Tinggi badan tidak boleh negatif.", "danger");
                    return;
                }
            }

            if (!value(weightField.getText()).isBlank()) {
                weight = Double.parseDouble(value(weightField.getText()));

                if (weight < 0) {
                    showMessage("Berat badan tidak boleh negatif.", "danger");
                    return;
                }
            }

        } catch (NumberFormatException e) {
            showMessage("Umur, tinggi, dan berat harus berupa angka.", "danger");
            return;
        }

        String checkEmailSql = """
                    SELECT user_id
                    FROM users
                    WHERE email = ?
                    AND user_id != ?
                    LIMIT 1
                """;

        String updateSql = """
                    UPDATE users
                    SET
                        name = ?,
                        email = ?,
                        phone = ?,
                        gender = ?,
                        age = ?,
                        height = ?,
                        weight = ?,
                        target_fitness = ?
                    WHERE user_id = ?
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                checkStmt.setString(1, email);
                checkStmt.setInt(2, userId);

                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        showMessage("Email sudah digunakan oleh user lain.", "danger");
                        return;
                    }
                }
            }

            try (PreparedStatement stmt = conn.prepareStatement(updateSql)) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, phone.isBlank() ? null : phone);
                stmt.setString(4, gender.isBlank() ? null : gender);

                if (value(ageField.getText()).isBlank()) {
                    stmt.setNull(5, Types.INTEGER);
                } else {
                    stmt.setInt(5, age);
                }

                if (value(heightField.getText()).isBlank()) {
                    stmt.setNull(6, Types.DECIMAL);
                } else {
                    stmt.setDouble(6, height);
                }

                if (value(weightField.getText()).isBlank()) {
                    stmt.setNull(7, Types.DECIMAL);
                } else {
                    stmt.setDouble(7, weight);
                }

                stmt.setString(8, target.isBlank() ? null : target);
                stmt.setInt(9, userId);

                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    showMessage("Profile berhasil diperbarui.", "success");
                    loadProfile();
                } else {
                    showMessage("Gagal memperbarui profile.", "danger");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal memperbarui profile.", "danger");
        }
    }

    @FXML
    private void handleUpdatePassword() {
        String oldPassword = value(oldPasswordField.getText());
        String newPassword = value(newPasswordField.getText());
        String confirmPassword = value(confirmPasswordField.getText());

        if (oldPassword.isBlank() || newPassword.isBlank() || confirmPassword.isBlank()) {
            showMessage("Semua field password wajib diisi.", "danger");
            return;
        }

        if (newPassword.length() < 6) {
            showMessage("Password baru minimal 6 karakter.", "danger");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showMessage("Konfirmasi password tidak sama.", "danger");
            return;
        }

        String getPasswordSql = """
                    SELECT password
                    FROM users
                    WHERE user_id = ?
                    LIMIT 1
                """;

        String updatePasswordSql = """
                    UPDATE users
                    SET password = ?
                    WHERE user_id = ?
                """;

        try (Connection conn = Database.getConnection()) {
            if (conn == null) {
                showMessage("Koneksi database gagal.", "danger");
                return;
            }

            String dbPassword;

            try (PreparedStatement stmt = conn.prepareStatement(getPasswordSql)) {
                stmt.setInt(1, userId);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        showMessage("Data user tidak ditemukan.", "danger");
                        return;
                    }

                    dbPassword = rs.getString("password");
                }
            }

            if (!oldPassword.equals(dbPassword)) {
                showMessage(
                        "Password lama salah. Untuk password hasil register PHP yang terenkripsi, update password dari PHP dulu atau tambahkan BCrypt jar di Java.",
                        "danger");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(updatePasswordSql)) {
                stmt.setString(1, newPassword);
                stmt.setInt(2, userId);

                int affected = stmt.executeUpdate();

                if (affected > 0) {
                    oldPasswordField.clear();
                    newPasswordField.clear();
                    confirmPasswordField.clear();

                    showMessage("Password berhasil diperbarui.", "success");
                } else {
                    showMessage("Gagal mengubah password.", "danger");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Gagal mengubah password.", "danger");
        }
    }

    private void loadProfilePhoto(String photoName) {
        if (photoName == null || photoName.isBlank()) {
            loadDefaultPhoto();
            return;
        }

        try {
            File file = new File("assets/uploads/profile/" + photoName);

            if (file.exists()) {
                photoImageView.setImage(new Image(file.toURI().toString()));
                return;
            }

            loadDefaultPhoto();

        } catch (Exception e) {
            e.printStackTrace();
            loadDefaultPhoto();
        }
    }

    private void loadDefaultPhoto() {
        try {
            photoImageView.setImage(new Image(
                    "https://ui-avatars.com/api/?name=Member&background=ff7a00&color=ffffff"));
        } catch (Exception e) {
            photoImageView.setImage(null);
        }
    }

    private int calculateCompletion(
            String name,
            String email,
            String phone,
            String gender,
            String age,
            String height,
            String weight,
            String target) {
        int total = 8;
        int filled = 0;

        if (!name.isBlank())
            filled++;
        if (!email.isBlank())
            filled++;
        if (!phone.isBlank())
            filled++;
        if (!gender.isBlank())
            filled++;
        if (!age.isBlank())
            filled++;
        if (!height.isBlank())
            filled++;
        if (!weight.isBlank())
            filled++;
        if (!target.isBlank())
            filled++;

        return Math.round((filled * 100f) / total);
    }

    private String getBmiStatus(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25) {
            return "Normal";
        } else if (bmi < 30) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    private void showMessage(String message, String type) {
        messageLabel.setText(message);
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
        messageLabel.getStyleClass().removeAll("alert-success", "alert-danger");

        if ("success".equalsIgnoreCase(type)) {
            messageLabel.getStyleClass().add("alert-success");
        } else {
            messageLabel.getStyleClass().add("alert-danger");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String value(String value) {
        return value == null ? "" : value.trim();
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isImageExtension(String ext) {
        return ext.equals("jpg")
                || ext.equals("jpeg")
                || ext.equals("png")
                || ext.equals("webp");
    }
}