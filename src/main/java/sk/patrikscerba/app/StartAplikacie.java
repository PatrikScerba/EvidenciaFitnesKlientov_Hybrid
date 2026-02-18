package sk.patrikscerba.app;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import sk.patrikscerba.io.db.DatabazaPripojenie;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.ui.HlavneOkno;

import javax.swing.*;
import java.awt.*;


// Trieda zabezpečí správny štart aplikácie
public class StartAplikacie {

    private static final AppLogServis appLog = new AppLogServis();

    public static  void spusti(){

        nastavVzhlad();
        nastavSlovencinuDialogov();
        nastavRezimPodlaDatabazy();

        SwingUtilities.invokeLater(() -> {
            new HlavneOkno();
            informujOfflineRezim();
        });
        appLog.info("Aplikacia bola spustena.");
    }

    // Nastavenie slovenských textov pre dialógy
    private static  void nastavSlovencinuDialogov(){
        UIManager.put("OptionPane.yesButtonText", "Áno");
        UIManager.put("OptionPane.noButtonText", "Nie");
        UIManager.put("OptionPane.cancelButtonText", "Zrušiť");
        UIManager.put("OptionPane.okButtonText", "OK");
    }

    // Nastavenie vzhľadu aplikácie na FlatLaf s tmavým režimom
    private static void nastavVzhlad() {
        try {
            UIManager.put("Panel.background", new Color(30,30,30));
            UIManager.put("Button.background", new Color(70,70,70));
            UIManager.put("Component.background", new Color(37,37,37));
            UIManager.put("Button.hoverBackground", new Color(60,170,210));

            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            appLog.error("Nepodarilo sa nastavit vzhlad aplikacie. ", e);
        }
    }

    // Kontrola dostupnosti databázy a nastavenie režimu systému (online/offline)
    private static void nastavRezimPodlaDatabazy(){
        boolean dostupnaDb = DatabazaPripojenie.testConnection();

        SystemRezim.setOffline(!dostupnaDb);

        if (!dostupnaDb) {
            appLog.info("Databaza je nedostupna - offline rezim");
        } else {
            appLog.info("Databaza je dostupna - online rezim");
        }
    }

    // Informovanie užívateľa o offline režime a obmedzeniach, ktoré z toho vyplývajú
    private  static void informujOfflineRezim(){
        if (SystemRezim.isOffline()) {
           JOptionPane.showMessageDialog(
                    null,
                    "Databáza je nedostupná - aplikácia beží v offline režime.\n" +
                            "Niektoré funkcie môžu byť obmedzené.",
                    "Offline režim",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }
}
