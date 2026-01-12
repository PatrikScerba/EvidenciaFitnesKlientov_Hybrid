package sk.patrikscerba.dao;

import sk.patrikscerba.model.Klient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KlientDao {

    int ulozKlienta(Klient klient);

    Optional<Klient> najdiKlientaPodlaId(long id);

    List<Klient> ziskajVsetkychKlientov();

    boolean aktualizujKlienta(Klient klient);

    boolean vymazatKlienta(int id);

    boolean aktualizujPermanentkuPlatnuDo(long id, LocalDate platnaDo);
}
