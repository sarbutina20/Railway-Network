package org.uzdiz.chain_of_responsibility;

import org.uzdiz.singleton.HrvatskeZeljeznice;

public class IPHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;
    public IPHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IP")) {
            if(commandParts.length != 1) {
                System.out.println("Pogreška: Naredba IP ne prima argumente.");
                return;
            }
            handleIP();
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIP() {
        System.out.println("======================================================================================================");
        System.out.printf("%-15s | %-30s | %-30s | %-10s%n",
                "Oznaka pruge", "Početna stanica", "Završna stanica", "Ukupna udaljenost (km)");
        System.out.println("------------------------------------------------------------------------------------------------------");

        hrvatskeZeljeznice.getPruge().forEach(railway -> {
            String oznakaPruge = railway.getOznakaPruge();
            String startStation = railway.getStations().getFirst().getNaziv();
            String endStation = railway.getStations().getLast().getNaziv();
            int totalDistance = railway.getTotalDistance();

            System.out.printf("%-15s | %-30s | %-30s | %-10d%n",
                    oznakaPruge, startStation, endStation, totalDistance);
        });

        System.out.println("======================================================================================================");
    }
}
