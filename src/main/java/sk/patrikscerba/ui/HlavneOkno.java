package sk.patrikscerba.ui;

import javax.swing.*;

public class HlavneOkno extends JFrame {

    private JPanel mainPanel;
    private JButton Registracia;
    private JButton Vyhladanie;
    private JButton Klienti;
    private JButton HistoriaVstupov;
    private JLabel verzia3Label;
    private JLabel developedByPatrikŠčerbaLabel;

    //Nastavenie hlavného okna aplikácie.
    public HlavneOkno(){

        setContentPane(mainPanel);
        setTitle("Evidencia klientov");
        setSize(650, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        if (sk.patrikscerba.system.SystemRezim.isOffline()) {
            Registracia.setEnabled(false);
            Klienti.setEnabled(true); // čítanie povolené
            Vyhladanie.setEnabled(true); // čítanie povolené
        }

        //Nastavenie akcií tlačidiel v hlavnom okne
        Registracia.addActionListener(e -> new Registracia().setVisible(true));
        Vyhladanie.addActionListener(e -> new Vyhladavanie().setVisible(true));
        Klienti.addActionListener(e ->  new ZoznamKlientov().setVisible(true));
        HistoriaVstupov.addActionListener(e -> new HistoriaVstupov().setVisible(true));
    }
}
