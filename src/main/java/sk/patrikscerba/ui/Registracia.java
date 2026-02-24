package sk.patrikscerba.ui;

import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.servis.DetailKlientaServis;
import sk.patrikscerba.servis.KlientHybridServis;
import sk.patrikscerba.servis.RegistraciaKlientaServis;
import javax.swing.*;

// Okno pre registráciu nového klienta
public class Registracia extends JFrame {

    private final AppLogServis appLog = new AppLogServis();

    private JButton jButtonRegistrovat;
    private JTextField jTextKrstneMeno;
    private JTextField jTextPriezvisko;
    private JTextField jTextDatumNarodenia;
    private JTextField jTextTelefonneCislo;
    private JTextField jTextAdresa;
    private JTextField jTextEmail;

    private JLabel krstneMenoLabel;
    private JLabel priezviskoLabel;
    private JLabel datumNarodeniaLabel;
    private JLabel telefonneCisloLabel;
    private JLabel adresaLabel;
    private JLabel emailLabel;

    private JPanel mainPanel;
    private JPanel krstneMenoPanel;
    private JPanel priezviskoPanel;
    private JPanel datumPanel;
    private JPanel telefonPanel;
    private JPanel adresaPanel;
    private JPanel emailPanel;
    private JPanel tlacidloPanel;
    private JPanel nadpisPanel;
    private JLabel nadpisLabel;
    private JPanel cyanPanel;

    // Konštruktor okna pre registráciu klienta
    public Registracia() {
        setContentPane(mainPanel);
        setTitle("Registrácia klienta");
        setSize(480, 480);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Pridanie akcie na tlačidlo registrácie
        jButtonRegistrovat.addActionListener(e -> registrujKlienta());
    }

    // Registrácia klienta pomocou servisnej vrstvy
    private void registrujKlienta() {
        RegistraciaKlientaServis registraciaKlientaServis = new RegistraciaKlientaServis();

        try {
            Long klientId = registraciaKlientaServis.zaregistrujKlienta(
                    jTextKrstneMeno.getText(),
                    jTextPriezvisko.getText(),
                    jTextDatumNarodenia.getText(),
                    jTextTelefonneCislo.getText(),
                    jTextAdresa.getText(),
                    jTextEmail.getText()
            );

            JOptionPane.showMessageDialog(this, "Klient zaregistrovaný.");

            // Ponuka na zobrazenie detailu klienta
            int volba = JOptionPane.showConfirmDialog(
                    this,
                    "Klient bol zaregistrovaný.\nChcete zobraziť detail klienta?",
                    "Registrácia úspešná",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (volba == JOptionPane.YES_OPTION) {
                KlientHybridServis klientHybridServis = new KlientHybridServis();
                DetailKlientaServis detailServis = new DetailKlientaServis(klientHybridServis);

                new DetailKlienta(klientId, detailServis).setVisible(true);
                this.dispose();
            }

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(
                    this, ex.getMessage(),
                    "Chyba vstupu", JOptionPane.ERROR_MESSAGE);

        } catch (IllegalStateException ex) {
            appLog.error("Chyba systemu pri registracii klienta | meno="
                    + jTextKrstneMeno.getText() + " " + jTextPriezvisko.getText(), ex);

            JOptionPane.showMessageDialog(
                    this, "Chyba systému pri registrácii:\n" + ex.getMessage(),
                    "Chyba", JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            appLog.error("Neocakavana chyba pri registracii klienta", ex);

            JOptionPane.showMessageDialog(
                    this, "Nastala neočakávaná chyba pri registrácii.",
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }
}
