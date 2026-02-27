package sk.patrikscerba.io.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// Systémove logovanie do súboru app_log.txt
public class AppLogServis {

    private static final String CESTA_SUBORU = "data/app_log.txt";

    // Vytvorí priečinok "data", ak ešte neexistuje
    private void vytvorPriecinokDataAkTreba() {
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Umožňuje zapísať detailné informácie o chybe do log súboru
    private String getStackTrace(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        e.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    // Zápis logu do súboru s časom a úrovňou logu
    private void zapis(String level, String sprava, Throwable e) {
        vytvorPriecinokDataAkTreba();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String cas = LocalDateTime.now().format(formatter).toString();
        String riadok = "[" + cas + "] [" + level + "] " + sprava;

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(CESTA_SUBORU, true))) {
            bufferedWriter.write(riadok);
            bufferedWriter.newLine();

            if (e != null) {
                bufferedWriter.write(getStackTrace(e));
                bufferedWriter.newLine();
            }
        } catch (Exception ex) {

            // Posledná záchrana – ak zlyhá zápis do log súboru, vypíšeme chybu do konzoly
            System.err.println("Zlyhalo zapisovanie do app_log.txt: " + ex.getMessage());
        }
    }

    // Zapíše informačnú správu do log súboru(bežný priebeh aplikácie)
    public void info(String sprava) {
        zapis("INFO", sprava, null);
    }

    // Zapíše varovnú správu do log súboru(menej závažné problémy, podozrivé situácie)
    public void warn(String sprava, Throwable e) {
        zapis("WARN", sprava, null);
    }
    public void warn(String sprava) {
        zapis("WARN", sprava, null);
    }

    // Zapíše chybovú správu spolu s výnimkou do log súboru
    public void error(String sprava, Throwable e) {
        zapis("ERROR", sprava, e);
    }

    // Zapíše chybovú správu do log súboru bez priloženej výnimky
    public void error(String sprava) {
        zapis("ERROR", sprava, null);
    }
}
