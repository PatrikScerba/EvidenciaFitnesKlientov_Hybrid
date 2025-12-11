package sk.patrikscerba.dao;

import sk.patrikscerba.model.Klient;
import java.util.List;

public interface KlientDao {

    int ulozKlienta(Klient klient);


    Klient najdiKlientaPodlaId(int id);

    List<Klient> ziskajVsetkychKlientov();

    boolean aktualizujKlienta(Klient klient);

    boolean vymazatKlienta(int id);


}
