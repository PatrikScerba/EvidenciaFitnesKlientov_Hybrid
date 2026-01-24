package sk.patrikscerba.model;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;


public class Klient {

    private Long id;
    private String krstneMeno;
    private String priezvisko;
    private LocalDate datumNarodenia;
    private String telefonneCislo;
    private String adresa;
    private String email;
    private LocalDate datumRegistracie;
    private LocalDate permanentkaPlatnaDo;
    private String qrCesta;
    private String qrToken;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public Klient() {
    }

    // konštruktor pre databázu
    public Klient(Long id, String krstneMeno, String priezvisko, LocalDate datumNarodenia,
                  String telefonneCislo, String adresa, String email, LocalDate datumRegistracie, String qrCesta, String qrToken) {
        this.id = id;
        this.krstneMeno = krstneMeno;
        this.priezvisko = priezvisko;
        this.datumNarodenia = datumNarodenia;
        this.telefonneCislo = telefonneCislo;
        this.adresa = adresa;
        this.email = email;
        this.datumRegistracie = datumRegistracie;
        this.qrCesta = qrCesta;
        this.qrToken = qrToken;

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

    public Klient(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public LocalDate getPermanentkaPlatnaDo() {
        return permanentkaPlatnaDo;
    }

    public void setPermanentkaPlatnaDo(LocalDate permanentkaPlatnaDo) {
        this.permanentkaPlatnaDo = permanentkaPlatnaDo;
    }

    //Vypočíta sa vek a vráti vypočítaný vek klienta na základe dátumu narodenia
    public int getVek() {
        if (datumNarodenia == null) {
            return 0;
        }
        return Period.between(datumNarodenia, LocalDate.now()).getYears();
    }

    public String getQrCesta() {
        return qrCesta;
    }

    public void setQrCesta(String qrCesta) {
        this.qrCesta = qrCesta;
    }
    public String getQrToken() {
        return qrToken;
    }
    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }
}





