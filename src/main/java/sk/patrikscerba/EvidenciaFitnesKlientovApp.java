package sk.patrikscerba;

public class EvidenciaFitnesKlientovApp {
    public static void main(String[] args) {

        boolean dostupnaDatabaza = sk.patrikscerba.dao.DatabazaPripojenie.testConnection();

        if (!dostupnaDatabaza){

            sk.patrikscerba.servis.SystemRezim.setOffline(true);
            System.out.println("Databáza nie je  nedostupná. Offline režim.");
        }else {
            sk.patrikscerba.servis.SystemRezim.setOffline(false);
            System.out.println("Databáza je dostupná. Online režim.");
        }
    }
}











