package sk.patrikscerba.system;

public class SystemRezim {

    // ak je true → systém je v offline režime
    // ak je false → systém je v online režime
    private static boolean offlineRezim = false;

    /**
     * Nastaví režim systému.
     */
    public static void setOffline(boolean offline) {
        offlineRezim = offline;
    }

    /**
     * Vráti true, ak je systém v offline režime.
     */
    public static boolean isOffline() {
        return offlineRezim;
    }
}

