package org.uzdiz.chain_of_responsibility;

import org.uzdiz.observer.Korisnik;
import org.uzdiz.singleton.HrvatskeZeljeznice;

public class DKHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public DKHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("DK")) {
            if (commandParts.length < 3 || commandParts.length > 4) {
                System.out.println("Upotreba: DK <ime> <prezime>");
                return;
            }
            String fullName = commandParts.length == 3 ? commandParts[1] + " " + commandParts[2] : commandParts[1] + " " + commandParts[2] + " " + commandParts[3];
            handleDK(fullName);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleDK(String fullName) {
        if (Korisnik.provjeriPostojanjeKorisnika(fullName)) {
            System.out.println("Korisnik " + fullName + " već postoji.");
            return;
        }
        Korisnik.kreirajKorisnika(fullName);
        System.out.println("Dodan korisnik: " + fullName);
    }



}