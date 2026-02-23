package sk.patrikscerba.ui;

import sk.patrikscerba.io.log.LogNacitanieServis;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Collections;
import java.util.List;

// Okno zobrazujúce záznamy systémových logov aplikácie v tabuľke
public class SystemLogOkno extends JFrame {

    private JTable tabulka = new JTable();

    private final LogNacitanieServis logNacitanieServis = new LogNacitanieServis();

    // Konstruktor nastaví okno a načíta záznamy systémového logu do tabuľky
    public SystemLogOkno() {
        setTitle("Záznamy systémových logov");
        setSize(800, 450);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        add(new JScrollPane(tabulka));

        List<String> riadky = new java.util.ArrayList<>(
                logNacitanieServis.nacitajRiadky()
        );

        Collections.reverse(riadky);

        // Model tabuľky (stĺpce + riadky) - záznamy systémových logov len na čítanie
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Záznamy"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (String riadok : riadky) {
            model.addRow(new Object[]{riadok});
        }
        tabulka.setModel(model);

        tabulka.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabulka.getColumnModel().getColumn(0).setPreferredWidth(800);
        tabulka.setRowHeight(24);
    }
}
