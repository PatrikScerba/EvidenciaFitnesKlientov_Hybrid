package sk.patrikscerba.servis;

import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;

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
            throw new IllegalStateException("Aktualizácia klienta sa nepodarila (0 riadkov).");
        }
        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (Exception e) {
            throw new IllegalStateException("Aktualizácia klienta v XML sa nepodarila.", e);
        }
    }

    // Vymazanie klienta podľa ID s kontrolou úspešnosti operácie
    public void vymazatKlienta(Long klientId) {
        boolean vymazane = klientHybridServis.vymazatKlienta(klientId);

        if (!vymazane) {
            throw new IllegalStateException("Klienta sa nepodarilo vymazať.");
        }
    }

    // Načítanie QR ikony klienta podľa ID a zmena jej veľkosti
    public Optional<ImageIcon> nacitajQrIkonu(Long klientId, int sirka, int vyska) {

        Klient klient = klientHybridServis.najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        // Získanie cesty k QR obrázku klienta
        String qrCesta = klient.getQrCesta();
        if (qrCesta == null || qrCesta.isBlank()) {
            return Optional.empty();
        }

        // Overenie existencie súboru na danej ceste
        Path cesta = Path.of(qrCesta);
        if (!Files.exists(cesta)) {
            return Optional.empty();
        }

        // Načítanie QR obrázka zo súboru a zmena veľkostí na požadovaný rozmer
        ImageIcon ikona = new ImageIcon(qrCesta);
        Image img = ikona.getImage().getScaledInstance(sirka, vyska, Image.SCALE_SMOOTH);

        return Optional.of(new ImageIcon(img));
    }
}



