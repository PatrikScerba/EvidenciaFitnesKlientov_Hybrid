package sk.patrikscerba.ui;

import javax.swing.*;
import java.awt.*;

public class HlavneOkno extends JFrame {

    private JPanel mainPanel;
    private JButton registracia;
    private JButton vyhladanie;
    private JButton klienti;
    private JButton historiaVstupov;
    private JButton scanner;
    private JButton systemLog;
    private JLabel developedByPatrikŠčerbaLabel;

    private JLabel hlavnyNadpisLabel;
    private JLabel podnadpisLabel;

    private JPanel navigaciaPanel1;
    private JPanel navigaciaPanel2;
    private JPanel nazovPanel;
    private JPanel logoPanel;
    private JLabel logoLabel;
    private JPanel Stlpec;
    private JPanel panelPozadie;


    //Nastavenie hlavného okna aplikácie.
    public HlavneOkno() {

        setContentPane(mainPanel);
        setTitle("Evidencia klientov");
        setSize(670, 350);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        logoLabel.setIcon(vytvorLogoIkonu());

        logoPanel.revalidate();
        logoPanel.repaint();

        nastavRezim();
        nastavAkcieTlacidiel();

        setVisible(true);
    }

    // Nastavenie režimu systému (online/offline) a povolení pre tlačidlá v hlavnom okne
    private void nastavRezim() {
        if (sk.patrikscerba.system.SystemRezim.isOffline()) {
            registracia.setEnabled(false);
            klienti.setEnabled(true);
            vyhladanie.setEnabled(true);
            historiaVstupov.setEnabled(true);
            scanner.setEnabled(true);
            systemLog.setEnabled(true);
        }
    }

    //Nastavenie akcií tlačidiel v hlavnom okne
    private void nastavAkcieTlacidiel() {
        registracia.addActionListener(e -> new Registracia().setVisible(true));
        vyhladanie.addActionListener(e -> new Vyhladavanie(false).setVisible(true));
        klienti.addActionListener(e -> new ZoznamKlientov().setVisible(true));
        historiaVstupov.addActionListener(e -> new HistoriaVstupov().setVisible(true));
        scanner.addActionListener(e -> new ScannerQrOkno().setVisible(true));
        systemLog.addActionListener(e -> new SystemLogOkno().setVisible(true));
    }

    private ImageIcon vytvorLogoIkonu() {
        java.net.URL url = getClass().getResource("/obrazok/Logo.png");
        if (url == null) return null;

        ImageIcon icon = new ImageIcon(url);
        Image scaled = icon.getImage().getScaledInstance(280, 200, Image.SCALE_SMOOTH);

        return new ImageIcon(scaled);
    }
}
