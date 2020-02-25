package com.matejcerna;


public class Osoba {

    public int id;
    public String ime;
    public String prezime;
    public String spol;
    public String adresa;
    public String oib;
    public String datum_rodenja;
    public String grad;
    public String mjesto_rodenja;
    public byte[] slika;
    public int zupanija_id;


    public Osoba(int id, String ime, String prezime, String spol, String adresa, String oib, String datum_rodenja, String grad, String mjesto_rodenja, byte[] slika, int zupanija_id) {
        this.id = id;
        this.ime = ime;
        this.prezime = prezime;
        this.spol = spol;
        this.adresa = adresa;
        this.oib = oib;
        this.datum_rodenja = datum_rodenja;
        this.grad = grad;
        this.mjesto_rodenja = mjesto_rodenja;
        this.slika = slika;
        this.zupanija_id = zupanija_id;
    }

    public int getId() {
        return id;
    }

    public String getIme() {
        return ime;
    }

    public String getPrezime() {
        return prezime;
    }

    public String getSpol() {
        return spol;
    }

    public String getAdresa() {
        return adresa;
    }

    public String getOib() {
        return oib;
    }

    public String getDatum_rodenja() {
        return datum_rodenja;
    }

    public String getGrad() {
        return grad;
    }

    public String getMjesto_rodenja() {
        return mjesto_rodenja;
    }

    public byte[] getSlika() {
        return slika;
    }

    public int getZupanija_id(){
        return zupanija_id;
    }
}
