package sk.patrikscerba.vstup.model;

// Trieda nesie výsledok kontroly vstupu (povolený / zamietnutý + správa).
public class VstupVysledok {

    private final boolean povoleny;
    private final String sprava;

    // Konštruktor nastaví výsledok vstupu a sprievodnú správu
    public VstupVysledok(boolean povoleny, String sprava) {
        this.povoleny = povoleny;
        this.sprava = sprava;
    }

    public boolean jePovoleny() {
        return povoleny;
    }

    public String getSprava() {
        return sprava;
    }
}