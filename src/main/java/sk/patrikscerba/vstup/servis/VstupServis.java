package sk.patrikscerba.vstup.servis;

import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.vstup.VstupXmlServis;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.vstup.dao.VstupDao;
import sk.patrikscerba.vstup.model.VstupVysledok;

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


    // Hlavná kontrola vstupu – používa sa pri skenovaní QR kódu
    public boolean skontrolujVstup(Long klientId, String qrToken) {

        if (klientId == null) {
            zapisNeuspesnyVstup(null, null, "Chyba klientId");
            return false;
        }

        if (qrToken == null || qrToken.isBlank()) {
            zapisNeuspesnyVstup(null, klientId, "Chyba QR token");
            return false;
        }

        Optional<Klient> klientOpt = ziskajKlienta(klientId);

        if (klientOpt.isEmpty()) {
            zapisNeuspesnyVstup(null, klientId, "Klient neexistuje");
            return false;
        }

        Klient klient = klientOpt.get();

        // Token zdroj: OFFLINE = XML, ONLINE = DB (alebo fallback z XML)
        String tokenZdroj = klient.getQrToken();
        if (tokenZdroj == null || tokenZdroj.isBlank()) {
            zapisNeuspesnyVstup(klient, klientId, "Klient nema ulozeny QR token");
            return false;
        }

        // Presné porovnanie QR tokenu zo systému s tokenom zo skenu
        if (!tokenZdroj.equals(qrToken.trim())) {
            zapisNeuspesnyVstup(klient, klientId, "Neplatny QR token");
            return false;
        }

        // Token sedí, pokračuje plnou kontrolou (permanentka, duplicita, zápis, logy)
        return skontrolujVstupPreKlienta(klient, klientId);
    }

    // Načíta klienta podľa aktuálneho režimu (ONLINE = DB, OFFLINE = XML)
    public Optional<Klient> ziskajKlienta(Long klientId) {

        if (klientId == null) {
            return Optional.empty();
        }

        // OFFLINE – vyhľadanie v XML
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(klientId);
        }

        // ONLINE – DB + fallback na XML pri chybe
        try {
            Klient klient = klientDao.nacitajIdentituKlienta(klientId);
            return Optional.ofNullable(klient);

        } catch (Exception e) {
            appLog.error("DB chyba pri nacitani klienta, fallback na XML | klientId=" + klientId, e);
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(klientId);
        }
    }

    // Vnútorná kontrola vstupu pre už načítaného klienta
    private boolean skontrolujVstupPreKlienta(Klient klient, Long klientId) {

        // Kontrola platnosti permanentky klienta.
        if (!maPlatnuPermanentku(klientId, klient)) {
            zapisNeuspesnyVstup(klient, klientId, "Neplatna alebo chybajuca permanentka");
            return false;
        }

        // Duplicitný vstup - klient môže vstúpiť len raz denne
        if (malDnesVstup(klientId)) {
            zapisNeuspesnyVstup(klient, klientId, "Klient uz dnes mal vstup");
            return false;
        }

        // Zapíš vstup (iba raz)
        zapisVstup(klientId);
        zapisUspesnyVstup(klient, klientId);
        return true;
    }

    // Skontroluje, či klient už dnes vstúpil (kontrola duplicity podľa režimu)
    private boolean malDnesVstup(Long klientId) {

        LocalDate dnes = LocalDate.now();

        if (SystemRezim.isOffline()) {
            return vstupXmlServis.malDnesVstup(klientId, dnes);
        }
        boolean vDatabaze = vstupDao.malDnesVstup(klientId, dnes);
        boolean vXml = vstupXmlServis.malDnesVstup(klientId, dnes);

        return vDatabaze || vXml;
    }

    // Zápis vstupu podľa režimu (OFFLINE = XML, ONLINE = DB + XML cache, pri chybe DB fallback do XML)
    private void zapisVstup(Long klientId) {

        LocalDate datum = LocalDate.now();
        LocalTime cas = LocalTime.now();

        if (SystemRezim.isOffline()) {

            // OFFLINE -> iba XML
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
            appLog.info("ZAPIS: OFFLINE -> XML | klientId=" + klientId);
            return;
        }

        // ONLINE: najprv DB, potom XML cache
        try {
            vstupDao.zapisVstup(klientId, datum);
            appLog.info("ZAPIS: ONLINE -> DB OK | klientId=" + klientId);

            // cache do XML len ak DB prebehla OK (aby nebol nesúlad)
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
            appLog.info("ZAPIS: ONLINE -> XML CACHE OK | klientId=" + klientId);

        } catch (Exception e) {
            appLog.error("VSTUP: DB zlyhala, pouzije sa XML fallback | klientId=" + klientId, e);
        }
        // fallback: aspoň XML nech je offline stopa
        try {
            vstupXmlServis.zapisVstupXML(klientId, datum, cas);
        } catch (Exception ex) {
            appLog.error("VSTUP: zlyhal aj XML fallback | klientId=" + klientId, ex);
        }
    }

    // Zapíše log neúspešného vstupu (dôvod + režim)
    // Ak nieje meno/priezvisko, pokúsi sa doplniť identitu z DB (iba v ONLINE)
    public void zapisNeuspesnyVstup(Klient klient, Long klientId, String dovod) {

        if (!SystemRezim.isOffline() && klientId != null) {
            if (klient == null || klient.getKrstneMeno() == null || klient.getPriezvisko() == null) {

                try {
                    Klient identita = klientDao.nacitajIdentituKlienta(klientId);
                    if (identita != null) {
                        klient = identita;
                    }
                } catch (Exception e) {
                    appLog.error("DB chyba pri dotiahnuti identity do logu | klientId=" + klientId, e);
                }
            }
        }

        String identita = (klient != null)
                ? (" | meno=" + klient.getKrstneMeno() + " | priezvisko=" + klient.getPriezvisko())
                : "";

        VstupLogServis.zapisLog(
                "NEUSPECH | klientId=" + klientId + identita +
                        " | dovod=" + dovod +
                        " | rezim=" + (SystemRezim.isOffline() ? "OFFLINE_XML" : "ONLINE_DB")
        );
    }

    // Logovanie úspešných vstupov (identita + režim)
    public void zapisUspesnyVstup(Klient klient, Long klientId) {

        String identita = (klient != null)
                ? (" | meno=" + klient.getKrstneMeno() + " | priezvisko=" + klient.getPriezvisko())
                : "";

        VstupLogServis.zapisLog(
                "USPECH | klientId=" + klientId + identita +
                        " | dovod=Platna permanentka a vstup povoleny" +
                        " | rezim=" + (SystemRezim.isOffline() ? "OFFLINE_XML" : "ONLINE_DB")
        );
    }

    // Skontroluje platnosť permanentky (OFFLINE = XML, ONLINE = DB)
    // OFFLINE: Zoberie dátum z XML
    // ONLINE: Porovná DB a XML a použije neskorší dátum (aby stará cache nezablokovala klienta)
    private boolean maPlatnuPermanentku(Long klientId, Klient klient) {

        LocalDate platnaDoXml = (klient != null) ? klient.getPermanentkaPlatnaDo() : null;

        if (SystemRezim.isOffline()) {

            // OFFLINE – platí to, čo je v XML
            return permanentkaVstupServis.jePlatnaPermanentka(platnaDoXml);
        }

        // ONLINE: zoberie aj DB aj XML a použije neskorší (platnejší) dátum, aby cache neblokovala klienta
        LocalDate platnaDoDb = (klientId != null) ? klientDao.ziskajPermanentkuPlatnuDoDB(klientId) : null;

        LocalDate platnaDo = vyberNeskorsiDatum(platnaDoDb, platnaDoXml);

        return permanentkaVstupServis.jePlatnaPermanentka(platnaDo);
    }

    // Porovná dátum permanentky z DB a XML a vráti neskorší (platnejší) dátum
    private LocalDate vyberNeskorsiDatum(LocalDate prvyDatum, LocalDate druhyDatum) {
        if (prvyDatum == null)
            return druhyDatum;

        if (druhyDatum == null)
            return prvyDatum;

        return prvyDatum.isAfter(druhyDatum) ? prvyDatum : druhyDatum;
    }

    // Pomocná metóda pre desktop simuláciu (Scanner okno)
    // Vracia priamo text pre UI (aby UI trieda nemusela riešiť detailnú logiku)
    public VstupVysledok simulujVstupCezScanner(Long klientId) {

        Optional<Klient> klientOpt = ziskajKlienta(klientId);

        if (klientOpt.isEmpty()) {
            zapisNeuspesnyVstup(null, klientId, "Klient neexistuje");

            return new VstupVysledok(
                    false,
                    "Vstup zamietnutý.\nSkontrolujte históriu vstupov klienta.");
        }

        Klient klient = klientOpt.get();

        String token = klient.getQrToken();
        if (token == null || token.isBlank()) {
            zapisNeuspesnyVstup(klient, klientId, "Klient nemá uložený QR token");

            return new VstupVysledok(
                    false,
                    "Vstup zamietnutý.\nSkontrolujte históriu vstupov klienta.");
        }

        boolean ok = skontrolujVstup(klientId, token);

        if (ok) {
            return new VstupVysledok(
                    true,
                    "Vstup povolený.");
        } else {

            return new VstupVysledok(
                    false,
                    "Vstup zamietnutý.\nSkontrolujte históriu vstupov klienta.");
        }
    }
}

