package sk.patrikscerba.qr;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


// Servisná trieda určená na archiváciu QR obrázkov do ZIP archívu.
public class ArchivServis {

    // Priečinok, kde sa nachádza archív.
    private static final String ARCHIV_PRIECINOK = "data";

    // Názov ZIP archívu s QR kódmi.
    private static final String ARCHIV_MENO = "qr_archiv.zip";

    // Konštruktor zabezpečí, že existuje priečinok pre dáta.
    public ArchivServis() {

        pripravPriecinokArchivu();
    }

    // Pripraví priečinok pre dáta, ak ešte neexistuje.
    private void pripravPriecinokArchivu() {
        try {
            Files.createDirectories(Path.of(ARCHIV_PRIECINOK));

        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri vytváraní priečinka pre dáta.", e);
        }
    }

    // Pridá QR obrázok do ZIP archívu.
    // Ak archív už existuje, zachová jeho obsah a doplní nový QR kód (prípadne nahradí rovnaký názov).
    public void pridajQrDoZip(String nazovSuboru, Path cestaKObrazku) {

        if (cestaKObrazku == null || !Files.exists(cestaKObrazku)) {
            throw new IllegalArgumentException("Cesta k obrázku je neplatná: " + cestaKObrazku);
        }

        Path archivCesta = Path.of(ARCHIV_PRIECINOK, ARCHIV_MENO);

        // Očistený názov entry (bez priečinkov a podozrivých znakov).
        String nazovEntry = normalizujNazovEntry(nazovSuboru);

        // Dočasný archív, do ktorého sa všetko poskladá nanovo.
        Path docasnyArchiv;

        try {
            docasnyArchiv = Files.createTempFile(
                    Path.of(ARCHIV_PRIECINOK),
                    "qr_zip_temp_",
                    ".zip"
            );

        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri vytváraní dočasného archívu.", e);
        }

        try (ZipOutputStream zipOutputStream =
                     new ZipOutputStream(Files.newOutputStream(docasnyArchiv))) {

            if (Files.exists(archivCesta)) {
                skopirujStareZipEntryOkrem(archivCesta, zipOutputStream, nazovEntry);
            }

            // Pridanie nového QR obrázka do archívu.
            zipOutputStream.putNextEntry(new ZipEntry(nazovEntry));

            try (InputStream inputStream = Files.newInputStream(cestaKObrazku)) {
                byte[] buffer = new byte[4096];
                int length;

                while ((length = inputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }
            }

            zipOutputStream.closeEntry();

        } catch (IOException e) {

            throw new IllegalStateException("Chyba pri práci so ZIP archívom.", e);
        }

        try {
            // Nahradí pôvodný archív novým.
            Files.move(docasnyArchiv, archivCesta, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Chyba pri nahrádzaní pôvodného ZIP archívu novým.", e);
        }
    }

    //Skopíruje všetky položky zo starého ZIP archívu do už otvoreného ZipOutputStreamu,
    //okrem tej, ktorá má rovnaký názov (aby sa dala prepísať).
    private void skopirujStareZipEntryOkrem(Path povodnyZip, ZipOutputStream zipOutputStream, String preskocNazov) {

        try (ZipInputStream zipInputStream =
                     new ZipInputStream(Files.newInputStream(povodnyZip))) {

            ZipEntry entry;
            byte[] buffer = new byte[4096];

            while ((entry = zipInputStream.getNextEntry()) != null) {

                // Ak už existuje QR s rovnakým názvom, preskočí a nahradí sa novým.
                if (entry.getName().equals(preskocNazov)) {
                    zipInputStream.closeEntry();
                    continue;
                }

                zipOutputStream.putNextEntry(new ZipEntry(entry.getName()));

                int length;
                while ((length = zipInputStream.read(buffer)) > 0) {
                    zipOutputStream.write(buffer, 0, length);
                }

                zipOutputStream.closeEntry();
                zipInputStream.closeEntry();
            }

        } catch (Exception e) {
            throw new IllegalStateException("Chyba pri kopírovaní položiek zo ZIP archívu.", e);
        }
    }

    // Normalizuje názov entry v ZIP archíve (pridá príponu .png, očistí názov)
    private String normalizujNazovEntry(String nazov) {

        String cisty = vycistiNazovSuboru(nazov);

        if (!cisty.toLowerCase().endsWith(".png")) {
            cisty += ".png";
        }
        return cisty;
    }

    // Očistí názov súboru, aby neobsahoval priečinky alebo nebezpečné znaky.
    private String vycistiNazovSuboru(String nazov) {

        if (nazov == null || nazov.isBlank()) {
            return "qr.png";
        }

        // Odstráni lomítka a nechá len názov súboru.
        String cisty = nazov.replace("\\", "/");
        if (cisty.contains("/")) {
            cisty = cisty.substring(cisty.lastIndexOf("/") + 1);
        }

        // Zamedzí prechodu do iných adresárov.
        cisty = cisty.replace("..", "");

        return cisty.isBlank() ? "qr.png" : cisty;
    }
}
