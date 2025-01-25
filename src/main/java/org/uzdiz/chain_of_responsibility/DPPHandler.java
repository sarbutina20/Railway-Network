package org.uzdiz.chain_of_responsibility;

import org.uzdiz.command.DPPCommand;
import org.uzdiz.command.DiscountCommand;
import org.uzdiz.command.DiscountInvoker;
import org.uzdiz.singleton.HrvatskeZeljeznice;

public class DPPHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public DPPHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("DPP")) {
            String fullCommand = String.join(" ", commandParts);
            String[] segments = fullCommand.split(" - ");
            if (segments.length != 3) {
                System.out.println("Pogrešna sintaksa: DPP polazna - odredisna - popust");
                return;
            }

            String polazna = segments[0].replace("DPP ", "").trim();
            String odredisna = segments[1].trim();
            double popust = Double.parseDouble(segments[2].trim());

            if(popust < 0.0 || popust > 100.0) {
                System.out.println("Popust mora biti između 0 i 100.");
                return;
            }

            boolean postojiPolazna = hrvatskeZeljeznice.getStations().stream()
                    .anyMatch(s -> s.getNaziv().equalsIgnoreCase(polazna));

            boolean postojiOdredisna = hrvatskeZeljeznice.getStations().stream()
                    .anyMatch(s -> s.getNaziv().equalsIgnoreCase(odredisna));

            if (!postojiPolazna || !postojiOdredisna) {
                System.out.println("Pogreška: Jedna ili obje stanice ne postoje u mreži.");
                return;
            }

            DiscountCommand cmd = new DPPCommand(polazna, odredisna, popust);
            hrvatskeZeljeznice.getDiscountInvoker().executeAndStore(cmd);

            if (popust == 0.0) {
                System.out.println("Popust za relaciju " + polazna + "-" + odredisna + " je uklonjen.");
            }

            System.out.println("Popust za relaciju " + polazna + "-" + odredisna + " postavljen na " + popust + "%.");

        } else {
            super.handle(command, commandParts);
        }
    }

}