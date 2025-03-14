package org.uzdiz.mediator;

import org.uzdiz.builder.Stanica;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.observer.Strojovoda;
import org.uzdiz.observer.ObserverPutovanjaVlakom;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

public class KontrolaPrometa implements Mediator {
    private final Map<KomponentaVoznogReda, ObserverPutovanjaVlakom> driversByTrain;

    private static final int TIME_PROXIMITY_MINUTES = 10;

    private final HrvatskeZeljeznice hrvatskeZeljeznice;


    public KontrolaPrometa(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.driversByTrain = new HashMap<>();
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;

    }

    public boolean isTrainDriverObserver(KomponentaVoznogReda train, String name) {
        Strojovoda driver = (Strojovoda) driversByTrain.get(train);
        return driver != null && driver.getIme().equalsIgnoreCase(name);
    }

    @Override
    public void registerDriver(Strojovoda driver, KomponentaVoznogReda train) {
        String trainId = train.dohvatiOznaku();
        if (driversByTrain.containsKey(train)) {
            System.out.println("Vlak " + trainId + " već ima registriranog vozača.");
        } else {
            driversByTrain.put(train, driver);
            System.out.println("Vozač " + driver.getIme() + " registriran za vlak " + trainId + ".");
        }
    }

    @Override
    public void checkAndNotify(KomponentaVoznogReda mainTrain, LinkedHashMap<Stanica, LocalTime> mainArrivals) {
        for (Map.Entry<KomponentaVoznogReda, ObserverPutovanjaVlakom> entry : driversByTrain.entrySet()) {
            KomponentaVoznogReda otherTrain = entry.getKey();

            if (otherTrain.equals(mainTrain)) continue;
            Map<Stanica, LocalTime> otherArrivals = hrvatskeZeljeznice.getStationManager().calculateStationArrivals(otherTrain, null);
            if (otherArrivals == null) continue;

            Set<Stanica> commonStations = new HashSet<>();
            for (Stanica mainStation : mainArrivals.keySet()) {
                for (Stanica otherStation : otherArrivals.keySet()) {
                    if (mainStation.getNaziv().equalsIgnoreCase(otherStation.getNaziv())) {
                        commonStations.add(mainStation);
                    }
                }
            }

            if(commonStations.isEmpty()) continue;
            for (Stanica mainStation : mainArrivals.keySet()) {
                for (Stanica otherStation : otherArrivals.keySet()) {
                    if (mainStation.getNaziv().equalsIgnoreCase(otherStation.getNaziv())) {

                        LocalTime mainTime = mainArrivals.get(mainStation);
                        LocalTime otherTime = otherArrivals.get(otherStation);

                        if (mainTime != null && otherTime != null) {
                            long diff = Math.abs(Duration.between(mainTime, otherTime).toMinutes());

                            if (diff <= TIME_PROXIMITY_MINUTES) {
                                notifyDrivers(mainTrain, otherTrain, mainStation, diff); // Use mainStation or create a merged object
                            }
                        }
                    }
                }
            }

        }
    }

    private void notifyDrivers(KomponentaVoznogReda mainTrain, KomponentaVoznogReda otherTrain, Stanica station, long timeDifference) {
        ObserverPutovanjaVlakom mainDriver = driversByTrain.get(mainTrain);
        if (mainDriver != null) {
            mainDriver.update(createNotificationMessage(otherTrain, station, timeDifference));
        }

        ObserverPutovanjaVlakom otherDriver = driversByTrain.get(otherTrain);
        if (otherDriver != null) {
            otherDriver.update(createNotificationMessage(mainTrain, station, timeDifference));
        }
    }

    private String createNotificationMessage(KomponentaVoznogReda otherTrain, Stanica station, long timeDifference) {
        return "Vlak (" + otherTrain.dohvatiOznaku() + ") dolazi na istu stanicu "
                + station.getNaziv() + " za " + timeDifference + " minuta/e.";
    }


}



