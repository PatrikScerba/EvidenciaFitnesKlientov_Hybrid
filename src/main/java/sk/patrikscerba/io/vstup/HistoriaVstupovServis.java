package sk.patrikscerba.io.vstup;

import sk.patrikscerba.io.log.AppLogServis;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

// Servis na načítanie histórie vstupov zo súboru
public class HistoriaVstupovServis {
    private static final String CESTA = "data/vstupy_log.txt";
    private static final AppLogServis appLog = new AppLogServis();

    public List<String> nacitajRiadky() {
        try {
            Path path = Path.of(CESTA);

            if (!Files.exists(path)) {
                return Collections.emptyList();
            }

            return Files.readAllLines(path);

        } catch (IOException e) {
            appLog.error("Chyba pri nacítani suboru vstupy_log.txt", e);
            return Collections.emptyList();
        }
    }

    // Načíta riadky pre konkrétneho klienta podľa jeho ID
    public List<String> nacitajRiadkyPreKlienta(Long klientId) {

        return nacitajRiadky().stream()
                .filter(riadok -> riadok.contains("| klientId=" + klientId + " "))
                .toList();
    }
}


