package org.uzdiz.state;

public class KvarState implements State {

    @Override
    public void postaviIspravnu(RelacijaPruge relacija) {
        System.out.println("Relaciju " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " ne možemo postaviti u ispravno stanje pošto je prvo potrebno provesti testiranje.");
    }

    @Override
    public void postaviKvar(RelacijaPruge relacija) {
        System.out.println("Relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " je već u kvaru.");
    }

    @Override
    public void postaviZatvorena(RelacijaPruge relacija) {
        System.out.println("Uspješno zatvorio relaciju " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv());
        relacija.setTrenutnoStanje(relacija.getZatvorenaState());
    }

    @Override
    public void postaviTestiranje(RelacijaPruge relacija) {
        System.out.println("Relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " je uspješno postavljena u testiranje.");
        relacija.setTrenutnoStanje(relacija.getTestiranjeState());
    }

    @Override
    public String dohvatiStatus() {
        return "K";
    }
}