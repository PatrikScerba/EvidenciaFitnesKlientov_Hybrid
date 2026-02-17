package sk.patrikscerba.servis;

import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.qr.QrServis;
import javax.swing.*;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// Servisná trieda sprostredkujúca operácie nad detailom klienta pre UI vrstvu
public class DetailKlientaServis {

    private final KlientHybridServis klientHybridServis;
    private final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final QrServis qrServis = new QrServis();
    private final AppLogServis appLog = new AppLogServis();

    // Konštruktor s injektovaným hybridným servisom pre prácu s detailom klienta
    public DetailKlientaServis(KlientHybridServis klientHybridServis) {
        this.klientHybridServis = klientHybridServis;
    }

    //Načítanie detail klienta podľa ID
    public Optional<Klient> nacitajDetailKlienta(Long klientId) {
        return klientHybridServis.najdiKlientaPodlaId(klientId);
    }

    //Uloženie úprav klienta s validáciou vstupných údajov
    public void ulozUpravyKlienta(
            Long klientId,
            String krstneMeno,
            String priezvisko,
            String datumNarodenia,
            String telefonneCislo,
            String adresa,
            String email) {

        Klient klient = klientHybridServis.najdiKlientaPodlaId(klientId).orElseThrow(() ->
                new IllegalArgumentException("Klient neexistuje."));

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

        klient.setKrstneMeno(krstneMeno);
        klient.setPriezvisko(priezvisko);
        klient.setTelefonneCislo(telefonneCislo);
        klient.setAdresa(adresa);
        klient.setEmail(email);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        klient.setDatumNarodenia(LocalDate.parse(datumNarodenia, formatter));

        // Uloží aktualizované údaje klienta do databázy aj do XML kópie
        boolean ok = klientHybridServis.aktualizujKlienta(klient);

        if (!ok) {
            appLog.warn("DB neaktualizovala klienta (0 riadkov) | klientId=" + klientId);
            throw new IllegalStateException("Aktualizácia klienta sa nepodarila (0 riadkov).");
        }
        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (IllegalStateException e) {
            appLog.error("DB aktualizovana, ale XML zlyhalo | klientId=" + klientId, e);
            throw new IllegalStateException("Aktualizácia klienta v XML sa nepodarila.", e);
        }
    }

    // Vymazanie klienta podľa ID s kontrolou úspešnosti operácie
    public void vymazatKlienta(Long klientId) {
        boolean vymazane = klientHybridServis.vymazatKlienta(klientId);

        if (!vymazane) {
            appLog.warn("Mazanie klienta zlyhalo (0 riadkov) | klientId=" + klientId);
            throw new IllegalStateException("Klienta sa nepodarilo vymazať.");
        }
    }

    // Vygenerovanie nového QR kódu pre klienta podľa ID
    public String vygenerujNovyQrKod(Long klientId) {
        Klient klient = klientHybridServis.najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        // Zabezpečí nový QR token
        String qrToken = klientHybridServis.vygenerujNovyQrToken(klient);

        // Aktualizácia QR tokenu v databáze a XML
        klientHybridServis.aktualizujQrToken(klientId, qrToken);

        try {
            // Generovanie a uloženie QR obrázka
            String qrCesta = qrServis.vygenerujAUlozQrObrazok(klientId, qrToken);

            // Aktualizácia cesty k QR obrázku v databáze a XML
            klientHybridServis.aktualizujQrCestu(klientId, qrCesta);

            return qrCesta;

        } catch (IllegalStateException e) {
            appLog.error("Zlyhalo generovanie / ulozenie QR | klientId=" + klientId, e);
            throw new IllegalStateException("Nepodarilo sa vygenerovať/uložiť QR kód | klientId=" + klientId, e);
        }
    }

    // Získa a overí uloženú cestu k QR súboru klienta (existuje klient, cesta aj súbor)
    public Path ziskajExistujucuQrCestu(Long klientId) {
        Klient klient = klientHybridServis.najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        String qrCesta = klient.getQrCesta();
        if (qrCesta == null || qrCesta.isBlank()) {
            throw new IllegalStateException("Klient nemá uloženú cestu k QR kódu.");
        }

        Path cesta = Path.of(qrCesta);
        if (!Files.exists(cesta)) {
            throw new IllegalStateException("QR súbor neexistuje: " + cesta);
        }
        return cesta;
    }

    // Načítanie overenej cesty k QR súboru klienta
    public Path nacitajQrCestu(Long klientId) {
        return ziskajExistujucuQrCestu(klientId);
    }

    // Načíta QR ikonu klienta podľa ID, zmení veľkosť a pri chybe vráti Optional.empty()
    public Optional<ImageIcon> nacitajQrIkonu(Long klientId, int sirka, int vyska) {
        try {
            Path cesta = ziskajExistujucuQrCestu(klientId);

            ImageIcon ikona = new ImageIcon(cesta.toString());
            Image img = ikona.getImage().getScaledInstance(sirka, vyska, Image.SCALE_SMOOTH);

            return Optional.of(new ImageIcon(img));
        } catch (IllegalStateException e) {
            return Optional.empty();
        }
    }
}

