package sk.patrikscerba.servis;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.system.SystemRezim;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


public class KlientHybridServis {

    private final KlientDao klientDao = new KlientDaoImpl();
    private final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();
    private final AppLogServis appLog = new AppLogServis();

    // Získanie všetkých klientov s podporou hybridného režimu (DB / XML)
    public List<Klient> ziskajVsetkychKlientov() {

        //Ofline rezim - nacitanie z XML súboru
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.nacitajKlientovZoXML();
        }
        //Online režim-načítanie z databázy
        return klientDao.ziskajVsetkychKlientov();
    }

    // Vyhľadanie klienta podľa ID s podporou hybridného režimu (DB / XML)
    public Optional<Klient> najdiKlientaPodlaId(Long id) {

        //Ak systém je OFFLINE načíta cez XML
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }

        // online režim - databáza primárne
        try {
            return klientDao.najdiKlientaPodlaId(id);
        } catch (RuntimeException e) {

            appLog.error("DB chyba pri najdiKlientaPodlaId, fallback na XML | klientId=" + id, e);

            // Databáza zlyhala, načítanie z XML
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }
    }

    // Registrácia nového klienta je povolená len v online režime
    public Long registrujKlienta(Klient klient) {
        vyzadujOnline("Registrácia klienta");

        Long id = klientDao.ulozKlienta(klient);
        klient.setId(id);

        try {
            xmlZapisServis.ulozKlienta(klient);
        } catch (Exception e) {
            appLog.error("Zlyhal zápis klienta do XML po uložení do DB | klientId=" + id, e);
            throw new RuntimeException("Klient sa uložil do DB, ale nepodarilo sa uložiť XML.", e);
        }

        return id;
    }

    // Aktualizácia klienta je povolená len v online režime
    public boolean aktualizujKlienta(Klient klient) {
        vyzadujOnline("Aktualizácia klienta");

        boolean ok = klientDao.aktualizujKlienta(klient);

        if (!ok) {
            appLog.warn("DB neaktualizovala klienta (0 riadkov) | klientId=" + klient.getId());
            return false;
        }

        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (Exception e) {
            appLog.error("DB aktualizovaná, ale XML zlyhalo | klientId=" + klient.getId(), e);
            throw new RuntimeException("DB sa aktualizovala, ale XML sa nepodarilo zosúladiť.", e);
        }

        return true;
    }

    // Vymazanie klienta je povolené len v online režime(súbežne z DB aj z XML)
    public boolean vymazatKlienta(Long id) {
        if (SystemRezim.isOffline()) {
            throw new IllegalStateException("Vymazanie klienta nie je možná v offline režime.");
        }
        boolean vymazanyZDb = klientDao.vymazatKlienta(id);

        if (!vymazanyZDb) {
            throw new IllegalStateException("Klient sa nenašiel v databáze.");
        }
        boolean vymazanyZXml;

        try {
            vymazanyZXml = xmlZapisServis.vymazatKlientaPodlaId(id);
        } catch (Exception e) {
            throw new IllegalStateException("Chyba pri mazaní klienta z XML.", e);
        }

        if (!vymazanyZXml) {
            throw new IllegalStateException("Klient sa nenašiel v XML.");
        }

        return true;
    }

    // Nastavenie platnosti permanentky je povolené len v online režime
    public boolean nastavPermanentkuPlatnuDo(Long klientId, LocalDate platnaDo) {

        if (SystemRezim.isOffline()) {
            throw new IllegalStateException("Nastavenie permanentky nie je možné v offline režime.");
        }
        var klientOptional = klientDao.najdiKlientaPodlaId(klientId);

        // Skontroluj, či je klient registrovaný
        if (klientOptional.isEmpty() || klientOptional.get().getDatumRegistracie() == null) {
            throw new IllegalStateException("Klient nie je registrovaný – nemožno priradiť permanentku.");
        }
        Klient klient = klientOptional.get();

        // Aktualizuj platnosť permanentky v databáze
        boolean databazaOnlineOk = klientDao.aktualizujPermanentkuPlatnuDo(klientId, platnaDo);

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

    // Aktualizácia QR cesty je povolená len v online režime
    public boolean aktualizujQrCestu(Long klientId, String qrCesta) throws SQLException {
        vyzadujOnline("Aktualizácia QR cesty");

        boolean ok = klientDao.aktualizujQrCestu(klientId, qrCesta);

        if (!ok) {
            appLog.warn("DB neaktualizovala údaje qr_cesta (0 riadkov) | klientId=" + klientId);
            return false;
        }

        Klient klient = najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        klient.setQrCesta(qrCesta);

        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (Exception e) {
            appLog.error("DB aktualizovaná, ale XML zlyhalo pri qr_cesta | klientId=" + klientId, e);
            throw new RuntimeException("DB sa aktualizovala, ale XML sa nepodarilo zosúladiť.", e);
        }

        return true;
    }

    // Aktualizácia QR tokenu je povolená len v online režime
    public boolean aktualizujQrToken(Long klientId, String qrToken) throws SQLException {
        vyzadujOnline("Aktualizácia QR tokenu");

        boolean ok = klientDao.aktualizujQrToken(klientId, qrToken);

        if (!ok) {
            appLog.warn("DB neaktualizovala qr_token (0 riadkov) | klientId=" + klientId);
            return false;
        }

        // zosúladenie XML
        Klient klient = najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        klient.setQrToken(qrToken);

        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (Exception e) {
            appLog.error("DB aktualizovaná, ale XML zlyhalo pri qr_token | klientId=" + klientId, e);
            throw new RuntimeException("DB sa aktualizovala, ale XML sa nepodarilo zosúladiť.", e);
        }

        return true;
    }

    // Zabezpečí, že klient má QR token, ak nie, vygeneruje nový
    public String zabezpecQrToken(Klient klient) {
        if (klient.getQrToken() == null || klient.getQrToken().isBlank()) {
            klient.setQrToken(java.util.UUID.randomUUID().toString().replace("-", ""));
        }
        return klient.getQrToken();
    }

    // Vygeneruje nový QR token pre klienta
    public String vygenerujNovyQrToken(Klient klient) {
        vyzadujOnline("Generovanie nového QR tokenu nie je možné v offline režime.");

        klient.setQrToken(java.util.UUID.randomUUID().toString().replace("-", ""));

        return klient.getQrToken();
    }

    // Zabezpečuje, že zápisové operácie sú povolené len v online režime
    private void vyzadujOnline(String akcia) {
        if (SystemRezim.isOffline()) {
            throw new IllegalStateException(akcia + " nie je dostupné v offline režime.");
        }
    }
}


