package sk.patrikscerba.servis;

import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// Servisná trieda sprostredkujúca operácie nad detailom klienta pre UI vrstvu
public class DetailKlientaServis {

    private static final DateTimeFormatter FORMAT_DATUMU = DateTimeFormatter.ofPattern("dd.MM.yyyy");
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
}
