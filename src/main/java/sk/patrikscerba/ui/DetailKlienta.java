package sk.patrikscerba.ui;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.DetailKlientaServis;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


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

    private JLabel qrObrazokLabel;

    private JButton zatvoritButton;
    private JButton predlzitPermanentkuButton;
    private JButton historiaKlientaButton;
    private JButton upravitButton;
    private JButton zrusitUpravyButton;
    private JButton vymazatKlientaButton;
    private JButton znovaGenerovatQrButton;

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
        nastavDostupnostAkciiPodlaRezimu();
        zobrazQrObrazok();
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

        historiaKlientaButton.addActionListener(e -> new HistoriaKlienta(this.klientId).setVisible(true));

        predlzitPermanentkuButton.addActionListener(e -> predlzPermanentku());

        upravitButton.addActionListener(e -> {
            if (!rezim) {
                prepniDoUprav();
            } else {
                ulozZmeny();
            }
        });

        zrusitUpravyButton.addActionListener(e -> {
            nastavRezim(false);
            zobrazQrObrazok();
        });

        vymazatKlientaButton.addActionListener(e -> vymazKlienta());

        znovaGenerovatQrButton.addActionListener(e -> znovaVygenerovatQrKod());
    }

    //Režim zobrazenia alebo úprav
    private void nastavRezim(boolean uprav) {
        rezim = uprav;

        nastavViditelnosPoliUprav(uprav);
        zobrazLabely(!uprav);

        predlzitPermanentkuButton.setVisible(!uprav);
        historiaKlientaButton.setVisible(!uprav);
        zrusitUpravyButton.setVisible(uprav);
        zatvoritButton.setVisible(!uprav);
        vymazatKlientaButton.setVisible(!uprav);
        upravitButton.setText(uprav ? "Uložiť zmeny" : "Upraviť");
        mainPanel.setBackground(uprav ? new Color(47, 39, 39) : null);
        qrObrazokLabel.setVisible(!uprav);
        znovaGenerovatQrButton.setVisible(uprav);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    // Prepne režim do režimu úprav
    private void prepniDoUprav() {

        // Naplnenie polí
        upravitKrstneMeno.setText(klient.getKrstneMeno());
        upravitPriezvisko.setText(klient.getPriezvisko());
        upravitEmail.setText(klient.getEmail());
        upravitAdresa.setText(klient.getAdresa());
        upravitTelefonneCislo.setText(klient.getTelefonneCislo());

        upravitDatumNarodenia.setText(klient.getDatumNarodenia() != null ? klient.getDatumNarodenia().format(FORMATTER) : "");

        nastavRezim(true);
    }

    // Zobrazí alebo skryje polia na úpravu podľa režimu
    private void nastavViditelnosPoliUprav(boolean viditelne) {
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

    //Uloženie zmien po úprave klienta
    private void ulozZmeny() {
        try {
            //servis uloží do DB/XML cez hybrid servis (validácie sú v servise)
            detailKlientaServis.ulozUpravyKlienta(klientId, upravitKrstneMeno.getText(), upravitPriezvisko.getText(), upravitDatumNarodenia.getText(), upravitTelefonneCislo.getText(), upravitAdresa.getText(), upravitEmail.getText());

            // UI: aktualizácia aktuálneho klienta
            this.klient.setKrstneMeno(upravitKrstneMeno.getText().trim());
            this.klient.setPriezvisko(upravitPriezvisko.getText().trim());
            this.klient.setEmail(upravitEmail.getText().trim());
            this.klient.setAdresa(upravitAdresa.getText().trim());
            this.klient.setTelefonneCislo(upravitTelefonneCislo.getText().trim());
            this.klient.setDatumNarodenia(LocalDate.parse(upravitDatumNarodenia.getText().trim(), FORMATTER));

            // UI zobrazí aktualizované údaje
            zobrazUdaje(this.klient);
            nastavRezim(false);
            zobrazQrObrazok();

            JOptionPane.showMessageDialog(this, "Údaje boli uložené.");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Chyba: " + ex.getMessage(), "Chyba", JOptionPane.ERROR_MESSAGE);
            // zostaneš v edit režime, aby si mohol opraviť hodnoty
            nastavRezim(true);
        }
    }

    //Predĺženie permanentky klienta s výberom počtu dní
    private void predlzPermanentku() {

        int potvrdenie = JOptionPane.showConfirmDialog(this, "Naozaj chcete predĺžiť permanentku?", "Predĺženie permanentky", JOptionPane.YES_NO_OPTION);

        if (potvrdenie != JOptionPane.YES_OPTION) {
            return;
        }

        Object[] moznosti = {"30 dní", "90 dní", "180 dní"};
        Object vyber = JOptionPane.showInputDialog(this, "Vyber dĺžku predĺženia:", "Predĺženie permanentky", JOptionPane.QUESTION_MESSAGE, null, moznosti, moznosti[0]);

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

    //Podľa režimu systému nastaví dostupnosť akcií upraviť a predĺžiť permanentku pre klienta
    private void nastavDostupnostAkciiPodlaRezimu() {
        boolean offline = SystemRezim.isOffline();

        upravitButton.setEnabled(!offline);
        predlzitPermanentkuButton.setEnabled(!offline);
        zrusitUpravyButton.setEnabled(!offline);
        vymazatKlientaButton.setEnabled(!offline);
        znovaGenerovatQrButton.setEnabled(!offline);

        if (offline) {
            //informácia pre zamestnanca
            JOptionPane.showMessageDialog(this, "Offline režim: úpravy a predĺženie permanentky nie sú dostupné.\n" + "Zobrazenie detailu funguje len na čítanie (XML záloha).", "Offline režim", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Vymazanie klienta po potvrdení
    private void vymazKlienta() {

        int potvrdenie = JOptionPane.showConfirmDialog(
                this,
                "Naozaj chcete vymazať klienta?",
                "Potvrdenie",
                JOptionPane.YES_NO_OPTION
        );

        if (potvrdenie == JOptionPane.YES_OPTION) {
            try {
                detailKlientaServis.vymazatKlienta(klientId);
                JOptionPane.showMessageDialog(this, "Klient bol úspešne vymazaný.");
                dispose();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Chyba pri vymazaní: " + e.getMessage(),
                        "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Zobrazenie QR obrázka klienta
    private void zobrazQrObrazok() {

        Optional<ImageIcon> ikona = detailKlientaServis.nacitajQrIkonu(
                klientId, 200, 200);

        if (ikona.isPresent()) {
            qrObrazokLabel.setIcon(ikona.get());
            qrObrazokLabel.setText("");
        } else {
            qrObrazokLabel.setIcon(null);
            qrObrazokLabel.setText("QR kód nie je dostupný");
        }
    }

    // Vygenerovanie nového QR kódu po potvrdení
    private void znovaVygenerovatQrKod() {

        int potvrdenie = JOptionPane.showConfirmDialog(
                this,
                "Naozaj chcete vygenerovať nový QR kód?",
                "Potvrdenie",
                JOptionPane.YES_NO_OPTION
        );

        if (potvrdenie == JOptionPane.YES_OPTION) {
            try {
                detailKlientaServis.vygenerujNovyQrKod(klientId);
                 // refresh z DB/XML podľa klientId
                JOptionPane.showMessageDialog(this, "Nový QR bol úspešne obnovený");

                nastavRezim(false);
                zobrazQrObrazok();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Chyba pri generovaní nového QR kódu: " + e.getMessage(),
                        "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}







