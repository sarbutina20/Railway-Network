package org.uzdiz.strategy;

public class KupovinaVlak implements StrategijaKupovine {
    @Override
    public double izracunajCijenu(double osnovnaCijena, double uvecanje, double popustVikend) {
        if(popustVikend > 0) return osnovnaCijena * (1 - popustVikend / 100) * (1 + uvecanje / 100);
        else return osnovnaCijena * (1 + uvecanje / 100);
    }
}
