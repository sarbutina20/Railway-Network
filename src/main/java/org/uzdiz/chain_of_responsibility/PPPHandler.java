package org.uzdiz.chain_of_responsibility;

import org.uzdiz.singleton.HrvatskeZeljeznice;

public class PPPHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public PPPHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("PPP")) {
            if (commandParts.length != 1) {
                System.out.println("Upotreba: PPP");
                return;
            }

            hrvatskeZeljeznice.getDiscountInvoker().undoAll();
            System.out.println("Svi privremeni popusti poništeni.");
        } else {
            super.handle(command, commandParts);
        }
    }
}
