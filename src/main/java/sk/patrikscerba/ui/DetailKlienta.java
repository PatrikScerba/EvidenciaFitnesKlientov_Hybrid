package sk.patrikscerba.ui;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.DetailKlientaServis;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;

import javax.swing.*;
import java.awt.*;
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
    private JButton historiaKlientaButton;
    private JButton upravitButton;

    private JTextField upravitKrstneMeno;
    private JTextField upravitPriezvisko;
    private JTextField upravitEmail;
    private JTextField upravitAdresa;
    private JTextField upravitTelefonneCislo;
    private JTextField upravitDatumNarodenia;

    private Klient klient;
    private boolean rezim = false;
    private final Long klientId;
    private final DetailKlientaServis detailKlientaServis;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // Nastavenie okna + načítanie a zobrazenie detailu klienta
    public DetailKlienta(Long klientId, DetailKlientaServis detailKlientaServis) {
        this.detailKlientaServis = detailKlientaServis;
        this.klientId = klientId;

        // Nastavenie okna
        nastavOkno();
        nacitajKlientaAleboZavri();
        nastavAkcieTlacidiel();

        //štandardne v zobrazovacom režime
        nastavRezim(false);

    }

    // Nastavenie základných vlastností okna
    private void nastavOkno() {
        setContentPane(mainPanel);
        setTitle("Detail klienta");
        setSize(500, 550);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Načítanie klienta podľa ID, ak neexistuje, zavrie okno
    private void nacitajKlientaAleboZavri() {
        var klientOpt = detailKlientaServis.nacitajDetailKlienta(klientId);

        if (klientOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Klient nebol nájdený.", "Detail klienta", JOptionPane.WARNING_MESSAGE);
            dispose();
            return;
        }

        this.klient = klientOpt.get();
        zobrazUdaje(this.klient);
    }

    // Nastavenie akcií tlačidiel
    private void nastavAkcieTlacidiel() {

        zatvoritButton.addActionListener(e -> dispose());

        historiaKlientaButton.addActionListener(e ->
                new HistoriaKlienta(this.klientId).setVisible(true)
        );

        PredlzitPermanentkuButton.addActionListener(e -> predlzPermanentku());
        upravitButton.addActionListener(e -> prepniDoUprav());

    }

    //Režim zobrazenia alebo úprav
    private void nastavRezim(boolean uprav) {
        rezim = uprav;

        nastavViditelnostUpravPoli(uprav);
        zobrazLabely(!uprav);

        upravitButton.setText("Upraviť");
        mainPanel.setBackground(uprav ? new Color(47, 39, 39) : null);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Prepne mód do režimu úprav
    private void prepniDoUprav() {

        // Naplnenie polí
        upravitKrstneMeno.setText(klient.getKrstneMeno());
        upravitPriezvisko.setText(klient.getPriezvisko());
        upravitEmail.setText(klient.getEmail());
        upravitAdresa.setText(klient.getAdresa());
        upravitTelefonneCislo.setText(klient.getTelefonneCislo());

        upravitDatumNarodenia.setText(
                klient.getDatumNarodenia() != null
                        ? klient.getDatumNarodenia().format(FORMATTER)
                        : ""
        );

        nastavRezim(true);
    }

    // Zobrazí alebo skryje polia na úpravu podľa režimu
    private void nastavViditelnostUpravPoli(boolean viditelne) {
        upravitKrstneMeno.setVisible(viditelne);
        upravitPriezvisko.setVisible(viditelne);
        upravitEmail.setVisible(viditelne);
        upravitAdresa.setVisible(viditelne);
        upravitTelefonneCislo.setVisible(viditelne);
        upravitDatumNarodenia.setVisible(viditelne);
    }

    // Zobrazí alebo skryje labely podľa režimu
    private void zobrazLabely(boolean viditelne) {
        labKrstneMeno.setVisible(viditelne);
        labPriezvisko.setVisible(viditelne);
        labEmail.setVisible(viditelne);
        labAdresa.setVisible(viditelne);
        labTelefonneCislo.setVisible(viditelne);
        labDatumNarodenia.setVisible(viditelne);
        labDatumRegistracie.setVisible(viditelne);
        labVek.setVisible(viditelne);
        labPermanentkaStav.setVisible(viditelne);
        labPlatnostPermanentky.setVisible(viditelne);
    }

    //Predĺženie permanentky klienta s výberom počtu dní
    private void predlzPermanentku() {

        int potvrdenie = JOptionPane.showConfirmDialog(
                this,
                "Naozaj chcete predĺžiť permanentku?",
                "Predĺženie permanentky",
                JOptionPane.YES_NO_OPTION
        );

        if (potvrdenie != JOptionPane.YES_OPTION) {
            return;
        }

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
            return;
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

                obnovZobrazeniePermanentky(klient);

                JOptionPane.showMessageDialog(this, "Permanentka predĺžená do: " + novaPlatnost);
            } else {
                JOptionPane.showMessageDialog(this, "Permanentku sa nepodarilo predĺžiť.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Chyba: " + ex.getMessage(), "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Zobrazí údaje klienta v labeloch
    private void zobrazUdaje(Klient klient) {
        labKrstneMeno.setText("Meno: " + klient.getKrstneMeno());
        labPriezvisko.setText("Priezvisko: " + klient.getPriezvisko());
        labVek.setText("Vek: " + klient.getVek());
        labEmail.setText("Email: " + klient.getEmail());
        labAdresa.setText("Adresa: " + klient.getAdresa());
        labTelefonneCislo.setText("Telefónne číslo: " + klient.getTelefonneCislo());

        if (klient.getDatumNarodenia() != null) {
            labDatumNarodenia.setText("Dátum narodenia: " + klient.getDatumNarodenia().format(FORMATTER));
        } else {
            labDatumNarodenia.setText("Dátum narodenia: -");
        }

        if (klient.getDatumRegistracie() != null) {
            labDatumRegistracie.setText("Dátum registrácie: " + klient.getDatumRegistracie().format(FORMATTER));
        } else {
            labDatumRegistracie.setText("Dátum registrácie: -");
        }

        obnovZobrazeniePermanentky(klient);
    }

    // Obnoví zobrazenie stavu permanentky podľa aktuálnych údajov klienta
    private void obnovZobrazeniePermanentky(Klient klient) {
        PermanentkaVstupServis permanentkaServis = new PermanentkaVstupServis();

        LocalDate platnaDo = klient.getPermanentkaPlatnaDo();

        if (platnaDo == null) {
            labPermanentkaStav.setText("Nemá");
            labPlatnostPermanentky.setText("—");
            return;
        }

        long dni = permanentkaServis.zostavaDni(platnaDo);

        if (permanentkaServis.jePlatnaPermanentka(platnaDo)) {
            labPermanentkaStav.setText("Permanentka: Aktívna");
            labPlatnostPermanentky.setText("Platná do: " + platnaDo.format(FORMATTER) + " (" + dni + " dní)");
        } else {
            labPermanentkaStav.setText("Permanentka: Neplatná");
            labPlatnostPermanentky.setText("Vypršala: " + platnaDo.format(FORMATTER) + " (" + Math.abs(dni) + " dní po)");
        }
    }
}





