package sk.patrikscerba;

import sk.patrikscerba.dao.DatabazaPripojenie;
import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.model.Klient;
import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public class EvidenciaFitnesKlientovApp {
    public static void main(String[] args) {
        testDbConnection();
    }

    //Overí pripojenie k databáze a vypíše výsledok do konzoly
    private static void testDbConnection() {
        try(Connection connection = DatabazaPripojenie.getConnection()){
            if (connection != null){
                System.out.println("Pripojenie úspešné");

            }else {
                System.out.println("pripojenie zlyhalo");
            }
        }catch (Exception e ){
            System.out.println("chyba pri pripájaní " + e.getMessage());
        }

        // otestovanie CRUD operácií
        KlientDao klientDao = new KlientDaoImpl();

        /*
        ------  otestovanie uloženie klienta do databázy ------------

        Klient novyKlient = new Klient();

        novyKlient.setKrstneMeno("PatrikTest");
        novyKlient.setPriezvisko("ScerbaTest");
        novyKlient.setDatumNarodenia(LocalDate.of(1990, 1, 15));
        novyKlient.setTelefonneCislo("0900123456");
        novyKlient.setAdresa("Kosice 1");
        novyKlient.setEmail("test@patrik.sk");

        novyKlient.setKrstneMeno("PatrikTest2");
        novyKlient.setPriezvisko("ScerbaTest2");
        novyKlient.setDatumNarodenia(LocalDate.of(1990, 1, 15));
        novyKlient.setTelefonneCislo("0900123456");
        novyKlient.setAdresa("Kosice 1");
        novyKlient.setEmail("test@patrik.sk");

        int id = klientDao.ulozKlienta(novyKlient);
        System.out.println("Uložené ID: " + id);

       ------- otetovanie načítanie klienta z databázy -------

       Klient nacitanyKlient = klientDao.najdiKlientaPodlaId(id);
       System.out.println("Načítaný klient: "
              + nacitanyKlient.getId() + " | "
              + nacitanyKlient.getKrstneMeno() + " "
              + nacitanyKlient.getPriezvisko() + " | "
              + nacitanyKlient.getEmail());

        ------- otestovanie načítania všetkých klientov z databázy ------

        List<Klient>klienti = klientDao.ziskajVsetkychKlientov();

        System.out.println("Počet klientov:" + klienti.size());

        for (Klient klient : klienti){
            System.out.println(
                    klient.getId() + " |" +
                    klient.getKrstneMeno() + " " +
                    klient.getPriezvisko() + " " +
                    klient.getEmail() + " " +
                    klient.getDatumRegistracie()
            );
        }

        ------ otestovanie aktualizovanie klienta a uloženie do databázy -------

        Klient klient = klientDao.najdiKlientaPodlaId(3); // existujúce ID

        if (klient == null) {
            System.out.println("Klient neexistuje");
            return;
        }
        klient.setTelefonneCislo("0911222333");
        klient.setEmail("update@test.sk");
        klient.setAdresa("Michalovce – update");

        boolean uspesne = klientDao.aktualizujKlienta(klient);

        System.out.println(uspesne
                ? "Klient bol úspešne aktualizovaný"
                : "Aktualizácia zlyhala");

        ------ otestovanie vymazanie klienta z databázy -------

        int idVymazanieKlienta = 7;
        int klientVymazany = idVymazanieKlienta;

        boolean vymazanie = klientDao.vymazatKlienta(idVymazanieKlienta);
        System.out.println(vymazanie ? "Klient s Id" + " " + klientVymazany + " " + "sa vymazal!" :
                "klient neexistuje alebo bol vymazaný! ");
    */

    }
}

