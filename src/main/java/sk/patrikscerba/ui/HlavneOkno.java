package sk.patrikscerba.ui;

import javax.swing.*;

public class HlavneOkno extends JFrame {

    private JPanel mainPanel;
    private JButton Registracia;
    private JButton Vyhladanie;
    private JButton Klienti;
    private JLabel verzia3Label;
    private JLabel developedByPatrikŠčerbaLabel;

    //Nastavenie hlavného okna aplikácie.
    public HlavneOkno(){

        setContentPane(mainPanel);
        setTitle("Evidencia klientov");
        setSize(650, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        //Nastavenie akcií tlačidiel v hlavnom okne
        Registracia.addActionListener(e -> new Registracia().setVisible(true));
        Vyhladanie.addActionListener(e -> new Vyhladavanie().setVisible(true));
        Klienti.addActionListener(e ->  new ZoznamKlientov().setVisible(true));
    }
}
