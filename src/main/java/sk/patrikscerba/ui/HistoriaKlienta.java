package sk.patrikscerba.ui;

import sk.patrikscerba.io.vstup.HistoriaVstupovServis;
import sk.patrikscerba.servis.DetailKlientaServis;
import sk.patrikscerba.servis.KlientHybridServis;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.List;

// UI okno zobrazujúce históriu vstupov konkrétneho klienta
public class HistoriaKlienta extends JFrame {

    private final HistoriaVstupovServis historiaVstupovServis = new HistoriaVstupovServis();

    private JTable tabulka;

    // Konštruktor nastaví okno a zobrazí históriu vstupov pre konkrétneho klienta
    public HistoriaKlienta(long klientId, boolean zobrazTlacidloDetail) {
        setTitle("História klienta");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        tabulka = new JTable();
        add(new JScrollPane(tabulka));

        if (zobrazTlacidloDetail) {
            JButton tlacidloDetail = new JButton("Zobraziť detail klienta");
            tlacidloDetail.addActionListener(e -> otvorDetailKlienta(klientId));

            JPanel spodnyPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            spodnyPanel.add(tlacidloDetail);
            add(spodnyPanel, BorderLayout.SOUTH);
        }

        // Načítanie a zobrazenie záznamu vstupov klienta v tabuľke
        List<String> strings = new java.util.ArrayList<>(
                historiaVstupovServis.nacitajRiadkyPreKlienta(klientId)
        );

        Collections.reverse(strings);

        // Zabráni úprave buniek v tabuľke (tabuľka je iba na čítanie)
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        model.addColumn("Záznam vstupu");

        for (String riadok : strings) {
            model.addRow(new Object[]{
                    riadok
            });
        }
        tabulka.setModel(model);

        tabulka.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabulka.getColumnModel().getColumn(0).setPreferredWidth(900);
        tabulka.setRowHeight(24);

    }

    // Otvorí okno detailu klienta (napr. kvôli predĺženiu permanentky)
    private void otvorDetailKlienta(long klientId) {
        new DetailKlienta(klientId,
                new DetailKlientaServis(
                        new KlientHybridServis())).setVisible(true);
        dispose();
    }
}
