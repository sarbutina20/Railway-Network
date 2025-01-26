package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.EtapaVlaka;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class IVRVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IVRVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IVRV")) {
            if (commandParts.length < 2 || commandParts.length > 3) {
                System.out.println("Upotreba: IVRV <oznaka vlaka>");
                return;
            }

            var trainID = commandParts.length == 2 ? commandParts[1] : commandParts[1] + " " + commandParts[2];
            KomponentaVoznogReda train = hrvatskeZeljeznice.getVlakovi().stream()
                    .filter(t -> t.dohvatiOznaku().equals(trainID))
                    .findFirst()
                    .orElse(null);

            if (train == null) {
                System.out.printf("Vlak s oznakom %s nije pronađen.%n", trainID);
                return;
            }

            Stanica polazna = train.dohvatiEtape().getFirst().dohvatiPolaznuStanicu();
            Stanica odredisna = train.dohvatiEtape().getLast().dohvatiOdredisnuStanicu();
            boolean provjeraIspravnostiRute = train.provjeraIspravnostiRute(polazna, odredisna);
            if(!provjeraIspravnostiRute) {
                System.out.println("Relacije na pruzi po kojoj vozi vlak nisu ispravne pa nije moguće prikazati vozni red.");
                return;
            }

            handleIVRV(train);

        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIVRV(KomponentaVoznogReda train) {
        System.out.println("================================================================================================================");
        System.out.printf("Vozni red za vlak: %s%n", train.dohvatiOznaku());
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-10s | %-20s | %-10s | %-15s%n",
                "Oznaka vlaka", "Oznaka pruge", "Željeznička stanica", "Polazak", "Udaljenost (km)");
        System.out.println("----------------------------------------------------------------------------------------------------------------");

        double totalDistance = 0;
        LocalTime departureTime;
        ZeljeznickaPruga pruga;
        for (KomponentaVoznogReda stage : train.dohvatiEtape()) {
            if (stage instanceof EtapaVlaka etapaVlaka) {
                 pruga = hrvatskeZeljeznice.getRailwayByOznaka(etapaVlaka.dohvatiOznakuPruge());

                //List<Stanica> fullRoute = pruga.getStations();
                List<Stanica> fullRoute = etapaVlaka.dohvatiSveStanice();
                List<Stanica> routeForPrint = switch (etapaVlaka.dohvatiVrstaVlaka()) {
                    //case NORMALNI -> pruga.getStations();
                    case NORMALNI -> etapaVlaka.dohvatiSveStanice();
                    case UBRZANI -> pruga.getUbrzaneStanice();
                    case BRZI -> pruga.getBrzeStanice();
                };

                departureTime = etapaVlaka.getVrijemePolaska();
                String lastPrintedStation = null;
                Integer travelTime;

                List<Stanica> filteredFull   = filterStationByRange(fullRoute, etapaVlaka);
                List<Stanica> filteredPrint  = filterStationByRange(routeForPrint, etapaVlaka);

                for (int i = 0; i < filteredFull.size(); i++) {
                    Stanica currentStation = filteredFull.get(i);

                    if (i > 0) {
                        if (currentStation.getNaziv().equalsIgnoreCase(lastPrintedStation) && currentStation.getNaziv().equalsIgnoreCase(filteredFull.get(i - 1).getNaziv())) {
                            continue;
                        }
                        if (etapaVlaka.dohvatiSmjer().equalsIgnoreCase("N")) {
                            totalDistance += currentStation.getDuzina();
                            travelTime = pruga.getTimeForType(currentStation, etapaVlaka.dohvatiVrstaVlaka());
                            if (travelTime != null) {
                                departureTime = departureTime.plusMinutes(travelTime);
                            }
                        } else {
                            totalDistance += filteredFull.get(i - 1).getDuzina();
                            travelTime = pruga.getTimeForType(filteredFull.get(i - 1), etapaVlaka.dohvatiVrstaVlaka());
                            if (travelTime != null) {
                                departureTime = departureTime.plusMinutes(travelTime);
                            }
                        }
                    }


                    boolean isLastInEtapa = currentStation.getNaziv()
                            .equalsIgnoreCase(etapaVlaka.dohvatiOdredisnuStanicu().getNaziv());
                    if (filteredPrint.contains(currentStation) || isLastInEtapa) {
                        if (currentStation.getNaziv().equalsIgnoreCase(lastPrintedStation)) {
                            continue;
                        }
                        System.out.printf("%-15s | %-10s | %-20s | %-10s | %-15.2f%n",
                                etapaVlaka.getOznakaVlaka(),
                                etapaVlaka.dohvatiOznakuPruge(),
                                currentStation.getNaziv(),
                                isLastInEtapa ? "-" : departureTime,
                                (i == 0 && stage.equals(train.dohvatiEtape().getFirst()))
                                        ? 0
                                        : totalDistance
                        );
                        lastPrintedStation = currentStation.getNaziv();
                    }
                }
            }
        }

        System.out.println("================================================================================================================");
    }

    private List<Stanica> filterStationByRange(List<Stanica> stationsAlongPath, EtapaVlaka etapaVlaka) {
        boolean withinRange = false;
        List<Stanica> filteredStations = new ArrayList<>();
        if (etapaVlaka.dohvatiSmjer().equalsIgnoreCase("O")) {
            for (int i = stationsAlongPath.size() - 1; i >= 0; i--) {
                Stanica station = stationsAlongPath.get(i);

                if (station.getNaziv().equalsIgnoreCase(etapaVlaka.dohvatiPolaznuStanicu().getNaziv())) {
                    withinRange = true;
                }

                if (withinRange) {
                    filteredStations.add(station);
                }

                if (station.getNaziv().equalsIgnoreCase(etapaVlaka.dohvatiOdredisnuStanicu().getNaziv())) {
                    return filteredStations;
                }
            }
        } else {
            for (Stanica station : stationsAlongPath) {
                if (station.getNaziv().equalsIgnoreCase(etapaVlaka.dohvatiPolaznuStanicu().getNaziv())) {
                    withinRange = true;
                }
                if (withinRange) {
                    filteredStations.add(station);
                }
                if (station.getNaziv().equalsIgnoreCase(etapaVlaka.dohvatiOdredisnuStanicu().getNaziv())) {
                    return filteredStations;
                }
            }
        }
        return filteredStations;
    }

}
