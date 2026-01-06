package sk.patrikscerba.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class Klient {

    private int id;
    private String krstneMeno;
    private String priezvisko;
    private LocalDate datumNarodenia;
    private String telefonneCislo;
    private String adresa;
    private String email;
    private LocalDate datumRegistracie;
    private LocalDate permanentkaPlatnaDo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public Klient(){
    }

    // konštruktor pre databázu
    public Klient(int id, String krstneMeno, String priezvisko, LocalDate datumNarodenia,
                  String telefonneCislo, String adresa, String email, LocalDate datumRegistracie) {
        this.id = id;
        this.krstneMeno = krstneMeno;
        this.priezvisko = priezvisko;
        this.datumNarodenia = datumNarodenia;
        this.telefonneCislo = telefonneCislo;
        this.adresa = adresa;
        this.email = email;
        this.datumRegistracie = datumRegistracie;

    }
    // konštruktor pre registráciu nového klienta
    public Klient(String krstneMeno, String priezvisko, LocalDate datumNarodenia,
                  String telefonneCislo, String adresa, String email) {
        this.krstneMeno = krstneMeno;
        this.priezvisko = priezvisko;
        this.datumNarodenia = datumNarodenia;
        this.telefonneCislo = telefonneCislo;
        this.adresa = adresa;
        this.email = email;

    }

    public Klient(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKrstneMeno() {
        return krstneMeno;
    }

    public void setKrstneMeno(String krstneMeno) {
        this.krstneMeno = krstneMeno;
    }

    public String getPriezvisko() {
        return priezvisko;
    }

    public void setPriezvisko(String priezvisko) {
        this.priezvisko = priezvisko;
    }

    public LocalDate getDatumNarodenia() {
        return datumNarodenia;
    }

    public void setDatumNarodenia(LocalDate datumNarodenia) {
        this.datumNarodenia = datumNarodenia;
    }

    public String getTelefonneCislo() {
        return telefonneCislo;
    }

    public void setTelefonneCislo(String telefonneCislo) {
        this.telefonneCislo = telefonneCislo;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public LocalDate getDatumRegistracie() {
        return datumRegistracie;
    }

    public void setDatumRegistracie(LocalDate datumRegistracie) {
        this.datumRegistracie = datumRegistracie;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDate getPermanentkaPlatnaDo(){
        return permanentkaPlatnaDo;
    }
    public void setPermanentkaPlatnaDo(LocalDate permanentkaPlatnaDo){
        this.permanentkaPlatnaDo = permanentkaPlatnaDo;
    }
}


