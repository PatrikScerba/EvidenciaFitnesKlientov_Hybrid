package sk.patrikscerba;

import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.XMLNacitanieServis;
import sk.patrikscerba.servis.XMLZapisServis;

public class EvidenciaFitnesKlientovApp {
    public static void main(String[] args) {

        //DOČASNÉ  MANUÁLNE TESTOVANIE XML SERVISOV

        /*
        // otestovanie uloženia klienta do XML súboru
        XMLZapisServis xmlZapisServis = new XMLZapisServis();

        Klient klient1 = new Klient();
        klient1.setId(6);
        klient1.setKrstneMeno("Patrik");
        klient1.setPriezvisko("Jain");
        klient1.setEmail("PatrikJain@test.com");
        klient1.setTelefonneCislo("0909124456");
        klient1.setAdresa("Michalovce 1");
        klient1.setDatumNarodenia(LocalDate.of(2000, 5, 20));
        klient1.setDatumRegistracie(LocalDate.now());

        xmlZapisServis.ulozKlienta(klient1);

        System.out.println("Klient bol uložený do XML súboru.");

        //  testovanie načítania klientov z XML súboru
        XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();

        List<Klient> klienti = xmlNacitanieServis.nacitajKlientovZoXML();

        System.out.println("Počet načítaných klientov z XML: " + klienti.size());

        for (Klient klient : klienti) {
            System.out.println(klient.getId() + "|"
                    + klient.getKrstneMeno() + " "
                    + klient.getPriezvisko() + " | "
                    + klient.getDatumNarodenia() + " | "
                    + klient.getDatumRegistracie() + " | "
                    + klient.getEmail());

        /*
        // testovanie vymazania klienta z XML súboru podľa ID
        XMLZapisServis xmlZapisServis = new XMLZapisServis();

        int idKlientaNaVymazanie = 1;

        try {
            xmlZapisServis.vymazatKlientaPodlaId(idKlientaNaVymazanie);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("Klient s ID " + idKlientaNaVymazanie + " bol vymazaný z XML súboru.");


        // testovanie hľadania klienta v XML podľa ID
        XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();

        int testId = 2; // ID, ktoré chceš otestovať

        Klient klient = xmlNacitanieServis.najdiKlientaVXmlPodlaId(testId);

        if (klient == null) {
            System.out.println("Klient s ID " + testId + " nebol nájdený.");
        } else {
            System.out.println("Klient nájdený:");
            System.out.println("ID: " + klient.getId());
            System.out.println("Meno: " + klient.getKrstneMeno() + " " + klient.getPriezvisko());
            System.out.println("Email: " + klient.getEmail());
            System.out.println("Telefón: " + klient.getTelefonneCislo());
            System.out.println("Dátum registrácie: " + klient.getDatumRegistracie());

         */

        // testovanie aktualizácie klienta v XML súbore
        XMLNacitanieServis nacitanie = new XMLNacitanieServis();
        XMLZapisServis zapis = new XMLZapisServis();

        int testId = 4;

        // 1) Načítanie pôvodného klienta
        Klient povodny = nacitanie.najdiKlientaVXmlPodlaId(testId);

        if (povodny == null) {
            System.out.println("Klient s ID " + testId + " neexistuje v XML.");
            return;
        }
        System.out.println("=== PÔVODNÝ KLIENT ===");
        vypisKlienta(povodny);

        // 2) Vytvorenie aktualizovaného klienta
        Klient aktualizovany = new Klient(
                povodny.getId(),
                povodny.getKrstneMeno(),
                povodny.getPriezvisko(),
                povodny.getDatumNarodenia(),
                "0900 111 222",
                povodny.getAdresa(),
                "Patrik.update@gmail.com",
                povodny.getDatumRegistracie()
        );

        // 3) Aktualizovanie do XML súboru
        boolean ok = false;
        try {
            ok = zapis.aktualizujKlientaVXml(aktualizovany);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println(ok
                ? "Update prebehol."
                : "Update NEPREBEHOL (ID sa nenašlo).");

        // 4) Overenie – znovu načítanie klienta z XML a vypísanie
        Klient poZmene = nacitanie.najdiKlientaVXmlPodlaId(testId);

        System.out.println("====== KLIENT PO ZMENE ======");
        vypisKlienta(poZmene);
    }

    // testovanie vypísanie údajov klienta do konzoly
    private static void vypisKlienta(Klient k) {
        System.out.println("ID: " + k.getId());
        System.out.println("Meno: " + k.getKrstneMeno() + " " + k.getPriezvisko());
        System.out.println("Dátum nar.: " + k.getDatumNarodenia());
        System.out.println("Telefón: " + k.getTelefonneCislo());
        System.out.println("Adresa: " + k.getAdresa());
        System.out.println("Email: " + k.getEmail());
        System.out.println("Registrácia: " + k.getDatumRegistracie());
        System.out.println("----------------------------");
        }
    }











