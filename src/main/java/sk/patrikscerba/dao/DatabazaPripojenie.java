package sk.patrikscerba.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


// Trieda pre vytváranie pripojenia k MySQL databáze pomocou JDBC
public class DatabazaPripojenie {

    // Konštanty pre pripojenie k databáze
    private static final String URL = "jdbc:mysql://localhost:3306/fk_evidencia_hybrid";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Vráti pripojenie k databáze
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    //Test pripojenia k databáze
    public static boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (Exception e) {
            return false;

        }
    }
}
