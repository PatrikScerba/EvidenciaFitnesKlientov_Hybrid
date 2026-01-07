package sk.patrikscerba.servis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidaciaKlientaServis {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    //Overí, či je text prázdny alebo null
    public static boolean jePrazdne(String text) {
        return text == null || text.trim().isEmpty();
    }

    // Overí, či text obsahuje len písmená a medzery (povolí zložené mená, vrátane slovenských znakov)
    public static boolean obsahujeLenPismena(String text) {
        if (jePrazdne(text)) return false;
        return text.trim().matches("[\\p{L}]+(\\s+[\\p{L}]+)*");
    }

    //Overí správne zadanie emailu
    public static boolean jePlatnyEmail(String email) {
        if (jePrazdne(email)) return false;
        return email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    //Overí správne zadanie telefónneho čísla (povolí +421 a medzery)
    public static boolean jePlatnyTelefon(String telefon) {
        if (jePrazdne(telefon)) return false;
        return telefon.matches("^\\+?\\d(?:[\\d\\s-]{8,15})$");
    }

    //Overí správne zadanie dátumu vo formáte dd.MM.yyyy
    public static boolean jePlatnyDatum(String datumText) {
        if (jePrazdne(datumText)) return false;
        try {
            LocalDate.parse(datumText.trim(), FORMATTER);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
