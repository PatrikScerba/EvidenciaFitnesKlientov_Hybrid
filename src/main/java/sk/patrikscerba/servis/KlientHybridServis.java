package sk.patrikscerba.servis;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.model.Klient;
import java.sql.SQLException;
import java.util.List;


public class KlientHybridServis {

    private final KlientDao klientDao = new KlientDaoImpl();
    private  final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final XMLNacitanieServis xmlNacitanieServis = new XMLNacitanieServis();

    public List<Klient>ziskajVsetkychKlientov() throws SQLException {

        //Ofline rezim - nacitanie z XML suboru
        if (SystemRezim.isOffline()){
            return xmlNacitanieServis.nacitajKlientovZoXML();
        }
        //Online rezim - nacitanie z databazy
        return klientDao.ziskajVsetkychKlientov();
    }

    // Vyhľadanie klienta podľa ID s podporou hybridného režimu (DB / XML)
    public Klient najdiKlientaPodlaId(int id)throws SQLException {

        //Ak systém OFFLINE ide priamo cez XML
        if (SystemRezim.isOffline()){
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }

        // online režim - databáza primárne
        try{
            return klientDao.najdiKlientaPodlaId(id);
        }catch (SQLException e ){

            // DB nedostupná, prepni do offline režimu a skús načítať z XML
            SystemRezim.setOffline(true);
            return xmlNacitanieServis.najdiKlientaVXmlPodlaId(id);
        }
    }
}


