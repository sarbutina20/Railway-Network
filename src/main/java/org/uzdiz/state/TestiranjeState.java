package org.uzdiz.state;

public class TestiranjeState implements State {

    @Override
    public void postaviIspravnu(RelacijaPruge relacija) {
        System.out.println("Uspješno postavio da je relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " ispravna.");
        relacija.setTrenutnoStanje(relacija.getIspravnaState());
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
        System.out.println("Relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " je već u testiranju.");
    }

    @Override
    public String dohvatiStatus() {
        return "T";
    }
}
