package org.uzdiz.factory;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.composite.*;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TrainScheduleCSVLoader extends CSVLoader<Vlak> {
    private final List<Vlak> vlakovi = new ArrayList<>();
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("H:mm");

    private final KomponentaVoznogReda vozniRed;

    public TrainScheduleCSVLoader(String filePath) {
        super(filePath);
        vozniRed = new VozniRed(vlakovi);
    }

    @Override
    public List<Vlak> loadCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader(putanjaDatoteke))) {
            String headerLine = br.readLine();
            if (headerLine == null) return vlakovi;

            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (mogucePreskocitiLiniju(line)) continue;

                String[] fields = line.split(";");
                if (!validirajPodatkeVlaka(fields, lineNumber, line)) continue;

                processTrainData(fields, lineNumber, line);
            }
        } catch (IOException e) {
            System.err.println("Pogreška pri čitanju datoteke " + putanjaDatoteke + ": " + e.getMessage());
        }

        Iterator<Vlak> iterator = vlakovi.iterator();
        while (iterator.hasNext()) {
            Vlak train = iterator.next();
            if (!train.validirajEtapeVlaka()) {
                for (KomponentaVoznogReda stage : train.dohvatiEtape()) {
                    logError(stage.dohvatiBrojLinijeCSV(), "Nije važeći vlak s oznakom: " + train.dohvatiOznaku(), stage.dohvatiLinijuCSV());
                }
                iterator.remove();
            }
        }

        HrvatskeZeljeznice.getInstance().setVozniRed(vozniRed);
        return vlakovi;

    }

    private void processTrainData(String[] fields, int lineNumber, String line) {
        String oznakaPruge = fields[0];
        String smjer = fields[1];
        String polaznaStanica = fields.length > 2 ? fields[2] : null;
        String odredisnaStanica = fields.length > 3 ? fields[3] : null;
        String oznakaVlaka = fields[4];
        VrstaVlaka vrstaVlaka = fields[5].isEmpty() ? VrstaVlaka.NORMALNI : VrstaVlaka.fromCode(fields[5]);
        LocalTime vrijemePolaska = LocalTime.parse(fields[6], timeFormatter);
        LocalTime trajanjeVoznje = LocalTime.parse(fields[7], timeFormatter);

        Set<OznakeDana> oznakaDana = fields.length > 8 ? OznakeDana.fromCompositeCode(fields[8]) : OznakeDana.fromCompositeCode("PoUSrČPeSuN");

        Vlak vlak = pronadiKreirajVlak(oznakaVlaka, vrstaVlaka);

        try {
            EtapaVlaka etapaVlaka = kreirajEtapuVlaka(oznakaPruge, smjer, polaznaStanica, odredisnaStanica, vrijemePolaska, trajanjeVoznje, vrstaVlaka, oznakaVlaka, oznakaDana, line, lineNumber);

            vlak.dodajKomponentu(etapaVlaka);

        } catch (IllegalArgumentException e) {
            logError(lineNumber, e.getMessage(), line);
        }
    }

    private Vlak pronadiKreirajVlak(String oznakaVlaka, VrstaVlaka vrstaVlaka) {
        return vlakovi.stream().filter(t -> t.dohvatiOznaku().equalsIgnoreCase(oznakaVlaka)).findFirst().orElseGet(() -> {
            Vlak noviVlak = new Vlak(oznakaVlaka, vrstaVlaka);
            vlakovi.add(noviVlak);
            return noviVlak;
        });
    }

    private EtapaVlaka kreirajEtapuVlaka(String oznakaPruge, String smjer, String polaznaStanica, String odredisnaStanica, LocalTime vrijemePolaska, LocalTime trajanjeVoznje, VrstaVlaka vrstaVlaka, String oznakaVlaka, Set<OznakeDana> oznakaDana, String line, int lineNumber) {

        Stanica start = polaznaStanica.trim().isEmpty() ? dajDefaultPolaziste(oznakaPruge, smjer) : traziStanicuPoNazivu(polaznaStanica, oznakaPruge);

        Stanica end = odredisnaStanica.trim().isEmpty() ? dajDefaultOdrediste(oznakaPruge, smjer) : traziZadnjuStanicuPoNazivu(odredisnaStanica, oznakaPruge);


        if (start == null || end == null) {
            throw new IllegalArgumentException("Stanica nije pronađena za prugu: " + oznakaPruge);
        }

        Set<OznakeDana> validOznakaDana = oznakaDana != null && !oznakaDana.isEmpty() ? oznakaDana : EnumSet.allOf(OznakeDana.class);

        return new EtapaVlaka(oznakaPruge, smjer, start, end, vrijemePolaska, trajanjeVoznje, vrstaVlaka, oznakaVlaka, validOznakaDana, line, lineNumber);
    }

    private Stanica traziStanicuPoNazivu(String stationName, String oznakaPruge) {
        return HrvatskeZeljeznice.getInstance().getPruge().stream().filter(railway -> railway.getOznakaPruge().equals(oznakaPruge)).flatMap(railway -> railway.getStations().stream()).filter(station -> station.getNaziv().equals(stationName)).findFirst().orElse(null);
    }

    private Stanica traziZadnjuStanicuPoNazivu(String stationName, String oznakaPruge) {
        return HrvatskeZeljeznice.getInstance().getPruge().stream().filter(railway -> railway.getOznakaPruge().equals(oznakaPruge)).flatMap(railway -> railway.getStations().stream()).filter(station -> station.getNaziv().equals(stationName)).reduce((first, second) -> second).orElse(null);
    }

    private Stanica dajDefaultPolaziste(String railwayCode, String direction) {
        ZeljeznickaPruga railway = traziPruguPoOznaci(railwayCode);
        return direction.equals("N") ? railway.getStations().getFirst() : railway.getStations().getLast();
    }

    private Stanica dajDefaultOdrediste(String railwayCode, String direction) {
        ZeljeznickaPruga railway = traziPruguPoOznaci(railwayCode);
        return direction.equals("N") ? railway.getStations().getLast() : railway.getStations().getFirst();
    }

    private ZeljeznickaPruga traziPruguPoOznaci(String railwayCode) {
        return HrvatskeZeljeznice.getInstance().getPruge().stream().filter(railway -> railway.getOznakaPruge().equals(railwayCode)).findFirst().orElse(null);
    }


    private boolean validirajPodatkeVlaka(String[] values, int lineNumber, String line) {
        List<Validator> validators = Arrays.asList(new Validator(0, "^[A-Z]+[0-9]+$", "Oznaka pruge"), new Validator(1, "[NO]", "Smjer"), new Validator(4, "[a-zA-Z0-9 \\-]+", "Oznaka vlaka"), new Validator(6, "\\d{1,2}:\\d{2}", "Vrijeme polaska"), new Validator(7, "\\d{1,2}:\\d{2}", "Trajanje vožnje"));

        List<Validator> optionalValidators = Arrays.asList(new Validator(2, "^[A-Za-zčćđšžČĆĐŠŽ ]+$", "Polazna stanica"), new Validator(3, "^[A-Za-zčćđšžČĆĐŠŽ ]+$", "Odredišna stanica"), new Validator(5, "[NBU]", "Vrsta vlaka"), new Validator(8, "^[0-9]+$", "Oznaka dana"));

        if (values.length < 7 || values.length > 9) {
            logError(lineNumber, "Neispravni broj stupaca: Očekivano 7-9, nađeno " + values.length, line);
            return false;
        }

        for (Validator validator : validators) {
            if (!validator.validate(values)) {
                logError(lineNumber, "Neispravni format za " + validator.getDescription() + ": " + values[validator.getIndex()], line);
                return false;
            }
        }

        if (!values[5].isEmpty() && VrstaVlaka.fromCode(values[5]) == VrstaVlaka.NORMALNI && !values[5].equalsIgnoreCase("N")) {
            logError(lineNumber, "Neispravna vrsta vlaka: " + values[5], line);
            return false;
        }

        for (Validator validator : optionalValidators) {
            if (validator.getIndex() < values.length) {
                String fieldValue = values[validator.getIndex()];
                if (fieldValue != null && !fieldValue.trim().isEmpty()) {
                    if (!validator.validate(values)) {
                        logError(lineNumber, "Neispravni format za " + validator.getDescription() + ": " + fieldValue, line);
                        return false;
                    }
                }
            }

        }


        return true;
    }

    private boolean mogucePreskocitiLiniju(String line) {
        return line.trim().isEmpty() || line.startsWith("#") || line.trim().matches("^;*[,]*$");
    }

    private static void logError(int lineNumber, String message, String line) {
        povecajBrojUkupnihGreski();
        System.err.println("Pogreška #" + dohvatiBrojUkupnihGreski() + " prilikom čitanja datoteke voznog reda na liniji " + lineNumber + ": " + message + " - " + line);
    }
}
