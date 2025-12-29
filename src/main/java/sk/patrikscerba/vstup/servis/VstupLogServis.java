package sk.patrikscerba.vstup.servis;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


//Trieda na zápis logov súvisiacich so vstupmi klientov
public class VstupLogServis {

    //Cesta k logovaciemu súboru + formát dátumu a času použitý v logu
    private static final String LOG_SUBOR = "data/vstupy_log.txt";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    //Zapíše správu do log súboru
    public static void zapisLog(String sprava) {
        System.out.println("LOG_SUBOR = " + Path.of(LOG_SUBOR).toAbsolutePath());
        try (FileWriter writer = new FileWriter(LOG_SUBOR, true)) {

            String cas = LocalDateTime.now().format(FORMATTER);
            writer.write(cas + "| " + sprava + "\n");
            writer.flush();

        } catch (IOException e) {
            System.err.println("Nepodarilo sa zapísať log:" + e.getMessage());
        }
    }
}
