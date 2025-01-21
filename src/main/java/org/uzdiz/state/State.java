package org.uzdiz.state;

public interface State {
    void postaviIspravnu(RelacijaPruge relacija);
    void postaviKvar(RelacijaPruge relacija);
    void postaviZatvorena(RelacijaPruge relacija);
    void postaviTestiranje(RelacijaPruge relacija);

    String dohvatiStatus();
}
