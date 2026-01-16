package sk.patrikscerba.ui;

import sk.patrikscerba.io.vstup.HistoriaVstupovServis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;


public class HistoriaKlienta extends JFrame {

    HistoriaVstupovServis historiaVstupovServis = new HistoriaVstupovServis();

    private JTable tabulka;

    // Konstruktor triedy ZoznamKlientov nastaví okno, načíta klientov a naplní tabuľku
    public HistoriaKlienta(long klientId) {
        setTitle("História klienta");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabulka = new JTable();
        add(new JScrollPane(tabulka));

        // Načítanie a zobrazenie záznamu vstupov klienta v tabuľke
        List<String> strings = historiaVstupovServis.nacitajRiadkyPreKlienta(klientId);

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Záznam vstupu");

        for (String riadok : strings) {
            model.addRow(new Object[]{
                    riadok
            });
        }
        tabulka.setModel(model);

    }
}



