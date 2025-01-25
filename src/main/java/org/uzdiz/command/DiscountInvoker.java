package org.uzdiz.command;

import java.util.Stack;

public class DiscountInvoker {
    Stack<DiscountCommand> commands = new Stack<>();

    public void executeAndStore(DiscountCommand cmd) {
        cmd.execute();
        commands.push(cmd);
    }

    public void undoAll() {
        while (!commands.isEmpty()) {
            commands.pop().undo();
        }
    }
}
