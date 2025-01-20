package org.uzdiz.observer;

public interface SubjektPutovanjaVlakom {
    void dodajObserverZaVlak(String trainId, ObserverPutovanjaVlakom observer);
    void makniObserverZaVlak(String trainId, ObserverPutovanjaVlakom observer);
    void obavijestiObservereZaVlak(String trainId, String message);

    void dodajObserverZaStanicu(String stationName, ObserverPutovanjaVlakom observer);
    void makniObserverZaStanicu(String stationName, ObserverPutovanjaVlakom observer);
    void obavijestiObservereZaStanicu(String stationName, String message);
}
