package org.uzdiz;
import org.uzdiz.chain_of_responsibility.ChainCreator;
import org.uzdiz.chain_of_responsibility.CommandHandler;
import org.uzdiz.singleton.HrvatskeZeljeznice;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Map<String, String> putanjeDoDatoteka = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                if (i + 1 < args.length) {
                    putanjeDoDatoteka.put(key, args[++i]);
                }
            }
        }

        String putanjaDatotekeStanica = putanjeDoDatoteka.get("zs");
        String putanjaDatotekeVozila = putanjeDoDatoteka.get("zps");
        String putanjaDatotekeKompozicija = putanjeDoDatoteka.get("zk");
        String putanjaDatotekeVoznogReda = putanjeDoDatoteka.get("zvr");
        String putanjaDatotekeOznakaDana = putanjeDoDatoteka.get("zod");

        if (putanjaDatotekeStanica == null || putanjaDatotekeVozila == null || putanjaDatotekeKompozicija == null || putanjaDatotekeVoznogReda == null || putanjaDatotekeOznakaDana == null) {
            System.err.println("Pogreška: Nedostaju putanje do csv datoteka. Molim vas pridružite vrijednosti --zs, --zps, --zk, --zvr i --zod argumentima.");
            return;
        }

        HrvatskeZeljeznice hrvatskeZeljeznice = HrvatskeZeljeznice.getInstance();
        hrvatskeZeljeznice.initialize(putanjaDatotekeStanica, putanjaDatotekeVozila, putanjaDatotekeKompozicija, putanjaDatotekeOznakaDana, putanjaDatotekeVoznogReda);

        ChainCreator chainCreator = new ChainCreator(hrvatskeZeljeznice);
        CommandHandler upraviteljKomandi = chainCreator.buildCommandHandlerChain();

        Scanner scanner = new Scanner(System.in);
        System.out.println("Unesite naredbu (Q za izlaz):");

        while (true) {
            System.out.print("> ");
            String userInput = scanner.nextLine().trim();
            String[] commandParts = userInput.split(" ");

            if (commandParts.length == 0) {
                continue;
            }

            if (userInput.equalsIgnoreCase("Q")) {
                System.out.println("Exiting...");
                break;
            }

            String command = commandParts[0].toUpperCase();

            upraviteljKomandi.handle(command, commandParts);


        }

    }
}