package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.ZeljeznickaPruga;

import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.state.RelacijaPruge;


public class IRPSHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IRPSHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IRPS")) {
            if (commandParts.length < 2) {
                System.out.println("Nedovoljno argumenata! Sintaksa: IRPS status [oznakaPruge]");
                return;
            }
            String trazeniStatus = commandParts[1];
            String trazenaPruga = (commandParts.length > 2) ? commandParts[2] : null;

            if (!trazeniStatus.matches("(?i)[IKTZ]")) {
                System.out.println("Neispravan status! Dopuštene vrijednosti: I, K, T, Z.");
                return;
            }

            handleIRPS(trazeniStatus, trazenaPruga);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleIRPS(String trazeniStatus, String trazenaPruga) {

        System.out.printf("%-8s | %-25s | %-25s | %-5s%n", "PRUGA", "POLAZNA STANICA", "ODREDIŠNA STANICA", "STATUS");
        System.out.println("--------+---------------------------+---------------------------+-------");

        boolean ispisano = false;
        for (ZeljeznickaPruga pruga : hrvatskeZeljeznice.getPruge()) {
            if (trazenaPruga != null && !pruga.getOznakaPruge().equalsIgnoreCase(trazenaPruga)) {
                continue;
            }
            for (RelacijaPruge rel : pruga.getRelacije()) {
                if (rel.getStatusRelacije().equalsIgnoreCase(trazeniStatus)) {
                    System.out.printf("%-8s | %-25s | %-25s | %-5s%n",
                            pruga.getOznakaPruge(),
                            rel.getPolaznaStanica().getNaziv(),
                            rel.getOdredisnaStanica().getNaziv(),
                            rel.getStatusRelacije());
                    ispisano = true;
                }
            }
        }

        if (!ispisano) {
            System.out.println("Nema relacija sa statusom '" + trazeniStatus
                    + (trazenaPruga != null ? "' na pruzi " + trazenaPruga : "' na niti jednoj pruzi."));
        }
    }


}