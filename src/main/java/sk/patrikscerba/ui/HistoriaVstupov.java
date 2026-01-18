package sk.patrikscerba.ui;

import sk.patrikscerba.io.vstup.HistoriaVstupovServis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

// Zobrazenie histórie vstupov klienta
public class HistoriaVstupov extends JFrame {

    private final HistoriaVstupovServis historiaVstupovServis = new HistoriaVstupovServis();
    private final JTable tabulka = new JTable();

    //História vstupov klientov
    public HistoriaVstupov() {
        setTitle("Globálna história vstupov klientov");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // Pridanie tabuľky do okna s posuvníkom
        add(new JScrollPane(tabulka));

        List<String> riadky = historiaVstupovServis.nacitajRiadky();

        //Model tabuľky (stĺpce + riadky)
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Záznamy vstupov");

        for (String riadok : riadky) {
            model.addRow(new Object[]{riadok});
        }

        tabulka.setModel(model);
    }
}
