package org.uzdiz.chain_of_responsibility;

import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.EtapaVlaka;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.LocalTime;

public class IEVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IEVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IEV")) {
            if (commandParts.length < 2 || commandParts.length > 3) {
                System.out.println("Upotreba: IEV <oznaka vlaka>");
                return;
            }

            String trainIdentifier = commandParts.length == 2 ? commandParts[1] : commandParts[1] + " " + commandParts[2];
            KomponentaVoznogReda targetTrain = hrvatskeZeljeznice.getVlakovi().stream()
                    .filter(train -> train.dohvatiOznaku().equals(trainIdentifier))
                    .findFirst()
                    .orElse(null);

            if (targetTrain == null) {
                System.out.printf("Vlak s oznakom %s nije pronađen.%n", trainIdentifier);
            } else {
                handleIEV(targetTrain);
            }
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIEV(KomponentaVoznogReda targetTrain) {
        System.out.println("=====================================================================================================================================");
        System.out.println("Etape vlaka " + targetTrain.dohvatiOznaku() + ":");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-12s | %-12s | %-20s | %-20s | %-10s | %-10s | %-10s | %-12s%n",
                "Oznaka vlaka", "Oznaka pruge", "Polazna stanica", "Odredišna stanica",
                "Polazak", "Dolazak", "Km", "Dani u tjednu");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");

        for (KomponentaVoznogReda stage : targetTrain.dohvatiEtape()) {
            if (stage instanceof EtapaVlaka etapaVlaka) {

                LocalTime arrivalTime = etapaVlaka.dohvatiVrijemePolaska()
                        .plusHours(etapaVlaka.getTrajanjeVoznje().getHour())
                        .plusMinutes(etapaVlaka.getTrajanjeVoznje().getMinute());

                double distance = hrvatskeZeljeznice.getStationManager().calculateDistanceBetweenStations(
                        etapaVlaka.dohvatiPolaznuStanicu().getNaziv(),
                        etapaVlaka.dohvatiOdredisnuStanicu().getNaziv()
                );

                System.out.printf("%-12s | %-12s | %-20s | %-20s | %-10s | %-10s | %-10.2f | %-12s%n",
                        etapaVlaka.getOznakaVlaka(),
                        etapaVlaka.dohvatiOznakuPruge(),
                        etapaVlaka.dohvatiPolaznuStanicu().getNaziv(),
                        etapaVlaka.dohvatiOdredisnuStanicu().getNaziv(),
                        etapaVlaka.getVrijemePolaska(),
                        arrivalTime,
                        distance,
                        etapaVlaka.getOznakaDanaAsString());
            }
        }
        System.out.println("=====================================================================================================================================");
    }


}
