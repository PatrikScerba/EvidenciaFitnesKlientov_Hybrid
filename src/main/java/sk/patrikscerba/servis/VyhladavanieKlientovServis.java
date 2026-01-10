package sk.patrikscerba.servis;

import sk.patrikscerba.model.Klient;
import java.util.ArrayList;
import java.util.List;

// Servis na vyhľadávanie klientov podľa mena a priezviska
public class VyhladavanieKlientovServis {

    private final KlientHybridServis klientHybridServis = new KlientHybridServis();
    private  final ValidaciaKlientaServis validaciaKlientaServis = new ValidaciaKlientaServis();

    // Vyhľadávanie klientov podľa mena alebo priezviska
    public  List<Klient>vyhladaj(String meno, String priezvisko) {

        String hladaneMeno = ValidaciaKlientaServis.normalizujText(meno);
        String hladanePriezvisko = ValidaciaKlientaServis.normalizujText(priezvisko);

        List<Klient> klienti = klientHybridServis.ziskajVsetkychKlientov();
        List<Klient> zhodniKlienti = new ArrayList<>();

        // Prechádzanie všetkých klientov a hľadanie zhody
        for (Klient k : klienti) {
            String kmeno = ValidaciaKlientaServis.normalizujText(k.getKrstneMeno());
            String kpriezvisko = ValidaciaKlientaServis.normalizujText(k.getPriezvisko());

            boolean zhoda = false;

            if (!hladaneMeno.isEmpty() && hladanePriezvisko.isEmpty()) {
                zhoda = kmeno.equals(hladaneMeno) || kmeno.startsWith(hladaneMeno);

            } else if (hladaneMeno.isEmpty() && !hladanePriezvisko.isEmpty()) {
                zhoda = kpriezvisko.equals(hladanePriezvisko) || kpriezvisko.contains(hladanePriezvisko);

            } else {

                boolean menoZhoda = kmeno.equals(hladaneMeno) || kmeno.contains(hladaneMeno);
                boolean priezviskoZhoda = kpriezvisko.equals(hladanePriezvisko) || kpriezvisko.contains(hladanePriezvisko);
                zhoda = menoZhoda && priezviskoZhoda;
            }

            if (zhoda){
                zhodniKlienti.add(k);
        }
    }
        return zhodniKlienti;
    }
}

