package sk.patrikscerba.vstup.servis;

import java.time.LocalDate;

//logika pre kontrolu a predlžovanie platnosti permanentky klienta pri vstupe
public class PermanentkaVstupServis {

    //kontrola platnosti permanentky
    public boolean jePlatnaPermanentka(LocalDate platnaDo) {
        return platnaDo != null && !platnaDo.isBefore(LocalDate.now());
    }

    // vypočíta nový dátum platnosti pri predĺžení o X dní
    public LocalDate predlzODni(LocalDate platnaDo, int dni) {
        LocalDate dnes = LocalDate.now();

        LocalDate zaklad =
                (platnaDo != null && !platnaDo.isBefore(dnes))
                        ? platnaDo
                        : dnes;

        return zaklad.plusDays(dni);
    }

    //vypočíta nový dátum platnosti pri predĺžení o mesiace
    public LocalDate predlzOMesiace(LocalDate platnaDo, int mesiace) {
        LocalDate dnes = LocalDate.now();

        LocalDate zaklad =
                (platnaDo != null && !platnaDo.isBefore(dnes)) ?
                        platnaDo :
                        dnes;

        return zaklad.plusMonths(mesiace);
    }
}


