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
}
