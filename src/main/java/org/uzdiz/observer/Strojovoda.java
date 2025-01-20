package org.uzdiz.observer;

import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.mediator.Mediator;

public class Strojovoda implements ObserverPutovanjaVlakom {
    private String ime;
    private String idVlaka;
    private Mediator mediator;

    public Strojovoda(String ime, KomponentaVoznogReda train, Mediator mediator) {
        this.ime = ime;
        this.idVlaka = idVlaka;
        this.mediator = mediator;
        mediator.registerDriver(this, train);
    }

    @Override
    public void update(String message) {
        System.out.println("Obavijest za vozača " + ime + ": " + message);
    }

    public String getIme() {
        return ime;
    }


}