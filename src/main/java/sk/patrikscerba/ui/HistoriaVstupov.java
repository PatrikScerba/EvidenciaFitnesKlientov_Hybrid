package sk.patrikscerba.ui;

import sk.patrikscerba.io.vstup.HistoriaVstupovServis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import java.util.List;

// UI okno zobrazujúce globálnu históriu vstupov klientov
public class HistoriaVstupov extends JFrame {

    private final HistoriaVstupovServis historiaVstupovServis = new HistoriaVstupovServis();
    private final JTable tabulka = new JTable();

    // Nastaví a zobrazí globálnu históriu vstupov klientov
    public HistoriaVstupov() {
        setTitle("Globálna história vstupov klientov");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Pridanie tabuľky do okna s posuvníkom
        add(new JScrollPane(tabulka));

        List<String> riadky = new java.util.ArrayList<>(
                historiaVstupovServis.nacitajRiadky());
        Collections.reverse(riadky);

        // Zabráni úprave buniek v tabuľke (tabuľka je iba na čítanie)
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Záznamy vstupov");

        for (String riadok : riadky) {
            model.addRow(new Object[]{riadok});
        }

        tabulka.setModel(model);

        tabulka.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabulka.getColumnModel().getColumn(0).setPreferredWidth(900);
        tabulka.setRowHeight(24);
    }
}
