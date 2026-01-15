package sk.patrikscerba.ui;

import javax.swing.*;


public class HistoriaKlienta extends JFrame {

    private JTable tabulka;

    // Konstruktor triedy ZoznamKlientov nastaví okno, načíta klientov a naplní tabuľku
    public HistoriaKlienta() {
        setTitle("História klienta");
        setSize(900, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tabulka = new JTable();
        add(new JScrollPane(tabulka));

    }
}



