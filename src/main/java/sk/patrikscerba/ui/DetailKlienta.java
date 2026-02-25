package sk.patrikscerba.ui;

import sk.patrikscerba.dao.KlientDao;
import sk.patrikscerba.dao.KlientDaoImpl;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.io.xml.XMLZapisServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.qr.QrVystupServis;
import sk.patrikscerba.servis.DetailKlientaServis;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// UI: Okno DetailKlienta je v procese redizajnu (upravuje sa rozloženie, práca s poliami a panelmi)
// Trieda slúži len  na zobrazenie detailov klienta
public class DetailKlienta extends JFrame {

    private final XMLZapisServis xmlZapisServis = new XMLZapisServis();
    private final AppLogServis appLog = new AppLogServis();
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
    private JButton vytlacitQrButton;

    private JTextField upravitKrstneMeno;
    private JTextField upravitPriezvisko;
    private JTextField upravitEmail;
    private JTextArea  upravitAdresa;
    private JTextField upravitTelefonneCislo;
    private JTextField upravitDatumNarodenia;

    private JTextField txtVek;
    private JTextField txtDatumRegistracie;
    private JTextField txtPermanentkaStav;
    private JTextField txtPlatnostPermanentky;
    private JPanel udajePanel;
    private JPanel buttonPanel;
    private JPanel nadpisPanel1;
    private JPanel nadpisLPanel;
    private JLabel nadpisLabel1;
    private JPanel cyanPanel1;
    private JPanel qrHlavnyPanel;
    private JPanel qrObrazokPanel;
    private JPanel stlpec;
    private JPanel nadpisPanel2;
    private JPanel nadpisPPanel;
    private JLabel nadpisLabel2;
    private JPanel cyanPanel2;
    private JScrollPane JSrollPane;


    private Klient klient;
    private boolean rezim = false;
    private final Long klientId;
    private final DetailKlientaServis detailKlientaServis;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private final QrVystupServis qrVystupServis = new QrVystupServis();


    // Nastavenie okna + načítanie a zobrazenie detailu klienta
    public DetailKlienta(Long klientId, DetailKlientaServis detailKlientaServis) {
        this.detailKlientaServis = detailKlientaServis;
        this.klientId = klientId;

        // Nastavenie okna
        nastavOkno();
        nastavPopisyPoli();
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
        setSize(850, 543);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

    }

    private void nastavPopisyPoli() {
        if (labKrstneMeno != null) labKrstneMeno.setText("Krstné meno:");
        if (labPriezvisko != null) labPriezvisko.setText("Priezvisko:");
        if (labEmail != null) labEmail.setText("Email:");
        if (labAdresa != null) labAdresa.setText("Adresa:");
        if (labTelefonneCislo != null) labTelefonneCislo.setText("Telefónne číslo:");
        if (labDatumNarodenia != null) labDatumNarodenia.setText("Dátum narodenia:");
        if (labDatumRegistracie != null) labDatumRegistracie.setText("Dátum registrácie:");
        if (labVek != null) labVek.setText("Vek:");

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
        zobrazUdajeDoPoli(this.klient);
        obnovZobrazeniePermanentky(this.klient);
    }

    // Nastavenie akcií tlačidiel
    private void nastavAkcieTlacidiel() {

        zatvoritButton.addActionListener(e -> dispose());

        historiaKlientaButton.addActionListener(e -> new HistoriaKlienta(this.klientId, false).setVisible(true));

        predlzitPermanentkuButton.addActionListener(e -> predlzPermanentku());

        upravitButton.addActionListener(e -> {
            if (!rezim) {
                nastavRezim(true);
            } else {
                ulozZmeny();
            }
        });

        zrusitUpravyButton.addActionListener(e -> {
            zobrazUdajeDoPoli(this.klient);
            nastavRezim(false);
            zobrazQrObrazok();
        });

        vymazatKlientaButton.addActionListener(e -> vymazKlienta());

        znovaGenerovatQrButton.addActionListener(e -> znovaVygenerovatQrKod());
        vytlacitQrButton.addActionListener(e -> pripravQrNaTlac());
    }

