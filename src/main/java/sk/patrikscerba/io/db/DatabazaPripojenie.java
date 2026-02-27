package sk.patrikscerba.io.db;

import sk.patrikscerba.io.log.AppLogServis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Trieda pre vytváranie pripojenia k MySQL databáze pomocou JDBC
public class DatabazaPripojenie {

    // Konštanty pre pripojenie k databáze
    private static final String URL = "jdbc:mysql://localhost:3306/fk_evidencia_hybrid";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static final AppLogServis appLog = new AppLogServis();

    // Vráti pripojenie k databáze
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (SQLException e) {
            appLog.error("Zlyhalo pripojenie k databaze | url=" + URL, e);
            throw new IllegalStateException("DB pripojenie zlyhalo.", e);
        }
    }

    // Test pripojenia k databáze
    public static boolean testConnection() {

        try (Connection connection = new DatabazaPripojenie().getConnection()) {
            return connection != null && !connection.isClosed();

        } catch (IllegalStateException e) {
            appLog.warn("Chyba pri testovani pripojenia k databaze:", e);
            return false;

        } catch (Exception e) {
            appLog.warn("Neocakavana chyba pri testovani pripojenia DB:", e);
            return false;
        }
    }
}
