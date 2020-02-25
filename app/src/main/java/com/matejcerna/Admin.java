package com.matejcerna;

public class Admin {

    int id;
    String korisniko_ime;
    String lozinka;
    byte [] slika;

    public Admin(int id, String korisniko_ime, String lozinka, byte [] slika) {
        this.id = id;
        this.korisniko_ime = korisniko_ime;
        this.lozinka = lozinka;
        this.slika = slika;
    }

    public int getId() {
        return id;
    }

    public String getKorisniko_ime() {
        return korisniko_ime;
    }

    public String getLozinka() {
        return lozinka;
    }

    public byte[] getSlika() {
        return slika;
    }
}
