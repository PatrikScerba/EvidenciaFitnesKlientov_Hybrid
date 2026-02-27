package sk.patrikscerba.io.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Servisná trieda slúži na načítanie systémových logov zo súboru
public class LogNacitanieServis {

    private static final String LOG_SUBOR = "data/app_log.txt";
    private final AppLogServis appLog = new AppLogServis();

    // Načíta riadky zo súboru systémového logu a vráti ich ako zoznam
    public List<String> nacitajRiadky() {

        List<String> riadky = new ArrayList<>();

        File file = new File(LOG_SUBOR);

        if (!file.exists()) {
            return riadky;
        }

        try (BufferedReader bufferedReader =
                     new BufferedReader(new FileReader(LOG_SUBOR))) {

            String riadok;

            // Postupné načítanie všetkých riadkov zo súboru
            while ((riadok = bufferedReader.readLine()) != null) {
                riadky.add(riadok);
            }

        } catch (IOException e) {
            appLog.error("Chyba pri nacitani suboru app_log.txt" + LOG_SUBOR, e);
        }
        return riadky;
    }
}
