package org.uzdiz.strategy;

import org.uzdiz.singleton.PostavkeCijena;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class KontekstKupovine {
    private StrategijaKupovine strategija;

    public void postaviStrategiju(StrategijaKupovine strategija) {
        this.strategija = strategija;
    }

    public double izracunajCijenu(double osnovnaCijena, LocalDate datum, double popustIliUvecanje) {

        PostavkeCijena postavke = PostavkeCijena.getInstance();
        double popustVikend = 0;

        if(provjeriVikend(datum)) {
            popustVikend = postavke.dohvatiPopustSuN();
        }

        return strategija.izracunajCijenu(osnovnaCijena, popustIliUvecanje, popustVikend);
    }


    public double vratiPopuste(LocalDate datumPutovanja) {
        PostavkeCijena postavke = PostavkeCijena.getInstance();
        double popusti = 0;

        if(provjeriVikend(datumPutovanja)) {
            double popustVikend = postavke.dohvatiPopustSuN();
            popusti += popustVikend;
        }

        if(strategija instanceof KupovinaAplikacija) {
            double popustAplikacija = postavke.dohvatiPopustWebMob();
            popusti += popustAplikacija;
        }
        return popusti;
    }

    private boolean provjeriVikend(LocalDate datumPutovanja) {
        DayOfWeek day = datumPutovanja.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}