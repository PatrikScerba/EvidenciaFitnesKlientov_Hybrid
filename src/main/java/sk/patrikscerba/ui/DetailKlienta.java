package sk.patrikscerba.ui;

import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.DetailKlientaServis;
import javax.swing.*;
import java.time.format.DateTimeFormatter;


// Trieda slúži len  na zobrazenie detailov klienta
public class DetailKlienta extends JFrame {

    private  JPanel mainPanel;

    private JLabel labKrstneMeno;
    private JLabel labPriezvisko;
    private JLabel labVek;
    private JLabel labEmail;
    private JLabel labAdresa;
    private JLabel labTelefonneCislo;
    private JLabel labDatumNarodenia;
    private JLabel labDatumRegistracie;

    private  JLabel labPermantkaStav;
    private  JLabel labPlatnostPermanentky;

    private  JButton zatvoritButton;

    private final DetailKlientaServis detailKlientaServis;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Nastavenie okna pre detail klienta
    public DetailKlienta(Long klientId, DetailKlientaServis detailKlientaServis) {
        this.detailKlientaServis = detailKlientaServis;

        setContentPane(mainPanel);
        setTitle("Detail klienta");
        setSize( 500, 550);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Načítanie detailu klienta cez servisnú vrstvu
        var klientOpt = detailKlientaServis.nacitajDetailKlienta(klientId);

        if (klientOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Klient nebol nájdený.", "Detail klienta", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        //Získanie klienta z optional
        Klient klient = klientOpt.get();

        //Zobrazenie údajov klienta v UI
        zobrazUdaje(klient);

        zatvoritButton.addActionListener(e -> dispose());
    }

    // Zobrazenie údajov klienta v UI
    private void zobrazUdaje(Klient klient) {
        labKrstneMeno.setText("Meno: " + klient.getKrstneMeno());
        labPriezvisko.setText("Priezvisko: " + klient.getPriezvisko());
        labVek.setText("Vek: " + klient.getVek());
        labEmail.setText("Email: " + klient.getEmail());
        labAdresa.setText("Adresa: " + klient.getAdresa());
        labTelefonneCislo.setText("Telefónne číslo: " + klient.getTelefonneCislo());

        // Zobrazenie dátumu narodenia(môže byť null)
        if (klient.getDatumNarodenia() != null) {
            labDatumNarodenia.setText(
                    "Dátum narodenia: " + klient.getDatumNarodenia().format(FORMATTER));
        } else {
            labDatumNarodenia.setText("Dátum narodenia: -");
        }

        // Zobrazenie dátumu registrácie(môže byť null)
        if (klient.getDatumRegistracie() != null) {
            labDatumRegistracie.setText(
                    "Dátum registrácie: " + klient.getDatumRegistracie().format(FORMATTER));
        } else {
            labDatumRegistracie.setText("Dátum registrácie: -");
        }

        //Informácie o permanentke
        if (klient.getPermanentkaPlatnaDo() !=null){
            labPermantkaStav.setText("Permanentka: Aktívna");
            labPlatnostPermanentky.setText(
                    "Permanentka platná do: " + klient.getPermanentkaPlatnaDo().format(FORMATTER));
        }else {
            labPermantkaStav.setText("Permanentka: Neaktívna");
            labPlatnostPermanentky.setText("Platná do: -");
        }
    }
}
