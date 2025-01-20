package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ISPHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;
    public ISPHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("ISP")) {
            if (commandParts.length != 3) {
                System.out.println("Upotreba: ISP <oznakaLinije> <redoslijed (N/O)>");
                return;
            }
            handleISP(commandParts[1], commandParts[2].toUpperCase());
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleISP(String oznakaLinije, String order) {
        ZeljeznickaPruga foundRailway = hrvatskeZeljeznice.getPruge().stream()
                .filter(pruga -> pruga.getOznakaPruge().equals(oznakaLinije))
                .findFirst()
                .orElse(null);

        if (foundRailway == null) {
            System.out.println("Željeznička pruga s oznakom " + oznakaLinije + " nije pronađena.");
            return;
        }

        System.out.println("======================================================================================================");
        System.out.printf("Prikaz stajališta za prugu: %-15s%n", foundRailway.getOznakaPruge());
        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("%-30s | %-15s | %-30s%n",
                "Naziv stanice", "Udaljenost (km)", "Vrsta stanice");
        System.out.println("------------------------------------------------------------------------------------------------------");

        List<Stanica> stations = foundRailway.getStations();
        List<Integer> distances = foundRailway.getDistances();

        double cumulativeDistance = 0;

        if (order.equals("N")) {
            for (int i = 0; i < stations.size(); i++) {
                if (i > 0 && stations.get(i).getNaziv().equalsIgnoreCase(stations.get(i - 1).getNaziv())) {
                    continue;
                }

                if (i < distances.size()) {
                    cumulativeDistance += distances.get(i);
                }

                System.out.printf("%-30s | %-15.2f | %-30s%n",
                        stations.get(i).getNaziv(),
                        cumulativeDistance,
                        stations.get(i).getVrstaStanice());
            }
        } else if (order.equals("O")) {
            List<Stanica> reversedStations = new ArrayList<>(stations);
            List<Integer> reversedDistances = new ArrayList<>(distances);
            Collections.reverse(reversedStations);
            Collections.reverse(reversedDistances);

            for (int i = 0; i < reversedStations.size(); i++) {
                if (i > 0 && reversedStations.get(i).getNaziv().equalsIgnoreCase(reversedStations.get(i - 1).getNaziv())) {
                    continue;
                }

                if (i > 0 && i <= reversedDistances.size()) {
                    cumulativeDistance += reversedDistances.get(i - 1);
                }

                System.out.printf("%-25s | %-10.2f | %-20s%n",
                        reversedStations.get(i).getNaziv(),
                        cumulativeDistance,
                        reversedStations.get(i).getVrstaStanice());
            }
        } else {
            System.out.println("Neispravan redoslijed. Koristite 'N' za normalno ili 'O' za obrnuto.");
        }

        System.out.println("======================================================================================================");
    }
}