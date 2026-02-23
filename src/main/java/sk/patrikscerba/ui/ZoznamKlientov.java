package sk.patrikscerba.ui;

import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.ZoznamKlientovServis;
import sk.patrikscerba.vstup.servis.PermanentkaVstupServis;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;


//UI Okno pre zobrazenie zoznamu klientov v tabuľke
public class ZoznamKlientov extends JFrame {

    private final ZoznamKlientovServis zoznamKlientovServis = new ZoznamKlientovServis();
    private final PermanentkaVstupServis permanentkaVstupServis = new PermanentkaVstupServis();
    private final AppLogServis appLog = new AppLogServis();

    private static final DateTimeFormatter FORMAT_DATUMU = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private JTable tabulka;

    // Konstruktor triedy ZoznamKlientov nastaví okno, načíta klientov a naplní tabuľku
    public ZoznamKlientov() {
        setTitle("Zoznam klientov");
        setSize(1280, 520);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        tabulka = new JTable();
        add(new JScrollPane(tabulka));

        //Model tabuľky (stĺpce + riadky)
        DefaultTableModel model = new DefaultTableModel(){
        @Override
        public boolean isCellEditable ( int row, int column){
            return false;
        }
    };

        model.addColumn("Poradie");
        model.addColumn("ID");
        model.addColumn("Krstné meno");
        model.addColumn("Priezvisko");
        model.addColumn("Dátum narodenia");
        model.addColumn("Vek");
        model.addColumn("Email");
        model.addColumn("Telefón");
        model.addColumn("Adresa");
        model.addColumn("Dátum registrácie");
        model.addColumn("Permanentka");
        model.addColumn("Platnosť");

        try {
            List<Klient> klienti = zoznamKlientovServis.nacitajKlientov();

            int poradie = 1;

            for (Klient k : klienti) {

                int vek = 0;
                if (k.getDatumNarodenia() != null) {
                    vek = Period.between(k.getDatumNarodenia(), LocalDate.now()).getYears();
                }

                String stavPermanentky;
                String platnostPermanentky;

                LocalDate platnaDo = k.getPermanentkaPlatnaDo();

                if (platnaDo == null) {
                    stavPermanentky = "Nemá";
                    platnostPermanentky = "—";
                } else {
                    long dni = permanentkaVstupServis.zostavaDni(platnaDo);

                    if (permanentkaVstupServis.jePlatnaPermanentka(platnaDo)) {
                        stavPermanentky = "Platná";
                        platnostPermanentky = dni + " dní";
                    } else {
                        stavPermanentky = "Neplatná";
                        platnostPermanentky = Math.abs(dni) + " dní po";
                    }
                }

                String datumNarodeniaText = k.getDatumNarodenia() != null ? k.getDatumNarodenia().format(FORMAT_DATUMU) : "";
                String datumRegistracieText = k.getDatumRegistracie() != null ? k.getDatumRegistracie().format(FORMAT_DATUMU) : "";

                // Pridanie riadku do tabuľky s údajmi o klientovi
                model.addRow(new Object[]{
                        poradie++,
                        k.getId(),
                        k.getKrstneMeno(),
                        k.getPriezvisko(),
                        datumNarodeniaText,
                        vek,
                        k.getEmail(),
                        k.getTelefonneCislo(),
                        k.getAdresa(),
                        datumRegistracieText,
                        stavPermanentky,
                        platnostPermanentky
                });
            }

            tabulka.setModel(model);
            tabulka.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tabulka.setRowHeight(26);

            TableColumnModel columnModel = tabulka.getColumnModel();
            columnModel.getColumn(0).setMinWidth(60); // Poradie
            columnModel.getColumn(0).setMaxWidth(60);
            columnModel.getColumn(1).setMinWidth(60); // ID
            columnModel.getColumn(1).setMaxWidth(60);
            columnModel.getColumn(2).setPreferredWidth(90);  // Krstné meno
            columnModel.getColumn(3).setPreferredWidth(130); // Priezvisko
            columnModel.getColumn(4).setPreferredWidth(110); // Dátum narodenia
            columnModel.getColumn(5).setMinWidth(60); // Vek
            columnModel.getColumn(5).setMaxWidth(60);
            columnModel.getColumn(6).setPreferredWidth(180); // Email
            columnModel.getColumn(7).setPreferredWidth(120);  // Telefón
            columnModel.getColumn(8).setPreferredWidth(140); // Adresa
            columnModel.getColumn(9).setPreferredWidth(110); // Dátum registrácie
            columnModel.getColumn(10).setPreferredWidth(90); // Permanentka
            columnModel.getColumn(11).setPreferredWidth(100); // Platnosť

            DefaultTableCellRenderer center = new DefaultTableCellRenderer();
            center.setHorizontalAlignment(SwingConstants.CENTER);

            DefaultTableCellRenderer left = new DefaultTableCellRenderer();
            left.setHorizontalAlignment(SwingConstants.LEFT);

            tabulka.getColumnModel().getColumn(0).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(1).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(2).setCellRenderer(left);
            tabulka.getColumnModel().getColumn(3).setCellRenderer(left);
            tabulka.getColumnModel().getColumn(4).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(5).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(6).setCellRenderer(left);
            tabulka.getColumnModel().getColumn(7).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(8).setCellRenderer(left);
            tabulka.getColumnModel().getColumn(9).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(10).setCellRenderer(center);
            tabulka.getColumnModel().getColumn(11).setCellRenderer(center);

        } catch (IllegalArgumentException | IllegalStateException ex) {
            appLog.error("Chyba systemu pri nacítani zoznamu klientov | okno=ZoznamKlientov", ex);

            JOptionPane.showMessageDialog(this,
                    "Chyba pri načítaní klientov: " + ex.getMessage(),
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            appLog.error("Neocakavana chyba pri nacitani zoznamu klientov | okno=ZoznamKlientov", ex);

            JOptionPane.showMessageDialog(this,
                    "Nastala neočakávaná chyba pri načítaní klientov.",
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
