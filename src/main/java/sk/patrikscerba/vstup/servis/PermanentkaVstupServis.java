package sk.patrikscerba.vstup.servis;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Logika pre kontrolu a predlžovanie platnosti permanentky klienta pri vstupe
public class PermanentkaVstupServis {

    // Kontrola platnosti permanentky
    public boolean jePlatnaPermanentka(LocalDate platnaDo) {
        if (platnaDo == null) {
            return false;
        }
        return !platnaDo.isBefore(LocalDate.now());
    }

    // Vypočíta nový dátum platnosti pri predĺžení o X dní
    public LocalDate predlzODni(LocalDate platnaDo, int dni) {
        LocalDate dnes = LocalDate.now();

        LocalDate zaklad =
                (platnaDo != null && !platnaDo.isBefore(dnes))
                        ? platnaDo
                        : dnes;

        return zaklad.plusDays(dni);
    }

    // Vypočíta, koľko dní zostáva (môže byť aj záporné)
    public long zostavaDni(LocalDate platnaDo) {
        if (platnaDo == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), platnaDo);
    }

}


