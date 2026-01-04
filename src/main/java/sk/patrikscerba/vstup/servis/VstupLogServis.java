package sk.patrikscerba.vstup.servis;

import sk.patrikscerba.io.log.AppLogServis;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


//Trieda na zápis logov súvisiacich so vstupmi klientov
public class VstupLogServis {

    private  static final AppLogServis log = new AppLogServis();

    //Cesta k logovaciemu súboru + formát dátumu a času použitý v logu
    private static final String LOG_SUBOR = "data/vstupy_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    //Zapíše správu do log súboru
    public static void zapisLog(String sprava) {

        try (FileWriter writer = new FileWriter(LOG_SUBOR, true)) {

            String cas = LocalDateTime.now().format(FORMATTER);
            writer.write(cas + "| " + sprava + "\n");
            writer.flush();

        } catch (IOException e) {
            log.error("Nepodarilo sa zapísať log:", e);
        }
    }
}
