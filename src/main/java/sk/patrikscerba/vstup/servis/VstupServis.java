package sk.patrikscerba.vstup.servis;

import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.vstup.VstupXmlServis;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.vstup.dao.VstupDao;

import java.time.LocalDate;
import java.time.LocalTime;

//Servisná trieda zabezpečujúca kontrolu a evidenciu vstupov klientov do fitnes centra
public class VstupServis {

    private final VstupDao vstupDao = new VstupDao();
    private final VstupXmlServis vstupXmlServis = new VstupXmlServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();
    private final KlientDaoImpl klientDao = new KlientDaoImpl();

    // kontrola vstupu klienta
    public boolean skontrolujVstup(int klientId) {

        Klient klient = ziskajKlienta(klientId);
        if (klient == null) {
            zapisNeuspesnyVstup(klientId, "Klient neexistuje");
            return false;
        }

        //Duplicitný vstup-klient môže vstúpiť len raz denne
        if (malDnesVstup(klientId)) {
            zapisNeuspesnyVstup(klientId, "Klient už dnes mal vstup");
            return false;
        }

        //Zapíš vstup (iba raz!)
        zapisVstup(klientId);
        return true;
    }

    // Zistí klienta podľa režimu (ONLINE = DB, OFFLINE = XML)
    private Klient ziskajKlienta(int klientId) {

        if (!SystemRezim.isOffline()) {

            // ONLINE – overí, či klient existuje v DB
            if (!klientDao.existujeKlient(klientId)) {
                return null;
            }
            return new Klient(klientId);
        }

        // OFFLINE – vyhľadanie v XML
        return xmlNacitanieServis.najdiKlientaVXmlPodlaId(klientId);
    }

    // Skontroluje, či klient už dnes vstúpil (kontrola duplicity podľa režimu)
    private boolean malDnesVstup(int klientId) {
        LocalDate dnes = LocalDate.now();

        if (SystemRezim.isOffline()) {
            return vstupXmlServis.malDnesVstup(klientId, dnes);
        }
        return vstupDao.malDnesVstup(klientId, dnes);
    }

    // Zápis vstupu podľa režimu (OFFLINE = XML, ONLINE = DB + XML cache, pri chybe DB fallback do XML).
    private void zapisVstup(int klientId) {

        LocalDate datum = LocalDate.now();
        LocalTime cas = LocalTime.now();

        if (SystemRezim.isOffline()) {

            // OFFLINE -> iba XML
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
            System.out.println("ZAPIS: OFFLINE -> XML | klientId=" + klientId);
            return;
        }

        // ONLINE -> najprv DB, potom XML cache
        try {
            vstupDao.zapisVstup(klientId, datum);
            System.out.println("ZAPIS: ONLINE -> DB OK | klientId=" + klientId);

            // cache do XML len ak DB prebehla OK (aby nebol nesúlad)
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
            System.out.println("ZAPIS: ONLINE -> XML CACHE OK | klientId=" + klientId);

        } catch (Exception e) {
            System.out.println("CHYBA: DB zapis zlyhal -> zapisujem len XML | klientId=" + klientId);
            e.printStackTrace();

            // fallback: aspoň XML nech je offline stopa
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
        }
    }

    // Zápis neúspešného vstupu
    private void zapisNeuspesnyVstup(int klientId, String dovod) {
        System.out.println("[NEÚSPEŠNÝ VSTUP] Klient ID: " + klientId + " | Dôvod: " + dovod);
    }
}



