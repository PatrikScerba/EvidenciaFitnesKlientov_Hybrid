package sk.patrikscerba.servis;

import sk.patrikscerba.model.Klient;
import java.util.Optional;

//Servisná trieda určená výhradne na načítanie detailu klienta
public class DetailKlientaServis {

    private final KlientHybridServis klientHybridServis;

    public DetailKlientaServis(KlientHybridServis klientHybridServis) {
        this.klientHybridServis = klientHybridServis;
    }

    //Načítanie detail klienta podľa ID
    public Optional<Klient> nacitajDetailKlienta(long klientId) {
        return klientHybridServis.najdiKlientaPodlaId(klientId);
    }
}