package org.uzdiz.mediator;

import org.uzdiz.builder.Stanica;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.observer.Strojovoda;

import java.time.LocalTime;
import java.util.LinkedHashMap;

public interface Mediator {
    void registerDriver(Strojovoda driver, KomponentaVoznogReda train);
    void checkAndNotify(KomponentaVoznogReda train, LinkedHashMap<Stanica, LocalTime> arrivalTime);

    boolean isTrainDriverObserver(KomponentaVoznogReda train, String trainDriverName);
}
