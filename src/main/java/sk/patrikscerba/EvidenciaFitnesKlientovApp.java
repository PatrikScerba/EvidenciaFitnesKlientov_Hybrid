package sk.patrikscerba;

import com.formdev.flatlaf.FlatLightLaf;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.ui.HlavneOkno;

import javax.swing.*;

public class EvidenciaFitnesKlientovApp {

    private static final AppLogServis applog = new AppLogServis();

    public static void main(String[] args) {

        // Nastavenie moderného vzhľadu FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            applog.error("Nepodarilo sa načítať FlatLaf: ", e);
        }

        // Test pripojenia na databázu + nastavenie režimu (online/offline)
        boolean dostupnaDb = sk.patrikscerba.io.db.DatabazaPripojenie.testConnection();

        if (!dostupnaDb) {
            sk.patrikscerba.system.SystemRezim.setOffline(true);
            applog.info("Databáza nedostupná - offline režim");
        } else {
            sk.patrikscerba.system.SystemRezim.setOffline(false);
            applog.info("Databáza dostupná - online režim");
        }

        // Spustenie GUI na správnom vlákne (AWT)
        SwingUtilities.invokeLater(() -> {
            new HlavneOkno().setVisible(true);
        });
    }
}
