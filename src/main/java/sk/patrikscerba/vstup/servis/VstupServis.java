package sk.patrikscerba.vstup.servis;

import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.vstup.VstupXmlServis;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.vstup.dao.VstupDao;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

// Servisná trieda zabezpečujúca kontrolu a evidenciu vstupov klientov do fitnes centra
public class VstupServis {

    private final VstupDao vstupDao = new VstupDao();
    private final VstupXmlServis vstupXmlServis = new VstupXmlServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();
    private final KlientDaoImpl klientDao = new KlientDaoImpl();
    private final PermanentkaVstupServis permanentkaVstupServis = new PermanentkaVstupServis();
    private final AppLogServis appLog = new AppLogServis();

    // Kontrola vstupu klienta (true = povolený vstup, false = zamietnutý)
    public boolean skontrolujVstup(int klientId) {

        Optional<Klient> klientOpt = ziskajKlienta(klientId);

        if (klientOpt.isEmpty()) {
            zapisNeuspesnyVstup(null, klientId, "Klient neexistuje");
            return false;
        }

        Klient klient = klientOpt.get();

        if (!maPlatnuPermanentku(klientId, klientOpt)) {
            zapisNeuspesnyVstup(klient, klientId, "Neplatná alebo chýbajúca permanentka");
            return false;
        }

        // Duplicitný vstup-klient môže vstúpiť len raz denne
        if (malDnesVstup(klientId)) {
            zapisNeuspesnyVstup(klient, klientId, "Klient už dnes mal vstup");
            return false;
        }

        // Zapíš vstup (iba raz)
        zapisVstup(klientId);
        return true;
    }

    // Zistí klienta podľa režimu (ONLINE = DB, OFFLINE = XML)
    public Optional<Klient> ziskajKlienta(int klientId) {
        long id = klientId;

        // OFFLINE – vyhľadanie v XML
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }

        // ONLINE – DB
        try {
            // Bezpečný fallback, ak zatiaľ nieje "najdiKlientaPodlaId" ako Optional:
            if (!klientDao.existujeKlient(id)) {
                return Optional.empty();
            }

            // Ak existuje, načítaj aspoň identitu (alebo plného klienta)
            Klient klient = klientDao.nacitajIdentituKlienta(klientId);
            return Optional.ofNullable(klient);

        } catch (Exception e) {
            appLog.error("DB chyba pri ziskajKlienta, fallback na XML | klientId=" + klientId, e);
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }
    }

    // Skontroluje, či klient už dnes vstúpil (kontrola duplicity podľa režimu)
    private boolean malDnesVstup(int klientId) {
        LocalDate dnes = LocalDate.now();

        if (SystemRezim.isOffline()) {
            return vstupXmlServis.malDnesVstup(klientId, dnes);
        }
        return vstupDao.malDnesVstup(klientId, dnes);
    }

    // Zápis vstupu podľa režimu (OFFLINE = XML, ONLINE = DB + XML cache, pri chybe DB fallback do XML)
    private void zapisVstup(int klientId) {

        LocalDate datum = LocalDate.now();
        LocalTime cas = LocalTime.now();

        if (SystemRezim.isOffline()) {

            // OFFLINE -> iba XML
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
            appLog.info("ZAPIS: OFFLINE -> XML | klientId=" + klientId);
            return;
        }

        // ONLINE -> najprv DB, potom XML cache
        try {
            vstupDao.zapisVstup(klientId, datum);
            appLog.info("ZAPIS: ONLINE -> DB OK | klientId=" + klientId);

            // cache do XML len ak DB prebehla OK (aby nebol nesúlad)
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
            appLog.info("ZAPIS: ONLINE -> XML CACHE OK | klientId=" + klientId);

        } catch (Exception e) {
            appLog.error("Zlyhanie zápisu do DB, použije sa XML fallback | klientId=" + klientId + " | chyba=", e);

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

        VstupLogServis.zapisLog(
                "NEUSPECH | klientId=" + klientId + identita +
                        " | dovod=" + dovod +
                        " | rezim=" + (SystemRezim.isOffline() ? "OFFLINE_XML" : "DB")
        );
    }

    // Skontroluje platnosť permanentky (OFFLINE = XML, ONLINE = DB)
    private boolean maPlatnuPermanentku(int klientId, Optional<Klient> klientOpt) {
        LocalDate platnaDo;

        if (SystemRezim.isOffline()) {

            // V offline režime berie dátum priamo z klienta z XML (už je v Optional)
            platnaDo = klientOpt.map(Klient::getPermanentkaPlatnaDo).orElse(null);
        } else {

            // ONLINE: načítanie z DB (ako si mal)
            platnaDo = klientDao.ziskajPermanentkuPlatnuDoDB(klientId);
        }

        return permanentkaVstupServis.jePlatnaPermanentka(platnaDo);
    }
}
