package sk.patrikscerba.vstup.dao;

import sk.patrikscerba.io.db.DatabazaPripojenie;
import sk.patrikscerba.io.log.AppLogServis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;


public class VstupDao {

    private final AppLogServis applog = new AppLogServis();

    private final DatabazaPripojenie databazaPripojenie;

    // Konštruktor - inicializuje DB pripojenie
    public VstupDao() {
        this.databazaPripojenie = new DatabazaPripojenie();
    }

    // Zaznamenanie vstupu klienta do databázy
    public void zapisVstup(Long klientId, LocalDate datum) {
        String sql = "INSERT INTO vstupy(klient_id, datum) VALUES (?, ? )";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, klientId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(datum));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            applog.error("Chyba pri zápise vstupu do DB | klientId=" + klientId, e);
            throw new IllegalStateException("Nepodarilo sa zapísať vstup do databázy.", e);
        }
    }


    // Kontrola, či má klient dnes zaznamenaný vstup v databáze
    public boolean malDnesVstup(Long klientId, LocalDate datum) {

        String sql = "SELECT COUNT(*) FROM vstupy WHERE klient_id = ? AND datum = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, klientId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(datum));

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            applog.error("Chyba pri kontrole dnešného vstupu | klientId=" + klientId, e);
            throw new IllegalStateException("Nepodarilo sa overiť dnešný vstup klienta.", e);
        }
    }
}



