package sk.patrikscerba.qr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

// Servisná trieda pre prípravu QR kódu na výstup (tlač, export, odoslanie)
public class QrVystupServis {

    private static final String VYSTUP_PRIECINOK = "vystup";
    private static final String QR_VYSTUP_SUBOR = "qr_vystup.png";

    public QrVystupServis() {
        pripravPriecinok();
    }

    // Zabezpečí existenciu výstupného priečinka pre QR súbor
    private void pripravPriecinok() {
        try {
            Path folder = Path.of(VYSTUP_PRIECINOK);
            if (!Files.exists(folder)) {
                Files.createDirectories(folder);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Chyba pri vytváraní výstupného priečinka", e);
        }
    }

    // Pripraví QR kód na výstup skopírovaním do výstupného priečinka
    // V priečinku sa vždy nachádza len posledný pripravený QR súbor
    public Path pripravQrNaTlac(Path cestaKObrazku) throws Exception {
        Path ciel = Path.of(VYSTUP_PRIECINOK, QR_VYSTUP_SUBOR);
        Files.copy(cestaKObrazku, ciel, StandardCopyOption.REPLACE_EXISTING);
        return ciel;
    }
}
