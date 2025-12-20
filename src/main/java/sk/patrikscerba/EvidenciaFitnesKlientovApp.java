package sk.patrikscerba;

import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.KlientHybridServis;
import sk.patrikscerba.servis.SystemRezim;
import java.time.LocalDate;

public class EvidenciaFitnesKlientovApp {
    public static void main(String[] args) {

        /*
        boolean dostupnaDatabaza = sk.patrikscerba.dao.DatabazaPripojenie.testConnection();

        if (!dostupnaDatabaza){

            sk.patrikscerba.servis.SystemRezim.setOffline(true);
            System.out.println("Databáza nie je  nedostupná. Offline režim.");
        }else {
            sk.patrikscerba.servis.SystemRezim.setOffline(false);
            System.out.println("Databáza je dostupná. Online režim.");
        }
    }

         */

        KlientHybridServis klientServis = new KlientHybridServis();

        //Test registrácie
        Klient klient = new Klient(
                0,
                "Patrik",
                "Castle",
                LocalDate.of(1990, 5, 15),
                "0918123456",
                "PatrikCastle@gmail.com",
                "Košice",
                LocalDate.now()

        );

        //online režim
        try {
            SystemRezim.setOffline(false);
            int id = klientServis.registrujKlienta(klient);
            System.out.println("Klient registrovaný s ID: " + id);

        } catch (Exception e) {
            System.out.println("Online registrácia zlyhala: " + e.getMessage());
        }

        //offline režim
        try {
            SystemRezim.setOffline(true);
            klientServis.registrujKlienta(klient);
            System.out.println("Registrácia v offline režime nemôže prejsť");

        } catch (IllegalStateException e) {
            System.out.println("Offline test ok: " + e.getMessage());

        } catch (Exception e) {
            System.out.println("Iná chyba: " + e.getMessage());
        }
    }
}















