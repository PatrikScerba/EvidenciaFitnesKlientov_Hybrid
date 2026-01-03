package sk.patrikscerba;

import com.formdev.flatlaf.FlatLightLaf;
import sk.patrikscerba.ui.HlavneOkno;
import javax.swing.*;

public class EvidenciaFitnesKlientovApp {

    public static void main(String[] args) {

        // Nastavenie moderného vzhľadu FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Nepodarilo sa načítať FlatLaf: " + e.getMessage());
        }

        // Spustenie GUI na správnom vlákne (AWT)
        SwingUtilities.invokeLater(() ->
            new HlavneOkno().setVisible(true)
        );
    }
}






