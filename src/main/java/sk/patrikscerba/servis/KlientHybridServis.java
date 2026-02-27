package sk.patrikscerba.servis;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.xml.XMLNacitanieServis;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.system.SystemRezim;
import java.util.List;
import java.util.Optional;

// Servis pre správu klientov s podporou hybridného režimu (online databáza / offline XML)
public class KlientHybridServis {

    private final KlientDao klientDao = new KlientDaoImpl();
    private final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();
    private final AppLogServis appLog = new AppLogServis();

    // Získanie všetkých klientov s podporou hybridného režimu (DB / XML)
    public List<Klient> ziskajVsetkychKlientov() {

        // Offline režim-načítanie z XML súboru
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.nacitajKlientovZoXML();
        }
        // Online režim-načítanie z databázy
        return klientDao.ziskajVsetkychKlientov();
    }

    // Vyhľadanie klienta podľa ID s podporou hybridného režimu (DB / XML)
    public Optional<Klient> najdiKlientaPodlaId(Long id) {

        // Ak systém je OFFLINE načíta cez XML
        if (SystemRezim.isOffline()) {
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }

        // online režim - databáza primárne
        try {
            return klientDao.najdiKlientaPodlaId(id);
        } catch (RuntimeException e) {

            appLog.error("DB chyba pri najdi klienta podla ID, fallback na XML | klientId=" + id, e);

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
        } catch (IllegalStateException e) {
            appLog.error("Zlyhal zapis klienta do XML po ulozeni do DB | klientId=" + id, e);
            throw new IllegalStateException("Klient sa uložil do DB, ale nepodarilo sa uložiť XML.", e);
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
        } catch (IllegalStateException e) {
            appLog.error("DB aktualizovana, ale XML zlyhalo | klientId=" + klient.getId(), e);
            throw new IllegalStateException("DB sa aktualizovala, ale XML sa nepodarilo zosúladiť.", e);
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
        } catch (IllegalStateException e) {

            appLog.error("Zlyhalo mazanie klienta z XML po DB mazani | klientId=" + id, e);
            throw new IllegalStateException("Chyba pri mazaní klienta z XML.", e);
        }

        if (!vymazanyZXml) {

            appLog.error("Nekonzistencia: klient zmazany z DB, ale nenasiel sa v XML | klientId=" + id);
            throw new IllegalStateException("Klient sa nenašiel v XML.");
        }

        return true;
    }

    // Aktualizácia QR cesty je povolená len v online režime
    public boolean aktualizujQrCestu(Long klientId, String qrCesta){
        vyzadujOnline("Aktualizácia QR cesty");

        boolean ok = klientDao.aktualizujQrCestu(klientId, qrCesta);

        if (!ok) {
            appLog.warn("DB neaktualizovala udaje qr_cesta (0 riadkov) | klientId=" + klientId);
            return false;
        }

        Klient klient = najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        klient.setQrCesta(qrCesta);

        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (IllegalStateException e) {
            appLog.error("DB aktualizovana, ale XML zlyhalo pri qr_cesta | klientId=" + klientId, e);
            throw new IllegalStateException("DB sa aktualizovala, ale XML sa nepodarilo zosúladiť.", e);
        }

        return true;
    }

    // Aktualizácia QR tokenu je povolená len v online režime
    public boolean aktualizujQrToken(Long klientId, String qrToken){
        vyzadujOnline("Aktualizácia QR tokenu");

        boolean ok = klientDao.aktualizujQrToken(klientId, qrToken);

        if (!ok) {
            appLog.warn("DB neaktualizovala qr_token (0 riadkov) | klientId=" + klientId);
            return false;
        }

        // Zosúladenie XML
        Klient klient = najdiKlientaPodlaId(klientId)
                .orElseThrow(() -> new IllegalStateException("Klient sa nenašiel | klientId=" + klientId));

        klient.setQrToken(qrToken);

        try {
            xmlZapisServis.aktualizujKlientaVXml(klient);
        } catch (IllegalStateException e) {
            appLog.error("DB aktualizovana, ale XML zlyhalo pri qr_token | klientId=" + klientId, e);
            throw new IllegalStateException("DB sa aktualizovala, ale XML sa nepodarilo zosúladiť.", e);
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


