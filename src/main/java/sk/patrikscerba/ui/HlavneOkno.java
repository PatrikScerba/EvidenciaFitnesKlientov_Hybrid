package sk.patrikscerba.ui;

import javax.swing.*;

public class HlavneOkno extends JFrame {

    private JPanel mainPanel;
    private JButton Registracia;
    private JButton Vyhladanie;
    private JButton Klienti;
    private JButton HistoriaVstupov;
    private JButton Scanner;
    private JButton SystemLog;
    private JLabel verzia3Label;
    private JLabel developedByPatrikŠčerbaLabel;

    //Nastavenie hlavného okna aplikácie.
    public HlavneOkno() {

        setContentPane(mainPanel);
        setTitle("Evidencia klientov");
        setSize(650, 250);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nastavRezim();
        nastavAkcieTlacidiel();

        setVisible(true);
    }

    // Nastavenie režimu systému (online/offline) a povolení pre tlačidlá v hlavnom okne
    private void nastavRezim() {
        if (sk.patrikscerba.system.SystemRezim.isOffline()) {
            Registracia.setEnabled(false);
            Klienti.setEnabled(true);
            Vyhladanie.setEnabled(true);
            HistoriaVstupov.setEnabled(true);
            Scanner.setEnabled(true);
            SystemLog.setEnabled(true);
        }
    }

    //Nastavenie akcií tlačidiel v hlavnom okne
    private void nastavAkcieTlacidiel() {
        Registracia.addActionListener(e -> new Registracia().setVisible(true));
        Vyhladanie.addActionListener(e -> new Vyhladavanie(false).setVisible(true));
        Klienti.addActionListener(e -> new ZoznamKlientov().setVisible(true));
        HistoriaVstupov.addActionListener(e -> new HistoriaVstupov().setVisible(true));
        Scanner.addActionListener(e -> new ScannerQrOkno().setVisible(true));
        SystemLog.addActionListener(e -> new SystemLogOkno().setVisible(true));
    }
}

