package org.uzdiz.chain_of_responsibility;

import org.uzdiz.composite.OznakeDana;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.EtapaVlaka;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.LocalTime;
import java.util.Collections;
import java.util.Set;

public class IEVDHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IEVDHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IEVD")) {
            if (commandParts.length != 2) {
                System.out.println("Upotreba: IEVD <dani u tjednu>");
                return;
            }

            String dayCode = commandParts[1];

            if (!dayCode.matches("^[PoUČSrPeSuN]+$")) {
                System.out.println("Greška: Nevažeći format dana.");
                return;
            }

            Set<OznakeDana> specifiedDays = OznakeDana.fromCompositeCode(dayCode);

            if (specifiedDays.isEmpty()) {
                System.out.println("Greška: Nisu prepoznati dani.");
                return;
            }

            handleIEVD(specifiedDays, dayCode);

        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIEVD(Set<OznakeDana> specifiedDays, String dayCode) {
        System.out.println("\n==========================================================================================================");
        System.out.println("Vlakovi i njihove etape koje voze na dane: " + dayCode);
        System.out.println("------------------------------------------------------------------------------------------------");
        System.out.printf("%-15s | %-20s | %-20s | %-10s | %-10s | %-12s%n",
                "Oznaka pruge", "Polazna stanica", "Odredišna stanica",
                "Vrijeme polaska", "Vrijeme dolaska", "Dani u tjednu");
        System.out.println("------------------------------------------------------------------------------------------------");

        for (KomponentaVoznogReda train : hrvatskeZeljeznice.getVlakovi()) {
            boolean allStagesMatch = train.dohvatiEtape().stream()
                    .allMatch(stage -> stage instanceof EtapaVlaka &&
                            !Collections.disjoint(((EtapaVlaka) stage).dohvatiOznakuDana(), specifiedDays));

            if (allStagesMatch) {
                System.out.println("Oznaka vlaka: " + train.dohvatiOznaku());
                System.out.println("----------------------------------------------------------------------------------------------------------");
                System.out.printf("%-15s | %-20s | %-20s | %-10s | %-10s | %-12s%n",
                        "Oznaka pruge", "Polazna stanica", "Odredišna stanica", "Polazak", "Dolazak", "Dani u tjednu");
                System.out.println("----------------------------------------------------------------------------------------------------------");

                for (KomponentaVoznogReda stage : train.dohvatiEtape()) {
                    if (stage instanceof EtapaVlaka etapaVlaka) {

                        LocalTime arrivalTime = etapaVlaka.dohvatiVrijemePolaska()
                                .plusHours(etapaVlaka.getTrajanjeVoznje().getHour())
                                .plusMinutes(etapaVlaka.getTrajanjeVoznje().getMinute());

                        System.out.printf("%-15s | %-20s | %-20s | %-10s | %-10s | %-12s%n",
                                etapaVlaka.dohvatiOznakuPruge(),
                                etapaVlaka.dohvatiNazivPolazneStanice(),
                                etapaVlaka.dohvatiNazivOdredisneStanice(),
                                etapaVlaka.getVrijemePolaska(),
                                arrivalTime,
                                etapaVlaka.getOznakaDanaAsString());
                    }
                }

                System.out.println("==========================================================================================================\n");
            }
        }
    }

}