package org.uzdiz.memento;

import org.uzdiz.composite.VrstaVlaka;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class KartaOriginator {

    private String oznakaVlaka;
    private VrstaVlaka vrstaVlaka;
    private String nacinKupovine;
    private double udaljenost;

    private LocalDate datumPutovanja;

    private LocalTime vrijemePolaska;
    private LocalTime vrijemeDolaska;

    private LocalDateTime trenutnoVrijeme;

    private double izvornaCijena;
    private double konacnaCijena;
    private double popusti;

    private String relacija;


    public String getRelacija() {
        return relacija;
    }


    public KartaOriginator(String oznakaVlaka, VrstaVlaka vrstaVlaka, String nacinKupovine, double udaljenost, LocalDate datumPutovanja, LocalTime vrijemePolaska, LocalTime vrijemeDolaska, double izvornaCijena, double konacnaCijena, double popusti, LocalDateTime trenutnoVrijeme, String relacija) {
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


    public KartaMemento kreirajMemento() {
        return new KartaMemento(this.oznakaVlaka, this.vrstaVlaka, this.nacinKupovine, this.udaljenost, this.datumPutovanja, this.vrijemePolaska, this.vrijemeDolaska, this.izvornaCijena, this.konacnaCijena, this.popusti, this.trenutnoVrijeme, this.relacija);
    }

    public void povratiStanjeIzKarte(KartaMemento karta) {
        this.oznakaVlaka = karta.getOznakaVlaka();
        this.vrstaVlaka = karta.getVrstaVlaka();
        this.nacinKupovine = karta.getNacinKupovine();
        this.datumPutovanja = karta.getDatumPutovanja();
        this.izvornaCijena = karta.getIzvornaCijena();
        this.popusti = karta.getPopusti();
        this.konacnaCijena = karta.getKonacnaCijena();
        this.udaljenost = karta.getUdaljenost();
        this.vrijemePolaska = karta.getVrijemePolaska();
        this.vrijemeDolaska = karta.getVrijemeDolaska();
        this.trenutnoVrijeme = karta.getTrenutnoVrijeme();
        this.relacija = karta.getRelacija();
    }


}
