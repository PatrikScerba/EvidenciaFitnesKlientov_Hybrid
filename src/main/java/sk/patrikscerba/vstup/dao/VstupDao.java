package sk.patrikscerba.vstup.dao;

import sk.patrikscerba.io.db.DatabazaPripojenie;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;


public class VstupDao {

    // Zaznamenanie vstupu klienta do databázy
    public void zapisVstup(int klientId, LocalDate datum) {
        String sql = "INSERT INTO vstupy(klient_id, datum) VALUES (?, ? )";

        try (Connection connection = DatabazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, klientId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(datum));


            preparedStatement.executeUpdate();

        } catch (Exception e) {
            System.err.println("Chyba pri zápise vstupu do DB: " + e.getMessage());
        }
    }

    // Kontrola, či má klient dnes zaznamenaný vstup v databáze
    public boolean malDnesVstup(int klientId, LocalDate datum){

        String sql = "SELECT COUNT(*) FROM vstupy WHERE klient_id = ? AND datum = ?";

        try(Connection connection = DatabazaPripojenie.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql)){

            preparedStatement.setInt(1, klientId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(datum));

            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();

            return resultSet.getInt(1) > 0;
        } catch (Exception e){
            System.err.println("Chyba pri kontrole dnešného vstupu: " + e.getMessage());
            return false;
        }
    }
}
