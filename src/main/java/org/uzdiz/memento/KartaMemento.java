package org.uzdiz.memento;

import org.uzdiz.composite.VrstaVlaka;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
public class KartaMemento {
    private final String oznakaVlaka;
    private final VrstaVlaka vrstaVlaka;
    private final String nacinKupovine;
    private final double udaljenost;
    private final LocalDate datumPutovanja;
    private final LocalTime vrijemePolaska;



    private final LocalTime vrijemeDolaska;

    private final LocalDateTime trenutnoVrijeme;

    private final double izvornaCijena;
    private final double konacnaCijena;
    private final double popusti;

    private final String relacija;

    public KartaMemento(String oznakaVlaka, VrstaVlaka vrstaVlaka, String nacinKupovine, double udaljenost, LocalDate datumPutovanja, LocalTime vrijemePolaska, LocalTime vrijemeDolaska, double izvornaCijena, double konacnaCijena,
                        double popusti, LocalDateTime trenutnoVrijeme, String relacija) {
        this.oznakaVlaka = oznakaVlaka;
        this.vrstaVlaka = vrstaVlaka;
        this.nacinKupovine = nacinKupovine;
        this.udaljenost = udaljenost;
        this.datumPutovanja = datumPutovanja;
        this.vrijemePolaska = vrijemePolaska;
        this.vrijemeDolaska = vrijemeDolaska;
        this.izvornaCijena = izvornaCijena;
        this.konacnaCijena = konacnaCijena;
        this.popusti = popusti;
        this.trenutnoVrijeme = trenutnoVrijeme;
        this.relacija = relacija;
    }

    public String getOznakaVlaka() { return oznakaVlaka; }
    public VrstaVlaka getVrstaVlaka() { return vrstaVlaka; }
    public String getNacinKupovine() { return nacinKupovine; }

    public LocalDate getDatumPutovanja() {
        return datumPutovanja;
    }

    public String getRelacija() {
        return relacija;
    }


    public double getUdaljenost() { return udaljenost; }
    public LocalDateTime getTrenutnoVrijeme() { return trenutnoVrijeme; }

    public LocalTime getVrijemePolaska() { return vrijemePolaska; }

    public LocalTime getVrijemeDolaska() { return vrijemeDolaska; }

    public double getIzvornaCijena() { return izvornaCijena; }

    public double getKonacnaCijena() { return konacnaCijena; }

    public double getPopusti() { return popusti; }
}

