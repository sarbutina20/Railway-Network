package org.uzdiz.chain_of_responsibility;

import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.singleton.PostavkeCijena;

public class CVPHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public CVPHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("CVP")) {
            if (commandParts.length != 7) {
                System.out.println("CVP cijenaNormalni cijenaUbrzani cijenaBrzi popustSuN popustWebMob uvecanjeVlak");
                return;
            }

            String decimalniBrojRegex = "^\\d+(,\\d+)?$";

            for (int i = 1; i <= 6; i++) {
                if (!commandParts[i].matches(decimalniBrojRegex)) {
                    System.out.println("Pogreška: Parametar " + i + " nije ispravan decimalni broj. Vrijednost: " + commandParts[i]);
                    return;
                }
            }


            try {
                double cijenaNormalni = Double.parseDouble(commandParts[1].replace(',', '.'));
                double cijenaUbrzani = Double.parseDouble(commandParts[2].replace(',', '.'));
                double cijenaBrzi = Double.parseDouble(commandParts[3].replace(',', '.'));
                double popustSuN = Double.parseDouble(commandParts[4].replace(',', '.'));
                double popustWebMob = Double.parseDouble(commandParts[5].replace(',', '.'));
                double uvecanjeVlak = Double.parseDouble(commandParts[6].replace(',', '.'));


                if (cijenaNormalni < 0 || cijenaUbrzani < 0 || cijenaBrzi < 0 || popustSuN < 0 || popustWebMob < 0 || uvecanjeVlak < 0) {
                    System.out.println("Pogreška: Svi parametri moraju biti pozitivni brojevi.");
                    return;
                }

                if(popustSuN > 100 || popustWebMob > 100) {
                    System.out.println("Pogreška: Popusti ne smiju biti veći od 100%.");
                    return;
                }

                PostavkeCijena postavke = PostavkeCijena.getInstance();

                postavke.postaviCijene(cijenaNormalni, cijenaUbrzani, cijenaBrzi);
                postavke.postaviPopuste(popustSuN, popustWebMob);
                postavke.postaviUvecanje(uvecanjeVlak);


                System.out.println("Postavke cijena i popusta su uspješno spremljene.");

            } catch (NumberFormatException e) {
                System.out.println("Pogreška: Neispravan format brojeva u naredbi.");
            }
        } else {
            super.handle(command, commandParts);
        }
    }



}
