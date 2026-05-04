package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/gymbrut";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Koneksi database berhasil!");
            return conn;
        } catch (Exception e) {
            System.out.println("Koneksi database gagal!");
            e.printStackTrace();
            return null;
        }
    }
}