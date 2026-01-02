package sk.patrikscerba;

import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.KlientHybridServis;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;
import sk.patrikscerba.vstup.servis.VstupServis;
import java.time.LocalDate;


public class EvidenciaFitnesKlientovApp {

    public static void main(String[] args) {

         //---------------TESTY Vstupov --------------------
        //--------TESTY Vstupov + permanentka --------------

//        SystemRezim.setOffline(false);
//
//        KlientHybridServis hybrid = new KlientHybridServis();
//
//// 1) ---------registrácia bez permanentky------------
//
//        Klient test = new Klient();
//        test.setKrstneMeno("Marian");
//        test.setPriezvisko("Hroznyyyy");
//        test.setEmail("db@test.sk");
//        test.setTelefonneCislo("0999999999");
//        test.setAdresa("Kosice");
//        test.setDatumNarodenia(LocalDate.of(1990, 8, 1));
//
//        int id = hybrid.registrujKlienta(test);
//        System.out.println("DB+XML registrácia OK (id): " + id);
//
////      ------------- update permanentky (samostatne)------------
//
//        LocalDate platnaDo = LocalDate.now().plusDays(30);
//        boolean okPerm = hybrid.nastavPermanentkuPlatnuDo(id, platnaDo);
//        System.out.println("DB+XML update permanentky OK: " + okPerm);
//
//
////      -------------- kontrola XML (rýchlo načítaním)------------
//        XMLNacitanieServis xml = new XMLNacitanieServis();
//        Klient zXml = xml.najdiKlientaVXmlPodlaId(id);
//        System.out.println("XML permanentka: " + (zXml != null ? zXml.getPermanentkaPlatnaDo() : "NULL"));


//        SystemRezim.setOffline(false);
//
//        KlientHybridServis hybrid = new KlientHybridServis();
//
////      --------existujúci klient ID (KLIENT, ktorý je v DB + XML)------------
//        int klientId = 54;
//
////        ---------nový dátum permanentky------------
//        LocalDate novaPlatnost = LocalDate.now().plusDays(60);
//
//        System.out.println("TEST: aktualizácia permanentky (DB + XML)");
//
//        boolean ok = hybrid.nastavPermanentkuPlatnuDo(klientId, novaPlatnost);
//
//        System.out.println("Update OK: " + ok);
//        System.out.println("Očakávaná platnosť: " + novaPlatnost);







    }
}






