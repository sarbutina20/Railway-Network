package org.uzdiz.chain_of_responsibility;

import org.uzdiz.memento.IspisKarti;
import org.uzdiz.memento.KartaMemento;
import org.uzdiz.memento.PovijestKarata;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.util.List;

public class IKKPVHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public IKKPVHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IKKPV")) {
            if (commandParts.length > 2) {
                System.out.println("Upotreba: IKKPV [broj karte]");
                return;
            }

            PovijestKarata povijestKarata = hrvatskeZeljeznice.getPovijestKarata();
            IspisKarti ispisKarti = new IspisKarti();

            if (commandParts.length == 1) {
                List<KartaMemento> sveKarte = povijestKarata.dohvatiSveKarte();
                if (sveKarte.isEmpty()) {
                    System.out.println("Nema spremljenih karata.");
                } else {
                    for (int i = 0; i < sveKarte.size(); i++) {
                        KartaMemento karta = sveKarte.get(i);
                        System.out.println((i + 1) + ". " + ispisKarti.formatirajPodatkeOKarti(karta));
                    }
                }
            } else {
                Integer brojKarte;
                try {
                    brojKarte = Integer.parseInt(commandParts[1]);
                } catch (NumberFormatException e) {
                    System.out.println("Broj karte mora biti cijeli broj!");
                    return;
                }

                if (brojKarte < 1 || brojKarte > povijestKarata.dohvatiSveKarte().size()) {
                    System.out.println("Ne postoji karta s tim brojem");
                    return;
                }

                KartaMemento karta = povijestKarata.dohvatiKartu(brojKarte - 1);
                System.out.println(ispisKarti.formatirajPodatkeOKarti(karta));
            }
        } else {
            super.handle(command, commandParts);
        }
    }



}
