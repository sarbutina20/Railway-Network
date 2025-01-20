package org.uzdiz.chain_of_responsibility;

import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.observer.Strojovoda;
import org.uzdiz.observer.ObserverPutovanjaVlakom;
import org.uzdiz.singleton.HrvatskeZeljeznice;

public class DSVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public DSVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("DSV")) {
            String joined = String.join(" ", commandParts);
            String[] mainParts = joined.split(" - ");

            if (mainParts.length < 2 || mainParts.length > 4) {
                System.out.println("Upotreba: DSV <ime> <prezime> - <oznakaVlaka>");
                return;
            }

            String trainDriverName = mainParts[0].split(" ", 2)[1];
            String drivingTrain = mainParts[1];

            handleDSV(trainDriverName, drivingTrain);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleDSV(String trainDriverName, String drivingTrain) {
        KomponentaVoznogReda existingTrain = hrvatskeZeljeznice.getVlakovi().stream()
                .filter(t -> t.dohvatiOznaku().equalsIgnoreCase(drivingTrain))
                .findFirst()
                .orElse(null);

        if (existingTrain == null) {
            System.out.println("Pogreška: Vlak s oznakom " + drivingTrain + " ne postoji.");
            return;
        }

        if (hrvatskeZeljeznice.getTrafficControl().isTrainDriverObserver(existingTrain, trainDriverName)) {
            System.out.println("Pogreška: Strojovođa " + trainDriverName + " je već dodan.");
            return;
        } else {
            ObserverPutovanjaVlakom driver = new Strojovoda(trainDriverName, existingTrain, hrvatskeZeljeznice.getTrafficControl());
        }
    }
}
