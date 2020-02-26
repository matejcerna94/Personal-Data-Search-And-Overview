package com.matejcerna;

public class Admin {

    int id;
    String korisnicko_ime;
    String lozinka;
    byte [] slika;

    public Admin(int id, String korisnicko_ime, String lozinka, byte [] slika) {
        this.id = id;
        this.korisnicko_ime = korisnicko_ime;
        this.lozinka = lozinka;
        this.slika = slika;
    }

    public int getId() {
        return id;
    }

    public String getKorisniko_ime() {
        return korisnicko_ime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public byte[] getSlika() {
        return slika;
    }
}
