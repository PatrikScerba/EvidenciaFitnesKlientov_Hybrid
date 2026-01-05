package sk.patrikscerba.io.vstup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

// Servis na načítanie histórie vstupov zo súboru
public class HistoriaVstupovServis {
    private static final String CESTA = "data/vstupy_log.txt";

    public List<String> nacitajRiadky() {
        try {
            Path path = Path.of(CESTA);

            if (!Files.exists(path)) {
                return Collections.emptyList();
            }

            return Files.readAllLines(path);

        } catch (IOException e) {
            return Collections.singletonList("Chyba pri načítaní súboru: " + e.getMessage());
        }
    }
}

