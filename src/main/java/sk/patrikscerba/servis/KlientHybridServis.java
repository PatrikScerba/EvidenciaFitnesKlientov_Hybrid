package sk.patrikscerba.servis;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.model.Klient;
import java.util.List;


public class KlientHybridServis {

    private final KlientDao klientDao = new KlientDaoImpl();
    private  final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();

    public List<Klient>ziskajVsetkychKlientov() {

        //Ofline rezim - nacitanie z XML suboru
        if (SystemRezim.isOffline()){
            return xmlNacitanieServis.nacitajKlientovZoXML();
        }
        //Online rezim - nacitanie z databazy
        return klientDao.ziskajVsetkychKlientov();
    }

    // Vyhľadanie klienta podľa ID s podporou hybridného režimu (DB / XML)
    public Klient najdiKlientaPodlaId(int id){

        //Ak systém OFFLINE ide priamo cez XML
        if (SystemRezim.isOffline()){
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }

        // online režim - databáza primárne
        try{
            return klientDao.najdiKlientaPodlaId(id);
        }catch (RuntimeException e){

            // DB nedostupná, prepni do offline režimu a skús načítať z XML
            SystemRezim.setOffline(true);
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }
    }

    // Registrácia nového klienta je povolená len v online režime
    public  int registrujKlienta(Klient klient ){
        if (SystemRezim.isOffline()){

            throw  new IllegalStateException("Registrácia klienta nie je možná v offline režime.");
        }
        return klientDao.ulozKlienta(klient);
    }

    // Aktualizácia klienta je povolená len v online režime
    public boolean aktualizujKlienta(Klient klient){
        if (SystemRezim.isOffline()){
            throw  new IllegalStateException("Aktualizácia klienta nie je možná v offline režime.");
        }
        return klientDao.aktualizujKlienta(klient);
    }

    // Vymazanie klienta je povolené len v online režime
    public  boolean vymazatKlienta(int id){
        if (SystemRezim.isOffline()){
            throw  new IllegalStateException("Vymazanie klienta nie je možná v offline režime.");
        }
        return klientDao.vymazatKlienta(id);
    }
}


