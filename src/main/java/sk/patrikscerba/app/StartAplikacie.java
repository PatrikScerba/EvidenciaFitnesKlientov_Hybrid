package sk.patrikscerba.app;

import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import sk.patrikscerba.io.db.DatabazaPripojenie;
import sk.patrikscerba.io.log.AppLogServis;
import sk.patrikscerba.system.SystemRezim;
import sk.patrikscerba.ui.HlavneOkno;

import javax.swing.*;


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
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (Exception e) {
            appLog.error("Nepodarilo sa nastaviť vzhľad aplikácie. ", e);
        }
    }

    // Kontrola dostupnosti databázy a nastavenie režimu systému (online/offline)
    private static void nastavRezimPodlaDatabazy(){
        boolean dostupnaDb = DatabazaPripojenie.testConnection();

        SystemRezim.setOffline(!dostupnaDb);

        if (!dostupnaDb) {
            appLog.info("Databáza je nedostupná - offline režim");
        } else {
            appLog.info("Databáza je dostupná - online režim");
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