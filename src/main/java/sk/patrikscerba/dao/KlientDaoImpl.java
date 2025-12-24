package sk.patrikscerba.dao;

import sk.patrikscerba.io.db.DatabazaPripojenie;
import sk.patrikscerba.model.Klient;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


//Trieda zabezpečuje databázové operácie nad klientmi a mapovanie databázových dát na objekt Klient.
public class KlientDaoImpl implements KlientDao {


    private final DatabazaPripojenie databazaPripojenie;

    // Konštruktor - inicializuje pripojenie k databáze
    public KlientDaoImpl() {
        this.databazaPripojenie = new DatabazaPripojenie();
    }

    // Uloží nového klienta do DB
    @Override
    public int ulozKlienta(Klient klient) {

        //Použitie PreparedStatement kvôli bezpečnosti ( SQL injection )
        String sql = """
                INSERT INTO klienti (krstne_meno, priezvisko, datum_narodenia, telefonne_cislo, adresa, email)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        //try-with-resources = Connection aj PreparedStatement s automatický zatvoria
        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            //Napĺnenie hodnôt do SQL podľa poradia v INSERT
            preparedStatement.setString(1, klient.getKrstneMeno());
            preparedStatement.setString(2, klient.getPriezvisko());
            preparedStatement.setDate(3, Date.valueOf(klient.getDatumNarodenia()));
            preparedStatement.setString(4, klient.getTelefonneCislo());
            preparedStatement.setString(5, klient.getAdresa());
            preparedStatement.setString(6, klient.getEmail());


            preparedStatement.executeUpdate();

            // Vracia vygenerované ID z databázy po INSERT-e (AUTO_INCREMENT)
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }

            //Ak sa ID nepodarilo získať, vrátime -1 (signalizuje problém)
            return -1;

        } catch (SQLException e) {
            throw new RuntimeException("Chyba pri ukladaní klienta do databázy: " + e.getMessage(), e);
        }
    }

    // Nájde klienta podľa ID (ak neexistuje, vráti null)
    @Override
    public Klient najdiKlientaPodlaId(int id){

        String sql = "SELECT * FROM klienti WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            preparedStatement.setInt(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapujKlientaZResultSetu(resultSet);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Chyba pri hľadaní klienta podľa ID: " + e.getMessage(), e);
        }
    }

    // Načíta všetkých klientov (zoradené podľa dátumu registrácie od najnovších)
    @Override
    public List<Klient> ziskajVsetkychKlientov() {

        String sql = "SELECT * FROM klienti ORDER BY datum_registracie DESC";
        List<Klient> klienti = new ArrayList<>();

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            //Prejde všetky riadky a každý zmapuje na objekt
            while (resultSet.next()) {
                klienti.add(mapujKlientaZResultSetu(resultSet));
            }

            return klienti;

        } catch (SQLException e) {
            throw new RuntimeException("Chyba pri načítaní všetkých klientov: " + e.getMessage(), e);
        }
    }

    // Aktualizuje existujúceho klienta podľa ID (vracia true/false podľa toho, či sa niečo zmenilo)
    @Override
    public boolean aktualizujKlienta(Klient klient){

        String sql = """
                UPDATE klienti SET
                    krstne_meno = ?,
                    priezvisko = ?,
                    datum_narodenia = ?,
                    telefonne_cislo = ?,
                    email = ?,
                    adresa = ?
                WHERE id = ?
                """;

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            //Napĺnenie hodnôt do UPDATE podľa poradia
            preparedStatement.setString(1, klient.getKrstneMeno());
            preparedStatement.setString(2, klient.getPriezvisko());
            preparedStatement.setDate(3, Date.valueOf(klient.getDatumNarodenia()));
            preparedStatement.setString(4, klient.getTelefonneCislo());
            preparedStatement.setString(5, klient.getEmail());
            preparedStatement.setString(6, klient.getAdresa());
            preparedStatement.setInt(7, klient.getId());

            //Vráti počet zmenených riadkov
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Chyba pri aktualizácii klienta: " + e.getMessage(), e);
        }
    }

    // Vymaže klienta podľa ID
    @Override
    public boolean vymazatKlienta(int id) {

        String sql = "DELETE FROM klienti WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);

            //Ak sa v databáze vymazal aspoň 1 riadok tak vymazanie prebehlo
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Chyba pri mazaní klienta: " + e.getMessage(), e);
        }
    }

    // Pomocná metóda: premení 1 riadok z ResultSetu na objekt Klient
    private Klient mapujKlientaZResultSetu(ResultSet resultSet) throws SQLException {

        // Povinné údaje z databázy
        int id = resultSet.getInt("id");
        String krstneMeno = resultSet.getString("krstne_meno");
        String priezvisko = resultSet.getString("priezvisko");
        LocalDate datumNarodenia = resultSet.getDate("datum_narodenia").toLocalDate();
        String telefonneCislo = resultSet.getString("telefonne_cislo");
        String email = resultSet.getString("email");
        String adresa = resultSet.getString("adresa");

        // Dátum registrácie je v databáze default (CURRENT_DATE)
        //ale pre istotu je ošetrenie null (ak by vrátila prázdnu hodnotu)
        LocalDate datumRegistracie = null;
        Date registracnyDatum = resultSet.getDate("datum_registracie");
        if (registracnyDatum != null) {
            datumRegistracie = registracnyDatum.toLocalDate();
        }

        // Objekt Klient s všetkými údajmi
        return new Klient(id, krstneMeno, priezvisko, datumNarodenia, telefonneCislo, adresa, email, datumRegistracie);
    }

    // Overí, či klient s daným ID existuje v DB
    public boolean existujeKlient(int klientId){

        String sql = "SELECT 1 FROM klienti WHERE id = ? LIMIT 1";

        try (Connection connection = DatabazaPripojenie.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, klientId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true = existuje
            }

        } catch (Exception e) {
            System.err.println("Chyba pri overeni klienta v DB: " + e.getMessage());
            return false;
        }
    }

    // Načíta len krstné meno a priezvisko klienta podľa ID (použité pre logovanie)
    public Klient nacitajIdentituKlienta(int klientId) {
        String sql = "SELECT krstne_meno, priezvisko FROM klienti WHERE id = ? LIMIT 1";

        try (Connection connection = DatabazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, klientId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Klient klient = new Klient(klientId);
                    klient.setKrstneMeno(resultSet.getString("krstne_meno"));
                    klient.setPriezvisko(resultSet.getString("priezvisko"));
                    return klient;
                }
            }

        } catch (Exception e) {
            System.err.println("Chyba pri nacitani identity klienta z DB: " + e.getMessage());
        }
        return null;
    }
}
