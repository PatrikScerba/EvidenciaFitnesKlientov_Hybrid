package sk.patrikscerba.ui;

import javax.swing.*;

public class Registracia extends JFrame {

    private JPanel mainPanel;

    public Registracia() {
        setContentPane(mainPanel);
        setTitle("Registrácia klienta");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }
}

