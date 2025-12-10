package sk.patrikscerba;

import sk.patrikscerba.dao.DatabazaPripojenie;
import java.sql.Connection;

public class EvidenciaFitnesKlientovApp {
    public static void main(String[] args) {
        testDbConnection();
    }

    //Overí pripojenie k databáze a vypíše výsledok do konzoly
    private static void testDbConnection() {
        try(Connection connection = DatabazaPripojenie.getConnection()){
            if (connection != null){
                System.out.println("Pripojenie úspešné");

            }else {
                System.out.println("pripojenie zlyhalo");
            }
        }catch (Exception e ){
            System.out.println("chyba pri pripájaní " + e.getMessage());
        }
    }
}