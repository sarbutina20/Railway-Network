package org.uzdiz.state;

import org.uzdiz.builder.Stanica;

public class RelacijaPruge {

    private final Stanica polaznaStanica;
    private final Stanica odredisnaStanica;

    private final State ispravnaState;
    private final State kvarState;
    private final State zatvorenaState;
    private final State testiranjeState;

    private State trenutnoStanje;

    public RelacijaPruge(Stanica polazna, Stanica odredisna) {
        this.polaznaStanica = polazna;
        this.odredisnaStanica = odredisna;

        this.ispravnaState = new IspravnaState();
        this.kvarState = new KvarState();
        this.zatvorenaState = new ZatvorenaState();
        this.testiranjeState = new TestiranjeState();

        this.trenutnoStanje = ispravnaState;
    }

    public void postaviIspravnu() {
        trenutnoStanje.postaviIspravnu(this);
    }

    public void postaviKvar() {
        trenutnoStanje.postaviKvar(this);
    }

    public void postaviZatvorena() {
        trenutnoStanje.postaviZatvorena(this);
    }

    public void postaviTestiranje() {
        trenutnoStanje.postaviTestiranje(this);
    }

    public State getIspravnaState() { return ispravnaState; }
    public State getKvarState() { return kvarState; }
    public State getZatvorenaState() { return zatvorenaState; }
    public State getTestiranjeState() { return testiranjeState; }

    public void setTrenutnoStanje(State novoStanje) {
        this.trenutnoStanje = novoStanje;
    }

    public String getStatusRelacije() {
        return trenutnoStanje.dohvatiStatus();
    }

    public Stanica getPolaznaStanica() {
        return polaznaStanica;
    }

    public Stanica getOdredisnaStanica() {
        return odredisnaStanica;
    }
}
