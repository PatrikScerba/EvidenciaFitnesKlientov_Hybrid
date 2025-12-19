package sk.patrikscerba.dao;

import sk.patrikscerba.model.Klient;

import java.sql.SQLException;
import java.util.List;

public interface KlientDao {

    int ulozKlienta(Klient klient);


    Klient najdiKlientaPodlaId(int id) throws SQLException;

    List<Klient> ziskajVsetkychKlientov();

    boolean aktualizujKlienta(Klient klient);

    boolean vymazatKlienta(int id);


}
