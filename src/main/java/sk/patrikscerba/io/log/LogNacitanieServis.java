package sk.patrikscerba.servis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Servis pre čítanie systémových logov
public class SystemLogServis {

    private static final String LOG_SUBOR = "data/app_log.txt";

    public List<String> nacitajRiadky() {

        List<String> riadky = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(LOG_SUBOR))) {

            String riadok;

            while ((riadok = bufferedReader.readLine()) != null) {
                riadky.add(riadok);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return riadky;
    }
}