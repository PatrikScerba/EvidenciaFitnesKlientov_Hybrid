package sk.patrikscerba.servis;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.system.SystemRezim;
import java.time.LocalDate;
import java.util.List;


public class KlientHybridServis {

    private final KlientDao klientDao = new KlientDaoImpl();
    private final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();
    private  final AppLogServis appLog = new AppLogServis();

    // Získanie všetkých klientov s podporou hybridného režimu (DB / XML)
    public List<Klient> ziskajVsetkychKlientov() {

        //Ofline rezim - nacitanie z XML suboru
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.nacitajKlientovZoXML();
        }
        //Online režim-načítanie z databázy
        return klientDao.ziskajVsetkychKlientov();
    }

    // Vyhľadanie klienta podľa ID s podporou hybridného režimu (DB / XML)
    public Klient najdiKlientaPodlaId(int id) {

        //Ak systém OFFLINE ide priamo cez XML
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }

        // online režim - databáza primárne
        try {
            return klientDao.najdiKlientaPodlaId(id);
        } catch (RuntimeException e) {

            appLog.error("DB chyba pri najdiKlientaPodlaId, fallback na XML | klientId=" + id, e);

            // DB nedostupná, prepni do offline režimu a skús načítať z XML
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }
    }

    // Registrácia nového klienta je povolená len v online režime
    public int registrujKlienta(Klient klient) {
        if (SystemRezim.isOffline()) {

            throw new IllegalStateException("Registrácia klienta je povolená len v online režime .");
        }
        int id = klientDao.ulozKlienta(klient);

        klient.setId(id);
        try{
        xmlZapisServis.ulozKlienta(klient);

        } catch (Exception e){
            appLog.error("Zlyhal zápis klienta do XML po uložení do DB | klientId=" + id, e);
            throw e;
        }
        return id;
    }

    // Aktualizácia klienta je povolená len v online režime
    public boolean aktualizujKlienta(Klient klient) {
        if (SystemRezim.isOffline()) {
            throw new IllegalStateException("Aktualizácia klienta nie je možná v offline režime.");
        }
        return klientDao.aktualizujKlienta(klient);
    }

    // Vymazanie klienta je povolené len v online režime
    public boolean vymazatKlienta(int id) {
        if (SystemRezim.isOffline()) {
            throw new IllegalStateException("Vymazanie klienta nie je možná v offline režime.");
        }
        return klientDao.vymazatKlienta(id);
    }

    // Nastavenie platnosti permanentky je povolené len v online režime
    public boolean nastavPermanentkuPlatnuDo(int klientId, LocalDate platnaDo) {

        if (SystemRezim.isOffline()) {
            throw new IllegalStateException("Nastavenie permanentky nie je možné v offline režime.");
        }
        Klient klient = klientDao.najdiKlientaPodlaId(klientId);

        // Skontroluj, či je klient registrovaný
        if (klient == null || klient.getDatumRegistracie() == null) {
            throw new IllegalStateException("Klient nie je registrovaný – nemožno priradiť permanentku.");
        }

        // Aktualizuj platnosť permanentky v databáze
        boolean databazaOnlineOk =  klientDao.aktualizujPermanentkuPlatnuDo(klientId, platnaDo);

        if (!databazaOnlineOk) {
            appLog.warn("Databáza neaktualizovala platnosť permanentky, zrušenie operácie | klientId=" + klientId);
            return false;
        }
        klient.setPermanentkaPlatnaDo(platnaDo);

        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (Exception e) {

            appLog.error("Chyba pri aktualizácii permanentky v XML | klientId=" + klientId, e);
            throw new IllegalStateException("Chyba pri aktualizácii permanentky v XML.", e);
        }

        return true;
    }
}


