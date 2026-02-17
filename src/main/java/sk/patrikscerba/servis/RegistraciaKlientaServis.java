package sk.patrikscerba.servis;

import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.qr.QrServis;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

//Trieda na registráciu nového klienta
public class RegistraciaKlientaServis {

    private static final DateTimeFormatter FORMAT_DATUMU = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final KlientHybridServis klientHybridServis = new KlientHybridServis();
    private final QrServis qrServis = new QrServis();
    private AppLogServis appLog = new AppLogServis();

    //Zaregistruje nového klienta
    public Long zaregistrujKlienta(
            String krstneMeno,
            String priezvisko,
            String datumNarodenia,
            String telefonneCislo,
            String adresa,
            String email) {

        krstneMeno = krstneMeno.trim();
        priezvisko = priezvisko.trim();
        datumNarodenia = datumNarodenia.trim();
        telefonneCislo = telefonneCislo.trim();
        adresa = adresa.trim();
        email = email.trim();

        // Validácie vstupných údajov
        if (!ValidaciaKlientaServis.obsahujeLenPismena(krstneMeno)) {
            throw new IllegalArgumentException("Neplatné krstné meno. Môže obsahovať len písmená.");
        }

        if (!ValidaciaKlientaServis.obsahujeLenPismena(priezvisko)) {
            throw new IllegalArgumentException("Neplatné priezvisko. Môže obsahovať len písmená.");
        }

        if (!ValidaciaKlientaServis.jePlatnyDatum(datumNarodenia)) {
            throw new IllegalArgumentException("Neplatný dátum narodenia. Použi formát dd.MM.yyyy (napr. 15.06.1995).");
        }

        if (!ValidaciaKlientaServis.jePlatnyTelefon(telefonneCislo)) {
            throw new IllegalArgumentException("Neplatný format  telefónneho čísla.");
        }

        if (ValidaciaKlientaServis.jePrazdne(adresa)) {
            throw new IllegalArgumentException("Pole nesmie byť prázdne.");
        }

        if (!ValidaciaKlientaServis.jePlatnyEmail(email)) {
            throw new IllegalArgumentException("Neplatný formát emailu.");
        }

        LocalDate datum;
        try {
            datum = LocalDate.parse(datumNarodenia, FORMAT_DATUMU);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Neplatný dátum narodenia. Použi formát dd.MM.yyyy (napr. 15.06.1995).", e);
        }

        // Vytvorenie objektu Klient
        Klient klient = new Klient(
                krstneMeno,
                priezvisko,
                datum,
                telefonneCislo,
                adresa,
                email);

        klient.setDatumRegistracie(LocalDate.now());

        // Registrácia klienta pomocou hybridného servisu
        Long id = klientHybridServis.registrujKlienta(klient);

        klient.setId(id);

        // Token cez zabezpečenú metódu
        String token = klientHybridServis.zabezpecQrToken(klient);

        // Aktualizácia QR tokenu v databáze a XML
        try {
            klientHybridServis.aktualizujQrToken(id, token);
        } catch (IllegalStateException e) {
            appLog.error("Zlyhalo ulozenie QR tokenu (DB/XML) | klientId=" + id, e);
            throw new IllegalStateException("Nepodarilo sa uložiť QR token | klientId=" + id, e);
        }

        // Generovanie a uloženie QR obrázka
        String qrCesta;
        try {
            qrCesta = qrServis.vygenerujAUlozQrObrazok(id, token);
        } catch (IllegalStateException e) {
            appLog.error("Zlyhalo generovanie/ulozenie QR obrazka | klientId=" + id, e);
            throw new IllegalStateException("Nepodarilo sa vygenerovať/uložiť QR obrázok | klientId=" + id, e);
        }

        // Aktualizácia cesty k QR obrázku v databáze a XML
        try {
            klientHybridServis.aktualizujQrCestu(id, qrCesta);
        } catch (IllegalStateException e) {
            appLog.error("Zlyhalo ulozenie QR cesty (DB/XML) | klientId=" + id, e);
            throw new IllegalStateException("Nepodarilo sa uložiť QR cestu | klientId=" + id, e);
        }
        return id;
    }
}




