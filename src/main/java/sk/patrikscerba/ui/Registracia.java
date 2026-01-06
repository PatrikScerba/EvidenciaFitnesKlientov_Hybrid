package sk.patrikscerba.ui;

import sk.patrikscerba.servis.RegistraciaKlientaServis;
import javax.swing.*;

// Okno pre registráciu nového klienta
public class Registracia extends JFrame {

    private JButton buttonRegistrovat;
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

    // Konštruktor okna pre registráciu klienta
    public Registracia() {
        setContentPane(mainPanel);
        setTitle("Registrácia klienta");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        // Pridanie akcie na tlačidlo registrácie
        buttonRegistrovat.addActionListener(e -> registrujKlienta());
    }
    
    // Registrácia klienta pomocou servisnej vrstvy
    private void registrujKlienta() {
        RegistraciaKlientaServis registraciaKlientaServis = new RegistraciaKlientaServis();

        try {
            registraciaKlientaServis.zaregistrujKlienta(
                    jTextKrstneMeno.getText(),
                    jTextPriezvisko.getText(),
                    jTextDatumNarodenia.getText(),
                    jTextTelefonneCislo.getText(),
                    jTextAdresa.getText(),
                    jTextEmail.getText()
            );
            JOptionPane.showMessageDialog(this, "Klient zaregistrovaný.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }
}




