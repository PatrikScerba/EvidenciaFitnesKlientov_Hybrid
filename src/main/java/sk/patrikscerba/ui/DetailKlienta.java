package sk.patrikscerba.ui;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.DetailKlientaServis;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;

import javax.swing.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


// Trieda slúži len  na zobrazenie detailov klienta
public class DetailKlienta extends JFrame {

    private final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private JPanel mainPanel;

    private JLabel labKrstneMeno;
    private JLabel labPriezvisko;
    private JLabel labVek;
    private JLabel labEmail;
    private JLabel labAdresa;
    private JLabel labTelefonneCislo;
    private JLabel labDatumNarodenia;
    private JLabel labDatumRegistracie;

    private JLabel labPermanentkaStav;
    private JLabel labPlatnostPermanentky;

    private JButton zatvoritButton;
    private JButton PredlzitPermanentkuButton;
    private  JButton historiaKlientaButton;

    private final Long klientId;
    private final DetailKlientaServis detailKlientaServis;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Nastavenie okna + načítanie a zobrazenie detailu klienta
    public DetailKlienta(Long klientId, DetailKlientaServis detailKlientaServis) {
        this.detailKlientaServis = detailKlientaServis;
        this.klientId = klientId;

        setContentPane(mainPanel);
        setTitle("Detail klienta");
        setSize(500, 550);
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
        historiaKlientaButton.addActionListener(e -> {
            new HistoriaKlienta(this.klientId).setVisible(true);
        });


        // Akcia: predĺženie permanentky (výber dní -> update DB/XML -> obnovenie UI)
        PredlzitPermanentkuButton.addActionListener(e -> {
                    JOptionPane.showConfirmDialog(this,
                    "Naozaj chcete predĺžiť permanentku?",
                    "Predĺženie permanentky",
                    JOptionPane.YES_NO_OPTION);

            Object[] moznosti = {"30 dní", "90 dní", "180 dní"};
            Object vyber = JOptionPane.showInputDialog(
                    this,
                    "Vyber dĺžku predĺženia:",
                    "Predĺženie permanentky",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    moznosti,
                    moznosti[0]
            );

            if (vyber == null) {
                return; // používateľ zavrel okno
            }

            int dni;
            if (vyber.equals("30 dní")) dni = 30;
            else if (vyber.equals("90 dní")) dni = 90;
            else dni = 180;

            try {
                PermanentkaVstupServis permanentkaServis = new PermanentkaVstupServis();
                KlientDao klientDao = new KlientDaoImpl();

                LocalDate novaPlatnost = permanentkaServis.predlzODni(klient.getPermanentkaPlatnaDo(), dni);
                boolean ok = klientDao.aktualizujPermanentkuPlatnuDo(klient.getId(), novaPlatnost);

                if (ok) {
                    klient.setPermanentkaPlatnaDo(novaPlatnost);
                    xmlZapisServis.aktualizujKlientaVXml(klient);

                    obnovZobrazeniePermanentky();

                    JOptionPane.showMessageDialog(
                            this, "Permanentka predĺžená do: " + novaPlatnost);
                } else {
                    JOptionPane.showMessageDialog(
                            this, "Permanentku sa nepodarilo predĺžiť.");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this, "Chyba: " + ex.getMessage());
            }
        });
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
        if (klient.getPermanentkaPlatnaDo() != null) {
            labPermanentkaStav.setText("Permanentka: Aktívna");
            labPlatnostPermanentky.setText(
                    "Permanentka platná do: " + klient.getPermanentkaPlatnaDo().format(FORMATTER));
        } else {
            labPermanentkaStav.setText("Permanentka: Neaktívna");
            labPlatnostPermanentky.setText("Platná do: -");
        }
    }

    // Obnovenie zobrazenia stavu a platnosti permanentky
    private void obnovZobrazeniePermanentky() {
        PermanentkaVstupServis permanentkaServis = new PermanentkaVstupServis();
        Klient klient = new Klient();
        LocalDate platnaDo = klient.getPermanentkaPlatnaDo();

        if (platnaDo == null) {
            labPermanentkaStav.setText("Nemá permanentku");
            labPlatnostPermanentky.setText("—");
            return;
        }
        labPlatnostPermanentky.setText(platnaDo.toString());

        if (permanentkaServis.jePlatnaPermanentka(platnaDo)) {
            labPermanentkaStav.setText("Platná");
        } else {
            labPermanentkaStav.setText("Neplatná");
        }
    }
}


