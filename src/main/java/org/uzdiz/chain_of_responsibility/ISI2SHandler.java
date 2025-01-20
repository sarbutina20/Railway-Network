package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.util.List;

public class ISI2SHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public ISI2SHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("ISI2S")) {
            if (commandParts.length < 3) {
                System.out.println("Upotreba: ISI2S <početnaStanica> - <krajnjaStanica>");
                return;
            }

            String[] userEnteredStations = String.join(" ", commandParts).substring(6).trim().split(" - ");

            String startStation = userEnteredStations[0].trim();
            String endStation = userEnteredStations[1].trim();

            handleISI2S(startStation, endStation);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleISI2S(String startStation, String endStation) {
        UpraviteljStanicama upraviteljStanicama = hrvatskeZeljeznice.getStationManager();
        List<ZeljeznickaPruga> allRailways = hrvatskeZeljeznice.getPruge();

        UpraviteljStanicama.PathResult pathResult = upraviteljStanicama.handleDifferentRailways(startStation, endStation, allRailways);

        if (pathResult.getDistance() == Double.POSITIVE_INFINITY) {
            System.out.println("===================================================================");
            System.out.printf("Nema puta između stanica %s i %s.%n", startStation, endStation);
            System.out.println("===================================================================");
        } else {
            System.out.println("===================================================================");
            System.out.printf("Put između stanica: %-10s i %-10s%n", startStation, endStation);
            System.out.println("-------------------------------------------------------------------");
            System.out.printf("%-20s | %-15s%n", "Stanica", "Udaljenost od početne (km)");
            System.out.println("-------------------------------------------------------------------");

            double cumulativeDistance = 0;

            for (int i = 0; i < pathResult.getPath().size(); i++) {
                String station = pathResult.getPath().get(i);

                System.out.printf("%-20s | %-15.2f%n", station, cumulativeDistance);

                if (pathResult.getDistancesToNext().containsKey(station)) {
                    cumulativeDistance += pathResult.getDistancesToNext().get(station);
                }
            }

            System.out.println("-------------------------------------------------------------------");
            System.out.printf("Ukupna udaljenost: %-10s ->     %-10s: %.2f km%n", startStation, endStation, pathResult.getDistance());
            System.out.println("===================================================================");
        }
    }


}