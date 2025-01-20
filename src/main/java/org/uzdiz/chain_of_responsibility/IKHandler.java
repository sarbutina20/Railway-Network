package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Kompozicija;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.util.List;

public class IKHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IKHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IK")) {
            if (commandParts.length != 2) {
                System.out.println("Upotreba: IK <oznaka>");
                return;
            }
            String compositionDesignation = commandParts[1];

            handleIK(compositionDesignation);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIK(String compositionDesignation) {
        System.out.println("===================================================================");
        System.out.printf("Prikazivanje kompozicije: %-15s%n", compositionDesignation);
        System.out.println("-------------------------------------------------------------------");

        List<Kompozicija> kompozicije = hrvatskeZeljeznice.getKompozicije().stream()
                .filter(kompozicija -> kompozicija.getOznaka().equals(compositionDesignation))
                .toList();

        if (kompozicije.isEmpty()) {
            System.out.print("Kompozicija s oznakom " + compositionDesignation + " nije pronađena.\n");
            System.out.println("===================================================================");
            return;
        }

        if (!validateComposition(kompozicije)) {
            System.out.print("Kompozicija mora sadržavati barem jedno prijevozno sredstvo s ulogom putničkog vlaka (P) ");
            System.out.println("===================================================================");
            return;
        }

        System.out.printf("%-15s | %-30s | %-15s%n", "Oznaka", "Oznaka prijevoznog sredstva", "Uloga");
        System.out.println("-------------------------------------------------------------------");

        for (Kompozicija kompozicija : kompozicije) {
            System.out.printf("%-15s | %-30s | %-15s%n",
                    kompozicija.getOznaka(),
                    kompozicija.getOznakaPrijevoznogSredstva(),
                    kompozicija.getUloga());
        }

        System.out.println("===================================================================");
    }


    private static boolean validateComposition(List<Kompozicija> vehicles) {
        boolean foundFirstP = false;
        boolean isValid = true;
        int pCount = 0;

        for (Kompozicija vehicle : vehicles) {
            if (vehicle.getUloga().equals("P")) {
                if (!foundFirstP) {
                    foundFirstP = true;
                }
                pCount++;
            } else if (foundFirstP && vehicle.getUloga().equals("V")) {
                continue;
            } else {
                isValid = false;
                break;
            }
        }
        return isValid && pCount > 0;
    }
}
