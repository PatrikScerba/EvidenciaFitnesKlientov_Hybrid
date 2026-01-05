package sk.patrikscerba.ui;

import javax.swing.*;

public class Vyhladavanie extends JFrame {

    private  JPanel mainPanel;

    public Vyhladavanie(){
        setContentPane(mainPanel);
        setTitle("Vyhľadávanie klienta");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
