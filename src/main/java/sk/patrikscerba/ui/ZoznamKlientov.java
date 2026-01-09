package sk.patrikscerba.ui;

import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.ZoznamKlientovServis;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;


//UI Okno pre zobrazenie zoznamu klientov v tabuľke
public class ZoznamKlientov extends JFrame {

    private final ZoznamKlientovServis zoznamKlientovServis = new ZoznamKlientovServis();
    private final PermanentkaVstupServis permanentkaVstupServis = new PermanentkaVstupServis();

    private static final DateTimeFormatter FORMAT_DATUMU = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private JTable tabulka;

    // Konstruktor triedy ZoznamKlientov nastaví okno, načíta klientov a naplní tabuľku
    public ZoznamKlientov() {
        setTitle("Zoznam klientov");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabulka = new JTable();
        add(new JScrollPane(tabulka));

        //Model tabuľky (stĺpce + riadky)
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Poradie");
        model.addColumn("ID");
        model.addColumn("Krstné meno");
        model.addColumn("Priezvisko");
        model.addColumn("Email");
        model.addColumn("Telefón");
        model.addColumn("Adresa");
        model.addColumn("Dátum narodenia");
        model.addColumn("Dátum registrácie");
        model.addColumn("Vek");
        model.addColumn("Permanentka");
        model.addColumn("Platnosť");

        try {
            List<Klient> klienti = zoznamKlientovServis.nacitajKlientov();

            int poradie = 1;

            for (Klient k: klienti){

                int vek = 0;
                if (k.getDatumNarodenia() != null){
                    vek = Period.between(k.getDatumNarodenia(), LocalDate.now()).getYears();
                }

                String stavPermanentky;
                String platnostPermanentky;

                LocalDate platnaDo = k.getPermanentkaPlatnaDo();

                if (platnaDo == null) {
                    stavPermanentky = "Nemá";
                    platnostPermanentky = "—";
                } else {
                    long dni =  permanentkaVstupServis.zostavaDni(platnaDo);

                    if (permanentkaVstupServis.jePlatnaPermanentka(platnaDo)) {
                        stavPermanentky = "Platná";
                        platnostPermanentky = dni + " dní";
                    } else {
                        stavPermanentky = "Neplatná";
                        platnostPermanentky = Math.abs(dni) + " dní po";
                    }
                }

                String datumNarodeniaText = k.getDatumNarodenia() != null ? k.getDatumNarodenia().format(FORMAT_DATUMU) : "";
                String datumRegistracieText = k.getDatumRegistracie() != null ? k.getDatumRegistracie().format(FORMAT_DATUMU) : "";

                // Pridanie riadku do tabuľky s údajmi o klientovi
                model.addRow(new Object[]{
                        poradie++,
                        k.getId(),
                        k.getKrstneMeno(),
                        k.getPriezvisko(),
                        k.getEmail(),
                        k.getTelefonneCislo(),
                        k.getAdresa(),
                        datumNarodeniaText,
                        datumRegistracieText,
                        vek,
                        stavPermanentky,
                        platnostPermanentky
                });
            }

            tabulka.setModel(model);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Chyba pri načítaní klientov: " + e.getMessage(),
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
