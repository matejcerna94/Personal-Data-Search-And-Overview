package com.matejcerna.model;

public class Zupanija {

    public int id;
    public String ime_zupanije;

    public Zupanija(int id, String ime_zupanije) {
        this.id = id;
        this.ime_zupanije = ime_zupanije;
    }

    public int getId() {
        return id;
    }

    public String getIme_zupanije() {
        return ime_zupanije;
    }

    @Override
    public String toString() {
        return ime_zupanije;
    }
}
