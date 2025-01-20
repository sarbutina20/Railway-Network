package org.uzdiz.observer;

import org.uzdiz.builder.Stanica;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.EtapaVlaka;
import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpraviteljPutovanjaVlakom implements SubjektPutovanjaVlakom {


    private final Map<String, List<ObserverPutovanjaVlakom>> vlakObserveri = new HashMap<>();
    private final Map<String, List<ObserverPutovanjaVlakom>> stanicaObserveri = new HashMap<>();

    private AtomicBoolean prekinuto = new AtomicBoolean(false);

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public UpraviteljPutovanjaVlakom(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }


    public void dodajObserverZaVlak(String trainId, ObserverPutovanjaVlakom observer) {
        vlakObserveri.computeIfAbsent(trainId, k -> new ArrayList<>()).add(observer);
    }

    public void dodajObserverZaStanicu(String stationName, ObserverPutovanjaVlakom observer) {
        stanicaObserveri.computeIfAbsent(stationName, k -> new ArrayList<>()).add(observer);
    }

    @Override
    public void makniObserverZaStanicu(String stationName, ObserverPutovanjaVlakom observer) {
        List<ObserverPutovanjaVlakom> observers = stanicaObserveri.get(stationName);
        if (observers != null) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                stanicaObserveri.remove(stationName);
            }
        }
    }

    public void obavijestiObservereZaVlak(String trainId, String message) {
        List<ObserverPutovanjaVlakom> observers = vlakObserveri.getOrDefault(trainId, Collections.emptyList());
        for (ObserverPutovanjaVlakom observer : observers) {
            observer.update(message);
        }
    }

    public void obavijestiObservereZaStanicu(String stationName, String message) {
        List<ObserverPutovanjaVlakom> observers = stanicaObserveri.getOrDefault(stationName, Collections.emptyList());
        for (ObserverPutovanjaVlakom observer : observers) {
            observer.update(message);
        }
    }

    @Override
    public void makniObserverZaVlak(String trainId, ObserverPutovanjaVlakom observer) {
        List<ObserverPutovanjaVlakom> observers = vlakObserveri.get(trainId);
        if (observers != null) {
            observers.remove(observer);
            if (observers.isEmpty()) {
                vlakObserveri.remove(trainId);
            }
        }
    }

    public boolean postojiObserverZaStanicu(String stationName, ObserverPutovanjaVlakom observer) {
        List<ObserverPutovanjaVlakom> observers = stanicaObserveri.get(stationName);
        return observers != null && observers.contains(observer);
    }

    public boolean postojiObserverZaVlak(String idVlaka, ObserverPutovanjaVlakom observer) {
        List<ObserverPutovanjaVlakom> observers = vlakObserveri.get(idVlaka);
        return observers != null && observers.contains(observer);
    }

    public void simulacijaVoznjeVlakom(String idVlaka, String dan, int koeficijent, UpraviteljStanicama upraviteljStanicama) {
        prekinuto.set(false);
        KomponentaVoznogReda train = hrvatskeZeljeznice.getVlakovi().stream()
                .filter(t -> t.dohvatiOznaku().equals(idVlaka))
                .findFirst()
                .orElse(null);

        if (train == null) {
            System.out.println("Pogreška: Vlak s oznakom " + idVlaka + " ne postoji.");
            return;
        }

        boolean dayTagFound = train.dohvatiEtape().stream()
                .anyMatch(stage -> stage.dohvatiOznakuDana().stream()
                        .anyMatch(dayTag -> dayTag.getCode().equalsIgnoreCase(dan)));

        if (!dayTagFound) {
            System.out.println("Pogreška: Vlak " + idVlaka + " ne vozi na dan " + dan);
            return;
        }

        LinkedHashMap<Stanica, LocalTime> stationArrivals = upraviteljStanicama.calculateStationArrivals(train, dan);

        hrvatskeZeljeznice.getTrafficControl().checkAndNotify(train, stationArrivals);

        if (stationArrivals.isEmpty()) {
            System.out.println("Pogreška: Nisu pronađene stanice za simulaciju.");
            return;
        }

        Iterator<Map.Entry<Stanica, LocalTime>> iterator = stationArrivals.entrySet().iterator();
        Map.Entry<Stanica, LocalTime> firstEntry = iterator.next();
        LocalTime virtualTime = firstEntry.getValue();
        Stanica departureStation = firstEntry.getKey();

        System.out.println("================================================================================================");
        System.out.println("Početak simulacije za vlak " + idVlaka + " na dan " + dan + " s koeficijentom " + koeficijent);
        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.println("Polazna stanica: " + departureStation.getNaziv() + " u " + virtualTime + " na pruzi " + departureStation.getOznakaPruge());
        System.out.println("------------------------------------------------------------------------------------------------");


        Thread inputThread = new Thread(() -> {
            try {
                while (!prekinuto.get()) {
                    if (System.in.available() > 0) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                        if (reader.ready()) {
                            String input = reader.readLine();
                            if (input != null && input.equalsIgnoreCase("X")) {
                                prekinuto.set(true);
                                System.out.println("Simulacija je prekinuta.");
                                break;
                            }
                        }
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Greška u unosu: " + e.getMessage());
            }
        });


        inputThread.setDaemon(true);
        inputThread.start();

        while (iterator.hasNext()) {
            if (prekinuto.get()) {
                return;
            }

            Map.Entry<Stanica, LocalTime> arrivalEntry = iterator.next();
            Stanica currentStation = arrivalEntry.getKey();
            LocalTime targetTime = arrivalEntry.getValue();

            while (virtualTime.isBefore(targetTime)) {
                if (prekinuto.get()) {
                    return;
                }

                try {
                    long sleepTimeMillis = 60_000L / koeficijent;
                    Thread.sleep(sleepTimeMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Simulacija je prekinuta.");
                    return;
                }


                virtualTime = virtualTime.plusMinutes(1);
                //System.out.println("Vrijeme simulacije: " + virtualTime);
            }


            String message = "Vlak " + idVlaka + " je stigao u stanicu " + currentStation.getNaziv() + " u " + virtualTime + " na pruzi " + currentStation.getOznakaPruge();
            System.out.println("------------------------------------------------------------------------------------------------");

            System.out.println(message);
            obavijestiObservereZaVlak(idVlaka, message);
            obavijestiObservereZaStanicu(currentStation.getNaziv(), message);

        }

        if (!prekinuto.get()) {
            System.out.println("Simulacija završena: vlak " + idVlaka + " je stigao na krajnju stanicu.");
            System.out.println("================================================================================================\n");
            prekinuto.set(true);
        }
    }

}
