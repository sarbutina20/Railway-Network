package org.uzdiz.factory;

import org.uzdiz.composite.OznakeDana;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class DayTagCSVLoader extends CSVLoader<OznakeDana> {

    public DayTagCSVLoader(String filePath) {
        super(filePath);
    }

    private final Validator[] validators = {
            new Validator(0, "^\\d+$", "Neispravan format za oznaka dana (prva kolona mora biti cijeli broj)"),
            new Validator(1, "^[a-zA-ZčćžšđČĆŽŠĐ\\s]*$", "Neispravan format za dani vožnje (druga kolona može biti prazna ili string s dozvoljenim slovima)")
    };

    @Override
    public List<OznakeDana> loadCSV() {
        List<Map.Entry<Integer, String>> rawMappings = new ArrayList<>();
        String line;
        int lineNumber = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(putanjaDatoteke))) {
            String headerLine = br.readLine();
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] fields = line.split(";", -1);
                if (fields.length < 1) {
                    logError(lineNumber, "Nisu ispunjene sve kolone", line);
                    continue;
                }


                String codeField = fields[0].trim();
                String daysField = fields.length > 1 ? fields[1].trim() : "";

                if (!validateFields(new String[]{codeField, daysField}, lineNumber)) {
                    continue;
                }

                try {
                    int code = Integer.parseInt(codeField);
                    String days = daysField.isEmpty() ? "PoUSrČPeSuN" : daysField;
                    rawMappings.add(new AbstractMap.SimpleEntry<>(code, days));
                } catch (Exception e) {
                    logError(lineNumber, "Greška prilikom obrade linije: " + e.getMessage(), line);
                }
            }
        } catch (IOException e) {
            logError(0, "Pogreška pri čitanju datoteke: " + e.getMessage(), "");
        }
        return OznakeDana.loadMappings(rawMappings);


    }

    private boolean validateFields(String[] fields, int lineNumber) {
        for (Validator validator : validators) {
            if (!validator.validate(fields)) {
                logError(lineNumber, validator.getDescription(), fields[validator.getIndex()]);
                return false;
            }
        }
        return true;
    }

    private static void logError(int lineNumber, String message, String line) {
        povecajBrojUkupnihGreski();
        System.err.println("Pogreška #" + dohvatiBrojUkupnihGreski() + " prilikom čitanja datoteke oznaka dana na liniji " + lineNumber + ": " + message + " - " + line);
    }
}
