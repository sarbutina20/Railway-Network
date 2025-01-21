package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.state.RelacijaPruge;

import java.util.List;

public class PSP2SHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public PSP2SHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("PSP2S")) {
            String fullCommand = String.join(" ", commandParts);
            String normalized = fullCommand.replaceAll("[–—]", "-");

            String[] segments = normalized.split("\\s-\\s");
            if (segments.length != 4) {
                System.out.println("Pogrešan broj parametara. Ispravna sintaksa: PSP2S oznaka - polaznaStanica - odredišnaStanica - status");
                return;
            }

            String oznaka = segments[0].replace("PSP2S ", "").trim();
            String pocetakRelacije = segments[1].trim();
            String krajRelacije = segments[2].trim();
            String status = segments[3].trim();

            handlePSP2S(oznaka, pocetakRelacije, krajRelacije, status);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handlePSP2S(String oznaka, String pocetakRelacije, String krajRelacije, String status) {
        ZeljeznickaPruga pruga = hrvatskeZeljeznice.getRailwayByOznaka(oznaka);
        if (pruga == null) {
            System.out.println("Pruga s oznakom " + oznaka + " ne postoji.");
            return;
        }

        Stanica polazna = pruga.dohvatiStanicu(pocetakRelacije);
        Stanica odredisna = pruga.dohvatiStanicu(krajRelacije);

        if (polazna == null || odredisna == null) {
            System.out.println("Stanice nisu pronađene na pruzi " + oznaka);
            return;
        }

        List<RelacijaPruge> relacijeZaPromjenu
                = pruga.dohvatiRelacijeIzmedu(polazna, odredisna);

        if(relacijeZaPromjenu.isEmpty()) {
            System.out.println("Nema relacija između stanica " + pocetakRelacije + " i " + krajRelacije);
            return;
        }

        for (RelacijaPruge rel : relacijeZaPromjenu) {
            switch (status) {
                case "K" -> rel.postaviKvar();
                case "Z" -> rel.postaviZatvorena();
                case "I" -> rel.postaviIspravnu();
                case "T" -> rel.postaviTestiranje();
                default  -> System.out.println("Nepoznat status: " + status);
            }
        }

    }


}
