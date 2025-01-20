package org.uzdiz.chain_of_responsibility;

import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.observer.UpraviteljPutovanjaVlakom;
import org.uzdiz.singleton.HrvatskeZeljeznice;

public class SVVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public SVVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("SVV")) {
            String joined = String.join(" ", commandParts);

            if (!joined.matches("SVV\\s+[a-zA-Z0-9]+\\s+-\\s+[a-zA-ZčćžšđČĆŽŠĐ]+\\s+-\\s+\\d+")) {
                System.out.println("Upotreba: SVV <oznakaVlaka> - <dan> - <koeficijent>");
                return;
            }

            String[] mainParts = joined.split("\\s+-\\s+");
            handleSVV(mainParts);

        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleSVV(String[] svvParts) {
        try {

            String svvTrainId = svvParts[0].split(" ")[1];
            String svvDay = svvParts[1];


            if (hrvatskeZeljeznice.getOznakeDana().stream().noneMatch(d -> d.getCode().equals(svvDay))) {
                System.out.println("Pogreška: Dan " + svvDay + " nije prepoznat.");
                return;
            }

            int svvCoefficient = Integer.parseInt(svvParts[2]);

            if (svvCoefficient < 1) {
                System.out.println("Pogreška: Koeficijent mora biti veći od 1.");
                return;
            }

            UpraviteljPutovanjaVlakom journeyManager = (UpraviteljPutovanjaVlakom) hrvatskeZeljeznice.getJourneyManager();
            UpraviteljStanicama upraviteljStanicama = hrvatskeZeljeznice.getStationManager();

            journeyManager.simulacijaVoznjeVlakom(svvTrainId, svvDay, svvCoefficient, upraviteljStanicama);

        } catch (Exception e) {
            System.out.println("Pogreška: Neispravan format naredbe ili koeficijent nije broj.");
        }
    }
}
