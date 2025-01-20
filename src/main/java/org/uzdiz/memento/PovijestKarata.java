package org.uzdiz.memento;

import java.util.ArrayList;
import java.util.List;

public class PovijestKarata {
    private final List<KartaMemento> povijest = new ArrayList<>();

    public void dodajKartu(KartaMemento karta) {
        povijest.add(karta);
    }

    public KartaMemento dohvatiKartu(int index) {
        return povijest.get(index);
    }

    public List<KartaMemento> dohvatiSveKarte() {
        return povijest;
    }
}