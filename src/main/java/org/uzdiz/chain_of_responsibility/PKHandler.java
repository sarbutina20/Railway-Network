package org.uzdiz.chain_of_responsibility;

import org.uzdiz.observer.Korisnik;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.util.List;

public class PKHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public PKHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("PK")) {
            if (commandParts.length != 1) {
                System.out.println("Upotreba: PK");
                return;
            }
            List<String> users = Korisnik.vratiListuKorisnika();
            if (users.isEmpty()) {
                System.out.println("Nema dodanih korisnika.");
            } else {
                System.out.println("=======================================================");
                System.out.println("Lista dodanih korisnika:");
                users.forEach(System.out::println);
                System.out.println("=======================================================");
            }
        } else {
            super.handle(command, commandParts);
        }
    }
}
