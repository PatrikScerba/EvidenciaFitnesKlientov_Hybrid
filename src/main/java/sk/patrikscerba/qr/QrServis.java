package sk.patrikscerba.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import sk.patrikscerba.io.log.AppLogServis;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


// Servisná trieda pre generovanie a ukladanie QR kódov
public class QrServis {

    private static final int SIZE = 270;
    private static final String QR_PRIECINOK = "qr_kody";
    private final AppLogServis appLog = new AppLogServis();

    // Vytvorí priečinok pre QR kódy, ak neexistuje
    public QrServis() {

        pripravPriecinok();
    }

    private void pripravPriecinok() {

        try {
            Files.createDirectories(Path.of(QR_PRIECINOK));
        } catch (IOException e) {
            appLog.error("Nepodarilo sa vytvorit priecinok pre QR kody | path=" + QR_PRIECINOK, e);
            throw new IllegalStateException("Chyba pri vytváraní priečinka pre QR kódy.", e);
        }
    }

    // Vytvorí text pre QR kód z ID klienta a tokenu
    public String vytvorQrText(Long klientId, String qrToken) {
        if (klientId == null) {
            throw new IllegalArgumentException("ID klienta nesmie byť null.");
        }
        if (qrToken == null || qrToken.isBlank()) {
            throw new IllegalArgumentException("QR token nesmie byť null alebo prázdny.");
        }
        return "KLIENT:" + klientId + ";TOKEN:" + qrToken;
    }

    // Vygeneruje QR obrázok z textu
    public BufferedImage vygenerujQrObrazok(String text) throws WriterException {
        BitMatrix matrix = new MultiFormatWriter()
                .encode(text, BarcodeFormat.QR_CODE, SIZE, SIZE);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    // Uloží QR obrázok do súboru, vráti cestu k súboru
    public String ulozQrObrazok(Long klientId, BufferedImage qrObrazok) throws IOException {

        if (klientId == null) {
            throw new IllegalArgumentException("ID klienta nesmie byť null.");
        }
        if (qrObrazok == null) {
            throw new IllegalArgumentException("QR obrázok nesmie byť null.");
        }

        String nazovSuboru = "klient_qr_" + klientId + ".png";
        Path cestaKSuboru = Path.of(QR_PRIECINOK, nazovSuboru);

        boolean zapisane = javax.imageio.ImageIO.write(qrObrazok, "PNG", cestaKSuboru.toFile());

        if (!zapisane) {
            appLog.error("ImageIO.write vratilo false pri ukladani PNG | klientId=" + klientId
                    + " | subor=" + cestaKSuboru, null);
            throw new IllegalStateException("Nepodarilo sa zapísať PNG (ImageIO.write vrátilo false). Súbor: " + cestaKSuboru);
        }
        return cestaKSuboru.toString(); //uložená cesta pre DB
    }

    // Vygeneruje a uloží QR obrázok pre klienta, vráti cestu k súboru
    public String vygenerujAUlozQrObrazok(Long klientId, String qrToken) {
        try {
            String qrText = vytvorQrText(klientId, qrToken);

            BufferedImage qrObrazok = vygenerujQrObrazok(qrText);
            return ulozQrObrazok(klientId, qrObrazok);

        } catch (WriterException e) {
            appLog.error("Zlyhalo generovanie QR (ZXing) | klientId=" + klientId, e);
            throw new IllegalStateException("Chyba pri generovaní alebo ukladaní QR kódu pre klienta ID: " + klientId, e);

        } catch (IOException e) {
            appLog.error("Zlyhalo ulozenie QR na disk | klientId=" + klientId
                    + " | priecinok=" + QR_PRIECINOK, e);
            throw new IllegalStateException("Chyba pri ukladaní QR obrázka na disk pre klienta ID: " + klientId, e);
        }
    }
}







