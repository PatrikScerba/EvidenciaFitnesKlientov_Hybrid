package sk.patrikscerba.ui;

import sk.patrikscerba.model.Klient;
import sk.patrikscerba.servis.VyhladavanieKlientovServis;
import javax.swing.*;
import java.util.List;


// UI okno pre vyhľadávanie klientov podľa mena a priezviska
public class Vyhladavanie extends JFrame {

    private JPanel mainPanel;
    private JTextField jTextKrstneMeno;
    private JTextField jTextPriezvisko;
    private JButton hladatButton;

    private JLabel krstneMenoLabel;
    private JLabel priezviskoLabel;

    private final boolean zobrazit;
    private final VyhladavanieKlientovServis vyhladavanieKlientovServis = new VyhladavanieKlientovServis();

    // Nastavenie okna pre vyhľadávanie klientov
    public Vyhladavanie(boolean zobrazit) {
        this.zobrazit = zobrazit;
        setContentPane(mainPanel);
        setTitle("Vyhľadávanie klienta");
        setSize(400, 300);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        hladatButton.addActionListener(e -> vyhladajKlienta());
        setVisible(true);
    }

    // Vyhľadá klienta podľa mena a priezviska
    private void vyhladajKlienta() {

        String hladaneMeno = jTextKrstneMeno.getText().trim();
        String hladanePriezvisko = jTextPriezvisko.getText().trim();

        if (hladaneMeno.isEmpty() && hladanePriezvisko.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Zadaj aspoň meno alebo priezvisko.",
                    "Vyhľadávanie",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Vyhľadanie klientov cez servisnú vrstvu
        List<Klient> zhodniKlienti;

        try {
            zhodniKlienti = vyhladavanieKlientovServis.vyhladaj(hladaneMeno, hladanePriezvisko);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Chyba pri vyhľadávaní klientov: " + e.getMessage(),
                    "Chyba",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (zhodniKlienti.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nenašiel sa žiadny klient s daným menom alebo priezviskom.",
                    "Výsledok vyhľadávania",
                    JOptionPane.INFORMATION_MESSAGE);

            return;
        }

        // Ak je výsledkov viac, nech sa používateľ rozhodne, ktorého chce zobraziť
        String[] moznosti = zhodniKlienti.stream()
                .map(k -> k.getKrstneMeno() + " " + k.getPriezvisko() + ", ID: " + k.getId())
                .toArray(String[]::new);

        String vyberKlienta = (String) JOptionPane.showInputDialog(
                this,
                "Nájdených viac klientov (" + zhodniKlienti.size() + "). Vyber jedného:",
                "Výber klienta",
                JOptionPane.QUESTION_MESSAGE,
                null,
                moznosti,
                moznosti[0]
        );

        // Používateľ potvrdí výber pre detail vybraného klienta
        if (vyberKlienta != null) {

            for (Klient k : zhodniKlienti) {
                String label = k.getKrstneMeno() + " " + k.getPriezvisko() + ", ID: " + k.getId();
                if (label.equals(vyberKlienta)) {

                    // Zobrazenie detailu klienta v konzole (dočasne)
                    System.out.println("=====Vybraný klient:=====");
                    System.out.println("ID: " + k.getId());
                    System.out.println("Meno: " + k.getKrstneMeno());
                    System.out.println("Priezvisko: " + k.getPriezvisko());
                    System.out.println("Dátum narodenia: " + k.getDatumNarodenia());
                    System.out.println("Telefón: " + k.getTelefonneCislo());
                    System.out.println("Adresa: " + k.getAdresa());
                    System.out.println("Email: " + k.getEmail());

                    break;
                }
            }
        }
    }
}
