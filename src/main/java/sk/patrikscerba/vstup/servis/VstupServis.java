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
    private final PermanentkaVstupServis permanentkaVstupServis = new PermanentkaVstupServis();

    // kontrola vstupu klienta
    public boolean skontrolujVstup(int klientId) {

        Klient klient = ziskajKlienta(klientId);
        if (klient == null) {
            zapisNeuspesnyVstup(klient, klientId, "Klient neexistuje");
            return false;
        }

        if (!maPlatnuPermanentku(klientId)) {
            zapisNeuspesnyVstup(klient, klientId, "Neplatná alebo chýbajúca permanentka");
            return false;
        }

        //Duplicitný vstup-klient môže vstúpiť len raz denne
        if (malDnesVstup(klientId)) {
            zapisNeuspesnyVstup(klient, klientId, "Klient už dnes mal vstup");
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
            e.printStackTrace();

            // fallback: aspoň XML nech je offline stopa
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
        }
    }

    // Logovanie neúspešných pokusov o vstup
    public void zapisNeuspesnyVstup(Klient klient, int klientId, String dovod) {

        // ONLINE: ak nemáme meno/priezvisko, dotiahni ich z DB len pre log
        if (!SystemRezim.isOffline()) {
            if (klient == null || klient.getKrstneMeno() == null || klient.getPriezvisko() == null) {
                Klient identita = klientDao.nacitajIdentituKlienta(klientId);
                if (identita != null) {
                    klient = identita;
                }
            }
        }

        String identita = (klient != null)
                ? (" | meno=" + klient.getKrstneMeno() + " | priezvisko=" + klient.getPriezvisko())
                : "";

        VstupLogServis.zapisLog("NEUSPECH | klientId=" + klientId + identita + " | dovod=" + dovod +
                " | rezim=" + (SystemRezim.isOffline() ? "OFFLINE_XML" : "DB"));
    }

    // Skontroluje platnosť permanentky (OFFLINE = XML, ONLINE = DB)
    private boolean maPlatnuPermanentku(int klientId) {
        LocalDate platnaDo;

        if (SystemRezim.isOffline()) {
            Klient k = xmlNacitanieServis.najdiKlientaVXmlPodlaId(klientId);
            if (k == null) return false;
            platnaDo = k.getPermanentkaPlatnaDo();
        } else {
            platnaDo = klientDao.ziskajPermanentkuPlatnuDoDB(klientId); // doplníme v DAO
        }

        return permanentkaVstupServis.jePlatnaPermanentka(platnaDo);
    }
}
