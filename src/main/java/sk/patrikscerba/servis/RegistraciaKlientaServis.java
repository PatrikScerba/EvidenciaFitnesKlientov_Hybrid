package sk.patrikscerba.servis;

import sk.patrikscerba.model.Klient;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


//Trieda na registráciu nového klienta
public class RegistraciaKlientaServis {

    private static final DateTimeFormatter FORMAT_DATUMU = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final KlientHybridServis klientHybridServis = new KlientHybridServis();

    //Zaregistruje nového klienta
    public void zaregistrujKlienta(
            String krstneMeno,
            String priezvisko,
            String datumNarodenia,
            String telefonneCislo,
            String adresa,
            String email)
    {

        krstneMeno = krstneMeno.trim();
        priezvisko = priezvisko.trim();
        datumNarodenia = datumNarodenia.trim();
        telefonneCislo = telefonneCislo.trim();
        adresa = adresa.trim();
        email = email.trim();

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

        // Registrácia klienta pomocou hybridného servisu
        klientHybridServis.registrujKlienta(klient);

        System.out.println("Klient zaregistrovaný úspešne: " + krstneMeno + " " + priezvisko);
    }
}




