package sk.patrikscerba.dao;

import sk.patrikscerba.model.Klient;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface KlientDao {

    Long ulozKlienta(Klient klient);

    Optional<Klient> najdiKlientaPodlaId(Long id);

    List<Klient> ziskajVsetkychKlientov();

    boolean aktualizujKlienta(Klient klient);

    boolean vymazatKlienta(Long id);

    boolean aktualizujPermanentkuPlatnuDo(Long id, LocalDate platnaDo);

    boolean aktualizujQrCestu(Long id, String qrCesta);

    boolean aktualizujQrToken(Long id, String qrToken);

}