    //Režim zobrazenia alebo úprav
    private void nastavRezim(boolean uprav) {
        rezim = uprav;

        nastavEditovatelnostPoli(uprav);


        predlzitPermanentkuButton.setVisible(!uprav);
        historiaKlientaButton.setVisible(!uprav);
        zrusitUpravyButton.setVisible(uprav);
        zatvoritButton.setVisible(!uprav);
        vymazatKlientaButton.setVisible(!uprav);
        upravitButton.setText(uprav ? "Uložiť zmeny" : "Upraviť");
        qrObrazokLabel.setVisible(!uprav);
        znovaGenerovatQrButton.setVisible(uprav);
        vytlacitQrButton.setVisible(!uprav);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void nastavEditovatelnostPoli(boolean edit) {
        JTextComponent[] polia = {
                upravitKrstneMeno,
                upravitPriezvisko,
                upravitEmail,
                upravitAdresa,
                upravitTelefonneCislo,
                upravitDatumNarodenia

        };

        for (JTextComponent pole : polia) {
            if (pole == null) continue;

            pole.setEditable(edit);
            pole.setFocusable(edit);

            // aby sa vo view režime neukazoval caret a nepôsobilo to editovateľne
            if (!edit) {
                pole.setCaretPosition(0);
            }
        }

    JTextField[] readOnly = { txtVek, txtDatumRegistracie, txtPermanentkaStav, txtPlatnostPermanentky };
    for (JTextField pole : readOnly) {
        if (pole == null) continue;
        pole.setEditable(false);
        pole.setFocusable(false);
        pole.setCaretPosition(0);
    }
}

    // Prepne režim do režimu úprav
    private void zobrazUdajeDoPoli(Klient klient) {
        upravitKrstneMeno.setText(bezpecnyText(klient.getKrstneMeno()));
        upravitPriezvisko.setText(bezpecnyText(klient.getPriezvisko()));
        upravitEmail.setText(bezpecnyText(klient.getEmail()));
        upravitAdresa.setText(bezpecnyText(klient.getAdresa()));
        upravitTelefonneCislo.setText(bezpecnyText(klient.getTelefonneCislo()));

        upravitDatumNarodenia.setText(
                klient.getDatumNarodenia() != null ? klient.getDatumNarodenia().format(FORMATTER) : ""
        );

        // Vek a registrácia môžu ostať ako info labely viditeľné
        if (txtVek != null) {
            txtVek.setText(String.valueOf(klient.getVek()));
        }
        if (txtDatumRegistracie != null) {
            txtDatumRegistracie.setText(klient.getDatumRegistracie() != null ? klient.getDatumRegistracie().format(FORMATTER) : "-");

        }
    }

    private String bezpecnyText(String s) {
        return s == null ? "" : s;
    }

    // Uloženie zmien po úprave klienta
    private void ulozZmeny() {
        try {
            String meno = upravitKrstneMeno.getText();
            String priezvisko = upravitPriezvisko.getText();
            String datumNarodeniaText = upravitDatumNarodenia.getText();
            String telefon = upravitTelefonneCislo.getText();
            String adresa = upravitAdresa.getText();
            String email = upravitEmail.getText();

            // servis uloží do DB/XML cez hybrid servis (validácie sú v servise)
            detailKlientaServis.ulozUpravyKlienta(
                    klientId,
                    meno,
                    priezvisko,
                    datumNarodeniaText,
                    telefon,
                    adresa,
                    email
            );

            // UI: aktualizácia aktuálneho klienta
            this.klient.setKrstneMeno(meno != null ? meno.trim() : "");
            this.klient.setPriezvisko(priezvisko != null ? priezvisko.trim() : "");
            this.klient.setEmail(email != null ? email.trim() : "");
            this.klient.setAdresa(adresa != null ? adresa.trim() : "");
            this.klient.setTelefonneCislo(telefon != null ? telefon.trim() : "");

            // dátum narodenia: povoliť prázdne
            if (datumNarodeniaText == null || datumNarodeniaText.trim().isBlank()) {
                this.klient.setDatumNarodenia(null);
            } else {
                this.klient.setDatumNarodenia(LocalDate.parse(datumNarodeniaText.trim(), FORMATTER));
            }

            // UI zobrazí aktualizované údaje
            zobrazUdajeDoPoli(this.klient);
            obnovZobrazeniePermanentky(this.klient);

            nastavRezim(false);
            zobrazQrObrazok();

            JOptionPane.showMessageDialog(this, "Údaje boli uložené.");

        } catch (IllegalArgumentException ex) {
            appLog.error("Zlyhalo ulozenie uprav klienta (DB/XML) | klientId=" + klientId, ex);

            JOptionPane.showMessageDialog(
                    this,
                    "Chyba: " + ex.getMessage(),
                    "Chyba", JOptionPane.ERROR_MESSAGE
            );
            nastavRezim(true);

        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chyba systému: " + ex.getMessage(),
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE
            );
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

                JOptionPane.showMessageDialog(this,
                        "Permanentka predĺžená do : " + novaPlatnost);
            } else {
                appLog.warn("DB nepredlzila permanentku (0 riadkov) | klientId=" + klientId);

                JOptionPane.showMessageDialog(this,
                        "Permanentku sa nepodarilo predĺžiť.");
            }

        } catch (IllegalArgumentException | IllegalStateException ex) {
            appLog.error("Zlyhalo predlzenie permanentky (DB/XML) | klientId=" + klientId, ex);

            JOptionPane.showMessageDialog(
                    this,
                    "Chyba: " + ex.getMessage(),
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE
            );
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
            txtPermanentkaStav.setText("Nemá");
            txtPlatnostPermanentky.setText("—");
            return;
        }

