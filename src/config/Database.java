package config;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/db_gymbrut";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
<<<<<<< HEAD
=======
            System.out.println("Koneksi database gagal!");
>>>>>>> 7c2aef1530dd76ffab9d85aa76448403dd75423b
            e.printStackTrace();
            return null;
        }
    }
}