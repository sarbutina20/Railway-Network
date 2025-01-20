package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class IVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IV")) {
            if (commandParts.length != 1) {
                System.out.println("Upotreba: IV");
                return;
            }
            handleIV();
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIV() {
        System.out.println("=====================================================================================================================================");
        System.out.println("Prikaz vlakova:");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-2s | %-32s | %-32s | %-10s | %-10s | %-10s%n",
                "Oznaka vlaka", "Polazna stanica", "Odredišna stanica", "Vrijeme polaska", "Vrijeme dolaska", "Ukupan broj km");
        System.out.println("=====================================================================================================================================");

        List<KomponentaVoznogReda> trains = hrvatskeZeljeznice.getVlakovi();
        trains.sort(Comparator.comparing(KomponentaVoznogReda::dohvatiVrijemePolaska));

        for (KomponentaVoznogReda train : trains) {
            LocalTime arrivalTime = train.izracunajDolazakZadnjeEtape();
            if (arrivalTime == null || !train.validirajEtapeVlaka()) {
                System.out.printf("%-15s | %-20s%n", train.dohvatiOznaku(), "Neispravan vlak");
                continue;
            }


            Stanica polaziste = train.dohvatiPolaznuStanicu();
            Stanica odrediste = train.dohvatiOdredisnuStanicu();


            double ukupnaUdaljenost = train.izracunajUkupnuUdaljenost(hrvatskeZeljeznice.getStationManager());

            System.out.printf("%-2s | %-32s | %-32s | %-10s | %-10s | %-10.2f km%n",
                    train.dohvatiOznaku(),
                    polaziste != null ? polaziste.getNaziv() : "Nepoznato",
                    odrediste != null ? odrediste.getNaziv() : "Nepoznato",
                    train.dohvatiVrijemePolaska(),
                    arrivalTime,
                    ukupnaUdaljenost);
        }
        System.out.print("=====================================================================================================================================");
    }
}