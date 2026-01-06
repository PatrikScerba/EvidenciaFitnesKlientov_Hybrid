package sk.patrikscerba.ui;

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
        private void registrujKlienta() {
            String krstneMeno = jTextKrstneMeno.getText();
            String priezvisko = jTextPriezvisko.getText();
            String datumNarodenia = jTextDatumNarodenia.getText();
            String telefonneCislo = jTextTelefonneCislo.getText();
            String adresa = jTextAdresa.getText();
            String email = jTextEmail.getText();

            //------------Otestovanie výpisu údajov klienta do konzoly-------------
            System.out.println("Registrujem klienta: \n" + krstneMeno + " " + priezvisko +
                    ", \nDátum narodenia: " + datumNarodenia +
                    ", \nTelefón: " + telefonneCislo +
                    ", \nAdresa: " + adresa +
                    ", \nEmail: " + email +
                    "\n=============================" +
                    "\nKlient zaregistrovaný úspešne.");
    }
}




