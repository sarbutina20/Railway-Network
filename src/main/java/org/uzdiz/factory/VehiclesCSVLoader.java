package org.uzdiz.factory;

import org.uzdiz.builder.Vozilo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VehiclesCSVLoader extends CSVLoader<Vozilo> {

    private int expectedFieldCount;
    private Validator[] validators = {
            new Validator(0, "^[A-Z0-9-]+$", "Neispravan format oznake"),
            new Validator(1, "^.+$", "Neispravan format opisa"),
            new Validator(2, "^.+$", "Neispravan format proizvođača"),
            new Validator(3, "^(19|20)\\d{2}$", "Neispravan format godine"),
            new Validator(4, "^.+$", "Neispravan format namjene"),
            new Validator(5, "^[A-ZČĆŽŠĐ]+$", "Neispravan format vrste prijevoza"),
            new Validator(6, "^[A-Z]$", "Neispravan format vrste pogona"),
            new Validator(7, "^(\\d+(\\.\\d+)?|\\.\\d+)$", "Neispravan format maksimalne brzine"),
            new Validator(8, "^-?\\d+(,\\d+)?$", "Neispravan format maksimalne snage"),
            new Validator(9, "^\\d+$", "Neispravan format broja sjedećih mjesta"),
            new Validator(10, "^\\d+$", "Neispravan format broja stajaćih mjesta"),
            new Validator(11, "^\\d+$", "Neispravan format broja bicikala"),
            new Validator(12, "^\\d+$", "Neispravan format broja kreveta"),
            new Validator(13, "^\\d+$", "Neispravan format broja automobila"),
            new Validator(14, "^\\d+(,\\d+)?$", "Neispravan format nosivosti"),
            new Validator(15, "^\\d+(,\\d+)?$", "Neispravan format površine"),
            new Validator(16, "^(\\d+(\\.\\d+)?|\\.\\d+)$", "Neispravan format zapremine"),
            new Validator(17, "^[IK]$", "Neispravan format statusa")
    };

    public VehiclesCSVLoader(String filePath) {
        super(filePath);
    }

    @Override
    public List<Vozilo> loadCSV() {
        List<Vozilo> vozila = new ArrayList<>();
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

                if (!validateFields(fields, lineNumber)) {
                    continue;
                }

                try {
                    Vozilo vozilo = new Vozilo.Builder()
                            .oznaka(fields[0])
                            .opis(fields[1])
                            .proizvodjac(fields[2])
                            .godina(Integer.parseInt(fields[3]))
                            .namjena(fields[4])
                            .vrstaPrijevoza(fields[5])
                            .vrstaPogona(fields[6])
                            .maksBrzina(Double.parseDouble(fields[7].replace(',', '.')))
                            .maksSnaga(Double.parseDouble(fields[8].replace(',', '.')))
                            .brojSjedecihMjesta(Integer.parseInt(fields[9]))
                            .brojStajecihMjesta(Integer.parseInt(fields[10]))
                            .brojBicikala(Integer.parseInt(fields[11]))
                            .brojKreveta(Integer.parseInt(fields[12]))
                            .brojAutomobila(Integer.parseInt(fields[13]))
                            .nosivost(Double.parseDouble(fields[14].replace(',', '.')))
                            .povrsina(Double.parseDouble(fields[15].replace(',', '.')))
                            .zapremina(Double.parseDouble(fields[16].replace(',', '.')))
                            .status(fields[17])
                            .build();

                    vozila.add(vozilo);
                } catch (NumberFormatException e) {
                    logError(lineNumber, "Pogreška u formatu podataka:", line);
                } catch (Exception e) {
                    logError(lineNumber, e.getMessage(), line);
                }
            }
        } catch (IOException e) {
            System.err.println("Pogreška pri čitanju datoteke " + putanjaDatoteke + ": " + e.getMessage());
        }
        return vozila;
    }

    private static void logError(int lineNumber, String message, String line) {
        povecajBrojUkupnihGreski();
        System.err.println("Pogreška #" + dohvatiBrojUkupnihGreski() + " prilikom čitanja datoteke vozila na liniji " + lineNumber + ": " + message + " - " + line);
    }

    public boolean validateFields(String[] fields, int lineNumber) {
        for (Validator validator : validators) {
            if (!validator.validate(fields)) {
                logError(lineNumber, validator.getDescription(), fields[validator.getIndex()]);
                return false;
            }
        }
        return true;
    }

}
