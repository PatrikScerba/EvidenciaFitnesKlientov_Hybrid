package sk.patrikscerba.servis;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
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
        xmlZapisServis.ulozKlienta(klient);
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

        return klientDao.aktualizujPermanentkuPlatnuDo(klientId, platnaDo);
    }
}


