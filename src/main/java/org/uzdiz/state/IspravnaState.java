package org.uzdiz.state;

import jdk.jshell.spi.ExecutionControl;

public class IspravnaState implements State {

    @Override
    public void postaviIspravnu(RelacijaPruge relacija) {
        //System.out.println("Relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " je već ispravna.");
    }

    @Override
    public void postaviKvar(RelacijaPruge relacija) {
        System.out.println("Uspješno postavio da je relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " u kvaru.");
        relacija.setTrenutnoStanje(relacija.getKvarState());
    }

    @Override
    public void postaviZatvorena(RelacijaPruge relacija) {
        System.out.println("Uspješno postavio da je relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " zatvorena.");
        relacija.setTrenutnoStanje(relacija.getZatvorenaState());
    }

    @Override
    public void postaviTestiranje(RelacijaPruge relacija) {
        System.out.println("Uspješno postavio da je relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " na testiranju.");
        relacija.setTrenutnoStanje(relacija.getTestiranjeState());
    }

    @Override
    public String dohvatiStatus() {
        return "I";
    }
}