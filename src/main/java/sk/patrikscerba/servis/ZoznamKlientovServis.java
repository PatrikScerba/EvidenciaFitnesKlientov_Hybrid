package sk.patrikscerba.servis;

import sk.patrikscerba.model.Klient;
import java.util.List;

// Servisná trieda na načítanie zoznamu klientov
public class ZoznamKlientovServis {

    KlientHybridServis klientHybridServis = new KlientHybridServis();

    // Načíta zoznam klientov cez hybridnú servisnú vrstvu (DB/XML)
    public List<Klient> nacitajKlientov() {
        return klientHybridServis.ziskajVsetkychKlientov();
    }
}