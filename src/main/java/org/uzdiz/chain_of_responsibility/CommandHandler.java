package org.uzdiz.chain_of_responsibility;

public abstract class CommandHandler {
    protected CommandHandler next;

    public void setNextHandler(CommandHandler next) {
        this.next = next;
    }

    public void handle(String command, String[] args) {
        if (next != null) {
            next.handle(command, args);
        } else {
            System.out.println("Nepoznata naredba: " + command);
        }
    }
}
