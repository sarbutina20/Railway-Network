package org.uzdiz.factory;

import org.uzdiz.builder.Kompozicija;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositionsCSVLoader extends CSVLoader<Kompozicija> {
    private int expectedFieldCount;
    private final Validator[] validators = {
            new Validator(0, "^\\d{1,20}$", "Neispravan format oznake"),
            new Validator(1, "^[A-Z0-9-]{2,30}$", "Neispravan format oznake prijevoznog sredstva"),
            new Validator(2, "^.{1}$", "Neispravan format uloge")
    };

    public CompositionsCSVLoader(String filePath) {
        super(filePath);
    }

    @Override
    public List<Kompozicija> loadCSV() {
        List<Kompozicija> kompozicije = new ArrayList<>();
        Map<String, List<Kompozicija>> compositionsMap = new HashMap<>();
        String line;
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(putanjaDatoteke))) {
            String headerLine = br.readLine();
            if (headerLine != null) {
                expectedFieldCount = headerLine.split(";").length;
            }

            while ((line = br.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty() || line.startsWith("#") || line.trim().matches("^;*[,]*$")) {
                    continue;
                }

                String[] fields = line.split(";");

                if (fields.length < expectedFieldCount) {
                    logError(lineNumber, "Nisu ispunjene vrijednosti za sve atribute", line);
                    continue;
                }

                if (!validateFields(fields)) {
                    continue;
                }

                try {
                    Kompozicija kompozicija = new Kompozicija(
                            fields[0],
                            fields[1],
                            fields[2],
                            lineNumber
                    );

                    compositionsMap.computeIfAbsent(kompozicija.getOznaka(), k -> new ArrayList<>()).add(kompozicija);
                } catch (NumberFormatException e) {
                    logError(lineNumber, "Pogreška u formatu podataka:", line);
                } catch (Exception e) {
                    logError(lineNumber, e.getMessage(), line);
                }
            }
            for (Map.Entry<String, List<Kompozicija>> entry : compositionsMap.entrySet()) {
                List<Kompozicija> tempKompozicije = entry.getValue();
                if (!validateComposition(tempKompozicije)) {
                    Kompozicija lastKompozicija = tempKompozicije.getLast();
                    logError(lastKompozicija.getLinija(), "Kompozicija nije ispravna nakon učitavanja", lastKompozicija.getOznaka());
                } else {
                    kompozicije.addAll(tempKompozicije);
                }
            }

        } catch (IOException e) {
            System.err.println("Pogreška pri čitanju datoteke " + putanjaDatoteke + ": " + e.getMessage());
        }


        return kompozicije;
    }

    private void logError(int lineNumber, String message, String line) {
        povecajBrojUkupnihGreski();
        System.err.println("Pogreška # " + dohvatiBrojUkupnihGreski() + " prilikom čitanja datoteke kompozicija na liniji " + lineNumber + ": " + message + " - " + line );
    }

    private boolean validateFields(String[] fields) {
        for (Validator validator : validators) {
            if (!validator.validate(fields)) {
                return false;
            }
        }
        return true;
    }

    private static boolean validateComposition(List<Kompozicija> vehicles) {
        boolean foundFirstP = false;
        boolean isValid = true;
        int pCount = 0;

        for (Kompozicija vehicle : vehicles) {
            if (vehicle.getUloga().equals("P")) {
                if (!foundFirstP) {
                    foundFirstP = true;
                }
                pCount++;
            } else if (foundFirstP && vehicle.getUloga().equals("V")) {
                continue;
            } else {
                isValid = false;
                break;
            }
        }
        return isValid && pCount > 0;
    }
}