        long dni = permanentkaServis.zostavaDni(platnaDo);

        if (permanentkaServis.jePlatnaPermanentka(platnaDo)) {
            txtPermanentkaStav.setText("Aktívna");
            txtPlatnostPermanentky.setText("Platná do: " + platnaDo.format(FORMATTER) + " (" + dni + " dní)");
        } else {
            txtPermanentkaStav.setText("Neplatná");
            txtPlatnostPermanentky.setText("Vypršala: " + platnaDo.format(FORMATTER) + " (" + Math.abs(dni) + " dní po)");
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
        vytlacitQrButton.setEnabled(!offline);

        if (offline) {
            //informácia pre zamestnanca
            JOptionPane.showMessageDialog(this,
                    "Offline režim: úpravy a predĺženie permanentky nie sú dostupné.\n" +
                            "Zobrazenie detailu funguje len na čítanie (XML záloha).",
                    "Offline režim", JOptionPane.WARNING_MESSAGE);
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

            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Chyba", JOptionPane.ERROR_MESSAGE);

            } catch (IllegalStateException e) {
                JOptionPane.showMessageDialog(this,
                        "Chyba systému pri vymazaní klienta:\n" + e.getMessage(),
                        "Chyba", JOptionPane.ERROR_MESSAGE);

            } catch (Exception e) {
                appLog.error("Neznama chyba pri vymazani klienta | klientId=" + klientId, e);

                JOptionPane.showMessageDialog(this,
                        "Neznáma chyba pri vymazaní klienta:\n" + e.getMessage(),
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

            } catch (IllegalArgumentException | IllegalStateException e) {
                appLog.error("Zlyhalo generovanie nového QR kodu | klientId=" + klientId, e);

                JOptionPane.showMessageDialog(this,
                        "Chyba: " + e.getMessage(),
                        "Chyba", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // Príprava QR kódu na tlač a otvorenie priečinka s pripraveným súborom
    private void pripravQrNaTlac() {
        try {
            Path qrCesta = detailKlientaServis.nacitajQrCestu(klientId);
            Path suborNaTlac = qrVystupServis.pripravQrNaTlac(qrCesta);

            if (suborNaTlac == null || suborNaTlac.getParent() == null) {
                throw new IllegalStateException("Nepodarilo sa určiť priečinok pre tlač.");
            }

            Desktop.getDesktop().open(suborNaTlac.getParent().toFile());

        } catch (IllegalArgumentException | IllegalStateException e) {
            appLog.error("Zlyhala priprava QR na tlac (servis/cesta) | klientId=" + klientId, e);

            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Chyba", JOptionPane.ERROR_MESSAGE);

        } catch (IOException e) {
            appLog.error("Zlyhalo otvorenie priecinka pre tlac QR | klientId=" + klientId, e);

            JOptionPane.showMessageDialog(this,
                    "Nepodarilo sa otvoriť priečinok so súborom na tlač:\n" + e.getMessage(),
                    "Chyba", JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            appLog.error("Neznama chyba pri priprave QR na tlac | klientId=" + klientId, e);

            JOptionPane.showMessageDialog(this,
                    "Neznáma chyba pri príprave QR kódu na tlač:\n" + e.getMessage(),
                    "Chyba", JOptionPane.ERROR_MESSAGE);
        }
    }
}










