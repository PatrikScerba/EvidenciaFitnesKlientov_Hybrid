package sk.patrikscerba.ui;

import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.vstup.model.VstupVysledok;
import sk.patrikscerba.vstup.servis.VstupServis;

import javax.swing.*;
import java.awt.event.ActionEvent;


// Okno simulujúce vstup klienta cez QR skener.
// Desktop verzia bez fyzickej čítačky – zadáva sa iba klientId.
// QR token sa neskenuje, systém používa uložený token priradený ku klientovi.
public class ScannerQrOkno extends JFrame {

    private JPanel panel;
    private JTextField textKlientId;
    private JButton skenovatButton;

    private final VstupServis vstupServis = new VstupServis();
    private static final String NAZOV_OKNA = "Simulácia vstupu (QR)";
    private final AppLogServis appLog = new AppLogServis();

    // Nastavenie okna simulácie QR vstupu a prepojenie tlačidla na spracovanie skenu
    public ScannerQrOkno() {
        setTitle(NAZOV_OKNA);
        setContentPane(panel);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        skenovatButton.addActionListener(this::spracujSken);
    }

    // Spracuje naskenovanie QR kódu a vykoná kontrolu vstupu klienta
    private void spracujSken(ActionEvent e) {

        long klientId;

        try {
            String text = textKlientId.getText();
            if (text == null || text.isBlank()) {
                JOptionPane.showMessageDialog(
                        this, "Zadaj ID klienta.",
                        NAZOV_OKNA,
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            klientId = Long.parseLong(textKlientId.getText().trim());

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this, "Neplatné ID.",
                    NAZOV_OKNA,
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            VstupVysledok vysledok = vstupServis.simulujVstupCezScanner(klientId);

            boolean chyba = !vysledok.jePovoleny();

            JOptionPane.showMessageDialog(this,
                    vysledok.getSprava(),
                    NAZOV_OKNA,
                    chyba ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

            if (chyba) {
                new HistoriaKlienta(klientId, true).setVisible(true);
                dispose();
            }

        } catch (IllegalArgumentException | IllegalStateException ex) {
            appLog.error("Chyba systemu pri spracovani vstupu (scanner) | klientId=" + klientId, ex);

            JOptionPane.showMessageDialog(
                    this, "Chyba: " + ex.getMessage(),
                    NAZOV_OKNA,
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            appLog.error("Neocakavana chyba pri spracovani vstupu (scanner) | klientId=" + klientId, ex);

            JOptionPane.showMessageDialog(
                    this,
                    "Nastala neočakávaná chyba pri spracovaní vstupu.",
                    NAZOV_OKNA,
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
