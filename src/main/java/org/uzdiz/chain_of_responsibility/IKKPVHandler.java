package org.uzdiz.chain_of_responsibility;

import org.uzdiz.memento.IspisKarti;
import org.uzdiz.memento.KartaMemento;
import org.uzdiz.memento.PovijestKarata;
import org.uzdiz.observer.Korisnik;
import org.uzdiz.singleton.HrvatskeZeljeznice;

public class IKKPVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IKKPVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IKKPV")) {
            if (commandParts.length != 2) {
                System.out.println("Upotreba: IKKPV <broj karte>");
                return;
            }
            Integer brojKarte = Integer.parseInt(commandParts[1]);

            PovijestKarata povijestKarata = hrvatskeZeljeznice.getPovijestKarata();

            if(brojKarte < 1 || brojKarte > povijestKarata.dohvatiSveKarte().size()) {
                System.out.println("Ne postoji karta s tim brojem");
                return;
            }

            KartaMemento karta = povijestKarata.dohvatiKartu(brojKarte-1);
            IspisKarti ispisKarti = new IspisKarti();

            System.out.println(ispisKarti.formatirajPodatkeOKarti(karta));

        } else {
            super.handle(command, commandParts);
        }
    }



}
