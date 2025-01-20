package org.uzdiz.builder;

public class Kompozicija {
    private final String oznaka;
    private final String oznakaPrijevoznogSredstva;
    private final String uloga;
    private final int linija;

    public Kompozicija(String oznaka, String oznakaPrijevoznogSredstva, String uloga, int linija) {
        this.oznaka = oznaka;
        this.oznakaPrijevoznogSredstva = oznakaPrijevoznogSredstva;
        this.uloga = uloga;
        this.linija = linija;
    }

    public String getOznaka() {
        return oznaka;
    }

    public String getOznakaPrijevoznogSredstva() {
        return oznakaPrijevoznogSredstva;
    }

    public String getUloga() {
        return uloga;
    }

    public int getLinija() {
        return linija;
    }
}
