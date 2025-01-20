package org.uzdiz.strategy;

public class KupovinaBlagajna implements StrategijaKupovine {
    public double izracunajCijenu(double osnovnaCijena, double popustIliUvecanje, double popustVikend) {
        if(popustVikend > 0) return osnovnaCijena * (1 - popustVikend / 100);
        else return osnovnaCijena;
    }
}