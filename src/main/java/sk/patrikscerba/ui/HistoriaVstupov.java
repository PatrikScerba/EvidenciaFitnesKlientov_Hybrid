package sk.patrikscerba.ui;

import javax.swing.*;

//Zobrazenie histórie vstupov klientov
public class HistoriaVstupov extends  JFrame{

    private JTable tabulka;

    public HistoriaVstupov(){
        setTitle("História vstupov klientov");
        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}
