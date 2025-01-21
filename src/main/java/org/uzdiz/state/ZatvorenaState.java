package org.uzdiz.state;

public class ZatvorenaState implements State {

    @Override
    public void postaviIspravnu(RelacijaPruge relacija) {
        System.out.println("Relaciju " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " ne možemo postaviti u ispravno stanje pošto je prvo potrebno provesti testiranje.");
    }

    @Override
    public void postaviKvar(RelacijaPruge relacija) {
        System.out.println("Uspješno postavio da je relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " zatvorena.");
        relacija.setTrenutnoStanje(relacija.getKvarState());
    }

    @Override
    public void postaviZatvorena(RelacijaPruge relacija) {
        System.out.println("Relacija " + relacija.getPolaznaStanica().getNaziv() + " - " + relacija.getOdredisnaStanica().getNaziv() + " je već zatvorena.");
    }

    @Override
    public void postaviTestiranje(RelacijaPruge relacija) {
        System.out.println("Uspješno postavio da je relacija " + relacija.getPolaznaStanica() + " - " + relacija.getOdredisnaStanica() + " zatvorena.");
        relacija.setTrenutnoStanje(relacija.getTestiranjeState());
    }

    @Override
    public String dohvatiStatus() {
        return "Z";
    }
}
