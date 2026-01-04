package sk.patrikscerba;

import com.formdev.flatlaf.FlatLightLaf;
import sk.patrikscerba.io.db.DatabazaPripojenie;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.ui.HlavneOkno;
import javax.swing.*;


public class EvidenciaFitnesKlientovApp {

    private static final AppLogServis applog = new AppLogServis();

    public static void main(String[] args) {

        boolean dostupnaDatabaza = DatabazaPripojenie.testConnection();

        if (!dostupnaDatabaza) {
            sk.patrikscerba.system.SystemRezim.setOffline(true);

            applog.info("Databáza nedostupná. Aplikácia spustená v OFFLINE režime.");

        } else {
            sk.patrikscerba.system.SystemRezim.setOffline(false);

            applog.info("Databáza dostupná. Aplikácia spustená v ONLINE režime.");

            // Nastavenie moderného vzhľadu FlatLaf
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception e) {
                applog.error("Nepodarilo sa načítať FlatLaf: ", e);
            }

            // Spustenie GUI na správnom vlákne (AWT)
            SwingUtilities.invokeLater(() ->
                    new HlavneOkno().setVisible(true)
            );
        }
    }
}







