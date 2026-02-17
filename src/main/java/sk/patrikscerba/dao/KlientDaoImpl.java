package sk.patrikscerba.dao;

import sk.patrikscerba.io.db.DatabazaPripojenie;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.model.Klient;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


// Trieda zabezpečuje databázové operácie nad klientmi a mapovanie databázových dát na objekt Klient.
public class KlientDaoImpl implements KlientDao {

    private final AppLogServis applog = new AppLogServis();
    private final DatabazaPripojenie databazaPripojenie;

    // Konštruktor - inicializuje pripojenie k databáze
    public KlientDaoImpl() {
        this.databazaPripojenie = new DatabazaPripojenie();
    }

    // Uloží nového klienta do DB
    @Override
    public Long ulozKlienta(Klient klient) {

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

            int rows=preparedStatement.executeUpdate();

            if(rows==0){

                applog.warn("INSERT klienta nevlozil riadok (0 rows) | email=" + klient.getEmail());
                throw new IllegalStateException("Klient sa neuložil (0 riadkov).");
            }

            // Vracia vygenerované ID z databázy po INSERT-e (AUTO_INCREMENT)
            try (ResultSet resultSet = preparedStatement.getGeneratedKeys()) {
                if (resultSet.next()) {
                    return resultSet.getLong(1);
                }
            }
            applog.error("Klient ulozeny, ale bez generovaneho ID | email=" + klient.getEmail(), null);
            throw new IllegalStateException("Klient bol uložený, ale nepodarilo sa získať vygenerované ID.");

        } catch (SQLException e) {
            applog.error("SQL chyba pri ukladani klienta | email=" + klient.getEmail(), e);
            throw new IllegalStateException("Chyba pri ukladaní klienta do databázy: " + e.getMessage(), e);
        }
    }

    // Nájde klienta podľa ID (ak neexistuje, vráti null)
    @Override
    public Optional<Klient> najdiKlientaPodlaId(Long id) {

        String sql =  "SELECT * FROM klienti WHERE id = ?" ;

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {


            preparedStatement.setLong(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Klient klient = mapujKlientaZResultSetu(resultSet);
                    return Optional.of(klient);
                }
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new IllegalStateException("Chyba pri hľadaní klienta podľa ID: " + e.getMessage(), e);
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
            throw new IllegalStateException("Chyba pri načítaní všetkých klientov: " + e.getMessage(), e);
        }
    }

    // Aktualizuje existujúceho klienta podľa ID (vracia true/false podľa toho, či sa niečo zmenilo)
    @Override
    public boolean aktualizujKlienta(Klient klient) {

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

            // Napĺnenie hodnôt do UPDATE podľa poradia
            preparedStatement.setString(1, klient.getKrstneMeno());
            preparedStatement.setString(2, klient.getPriezvisko());
            preparedStatement.setDate(3, Date.valueOf(klient.getDatumNarodenia()));
            preparedStatement.setString(4, klient.getTelefonneCislo());
            preparedStatement.setString(5, klient.getEmail());
            preparedStatement.setString(6, klient.getAdresa());
            preparedStatement.setLong(7, klient.getId());

            // Vráti počet zmenených riadkov
            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                applog.warn("UPDATE klienta nic nezmenil (0 rows) | klientId=" + klient.getId());
                return false;
            }
            return true;

        } catch (SQLException e) {
            applog.error("SQL chyba pri UPDATE klienta | klientId=" + klient.getId(), e);
            throw new IllegalStateException("Chyba pri aktualizácii klienta: " + e.getMessage(), e);
        }
    }

    // Vymaže klienta podľa ID
    @Override
    public boolean vymazatKlienta(Long id) {

        String sql = "DELETE FROM klienti WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, id);

            //Ak sa v databáze vymazal aspoň 1 riadok tak vymazanie prebehlo
            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                applog.warn("DELETE klienta nic nevymazal (0 rows) | klientId=" + id);
                return false;
            }
            return true;

        } catch (SQLException e) {
            applog.error("SQL chyba pri DELETE klienta | klientId=" + id, e);
            throw new IllegalStateException("Chyba pri mazaní klienta: " + e.getMessage(), e);
        }
    }

    // Pomocná metóda: premení 1 riadok z ResultSetu na objekt Klient
    private Klient mapujKlientaZResultSetu(ResultSet resultSet) throws SQLException {

        // Povinné údaje z databázy
        Long id = resultSet.getLong("id");
        String krstneMeno = resultSet.getString("krstne_meno");
        String priezvisko = resultSet.getString("priezvisko");
        LocalDate datumNarodenia = resultSet.getDate("datum_narodenia").toLocalDate();
        String telefonneCislo = resultSet.getString("telefonne_cislo");
        String email = resultSet.getString("email");
        String adresa = resultSet.getString("adresa");
        String qrCesta = resultSet.getString("qr_cesta");
        String qrToken = resultSet.getString("qr_token");

        // Dátum registrácie je v databáze default (CURRENT_DATE)
        // ale pre istotu je ošetrenie null (ak by vrátila prázdnu hodnotu)
        LocalDate datumRegistracie = null;
        Date registracnyDatum = resultSet.getDate("datum_registracie");
        if (registracnyDatum != null) {
            datumRegistracie = registracnyDatum.toLocalDate();
        }
        Date sqlPermanentka = resultSet.getDate("permanentka_platna_do");
        LocalDate permanentkaPlatnaDo = (sqlPermanentka != null) ? sqlPermanentka.toLocalDate() : null;

        Klient klient = new Klient(id, krstneMeno, priezvisko, datumNarodenia, telefonneCislo, adresa, email, datumRegistracie, qrCesta, qrToken);
        klient.setPermanentkaPlatnaDo(permanentkaPlatnaDo);

        return klient;

    }

    // Overí, či klient s daným ID existuje v DB
    public boolean existujeKlient(long klientId) {

        String sql = "SELECT 1 FROM klienti WHERE id = ? LIMIT 1";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setLong(1, klientId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); // true = existuje
            }

        } catch (SQLException e) {
            applog.error("Chyba pri overeni klienta v DB: ", e);
            throw new IllegalStateException("Chyba pri overení existencie klienta v DB.", e);
        }
    }

    // Načíta len krstné meno a priezvisko klienta podľa ID (použité pre logovanie)
    public Klient nacitajIdentituKlienta(Long klientId) {
        String sql = "SELECT krstne_meno, priezvisko, qr_token FROM klienti WHERE id = ? LIMIT 1";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, klientId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Klient klient = new Klient(klientId);
                    klient.setKrstneMeno(resultSet.getString("krstne_meno"));
                    klient.setPriezvisko(resultSet.getString("priezvisko"));
                    klient.setQrToken(resultSet.getString("qr_token"));
                    return klient;
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Chyba pri načítaní identity klienta z DB: " + e.getMessage(), e);
        }
        return null;
    }

    // Aktualizuje platnosť permanentky klienta
    @Override
    public boolean aktualizujPermanentkuPlatnuDo(Long id, LocalDate platnaDo) {
        String sql = "UPDATE klienti SET permanentka_platna_do = ? WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            if (platnaDo == null) {
                preparedStatement.setNull(1, java.sql.Types.DATE);
            } else {
                preparedStatement.setDate(1, java.sql.Date.valueOf(platnaDo));
            }

            preparedStatement.setLong(2, id);
            return preparedStatement.executeUpdate() > 0;

        } catch (SQLException e) {
            applog.error("SQL chyba pri UPDATE qr_cesta | klientId=" + id, e);
            throw new IllegalStateException("Chyba pri aktualizácii platnosti permanentky: " + e.getMessage(), e);
        }
    }

    // Získa dátum platnosti permanentky klienta podľa ID
    public LocalDate ziskajPermanentkuPlatnuDoDB(Long klientId) {
        String sql = "SELECT permanentka_platna_do FROM klienti WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setLong(1, klientId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Date d = resultSet.getDate("permanentka_platna_do");
                    return (d != null) ? d.toLocalDate() : null;
                }
            }
            return null;

        } catch (SQLException e) {
            applog.error("Chyba pri ziskani platnosti permanentky z DB | klientId=" + klientId, e);
            throw new IllegalStateException("Chyba pri načítaní platnosti permanentky z DB.", e);
        }
    }


    // Aktualizuje cestu k QR kódu klienta podľa ID
    @Override
    public boolean aktualizujQrCestu(Long id, String qrCesta) {

        String sql = "UPDATE klienti SET qr_cesta = ? WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, qrCesta);
            preparedStatement.setLong(2, id);

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                applog.warn("UPDATE qr_cesta nic nezmenil (0 rows) | klientId=" + id);
                return false;
            }
            return true;

        } catch (SQLException e) {
            applog.error("SQL chyba pri UPDATE qr_cesta | klientId=" + id, e);
            throw new IllegalStateException("Chyba pri aktualizácií QR cesty | id=" + id, e);
        }
    }

    // Aktualizuje QR token klienta
    @Override
    public boolean aktualizujQrToken(Long id, String qrToken) {

        String sql = "UPDATE klienti SET qr_token = ? WHERE id = ?";

        try (Connection connection = databazaPripojenie.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, qrToken);
            preparedStatement.setLong(2, id);

            int rows = preparedStatement.executeUpdate();

            if (rows == 0) {
                applog.warn("UPDATE qr_token nic nezmenil (0 rows) | klientId=" + id);
                return false;
            }
            return true;

        } catch (SQLException e) {
            applog.error("SQL chyba pri UPDATE qr_token | klientId=" + id, e);
            throw new IllegalStateException("Chyba pri aktualizácií QR tokenu | id=" + id, e);
        }
    }
}
