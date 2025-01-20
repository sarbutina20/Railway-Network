package org.uzdiz.strategy;



public class KupovinaAplikacija implements StrategijaKupovine {

    @Override
    public double izracunajCijenu(double osnovnaCijena, double popust, double popustVikend) {
        if(popustVikend > 0)
            return osnovnaCijena * (1 - popustVikend / 100) * (1 - popust / 100);
        else return osnovnaCijena * (1 - popust / 100);
    }
}

