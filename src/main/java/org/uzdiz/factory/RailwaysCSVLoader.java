package org.uzdiz.factory;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RailwaysCSVLoader extends CSVLoader<ZeljeznickaPruga> {

    List<ZeljeznickaPruga> pruge = new ArrayList<>();

    public RailwaysCSVLoader(String filePath) {
        super(filePath);
    }

    @Override
    public List<ZeljeznickaPruga> loadCSV() {
        List<Stanica> stanice = new ArrayList<>();

        int expectedFieldCount;
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

                if (!validateStanicaData(fields, lineNumber, line)) {
                    continue;
                }

                try {
                    Stanica stanica = new Stanica.Builder()
                            .naziv(fields[0])
                            .oznakaPruge(fields[1])
                            .vrstaStanice(fields[2])
                            .statusStanice(fields[3])
                            .putniciUlIz(fields[4].equals("DA"))
                            .robaUtIst(fields[5].equals("DA"))
                            .kategorijaPruge(fields[6])
                            .brojPerona(Integer.parseInt(fields[7]))
                            .vrstaPruge(fields[8])
                            .brojKolosjeka(Integer.parseInt(fields[9]))
                            .doPoOsovini(Double.parseDouble(fields[10].replace(',', '.')))
                            .doPoDuznomM(Double.parseDouble(fields[11].replace(',', '.')))
                            .statusPruge(fields[12])
                            .duzina(Integer.parseInt(fields[13]))
                            .vrijemeNormalniVlak(fields.length > 14 ? parseOptionalInteger(fields[14]) : null)
                            .vrijemeUbrzaniVlak(fields.length > 15 ? parseOptionalInteger(fields[15]) : null)
                            .vrijemeBrziVlak(fields.length > 16 ? parseOptionalInteger(fields[16]) : null)
                            .build();

                    stanice.add(stanica);

                    checkExistingRailway(fields[1], stanica, Integer.parseInt(fields[13]));

                } catch (NumberFormatException e) {
                    logError(lineNumber, "Pogreška u formatu podataka: ", line);
                } catch (Exception e) {
                    logError(lineNumber, e.getMessage(), line);
                }
            }
        } catch (IOException e) {
            System.err.println("Pogreška pri čitanju datoteke " + putanjaDatoteke + ": " + e.getMessage());
        }
        checkIfAllStationsAreValid(pruge);

        return pruge;
    }

    private void checkIfAllStationsAreValid(List<ZeljeznickaPruga> pruge) {
        for(ZeljeznickaPruga pruga : pruge) {
            if(pruga.getStations().size() == 1) {
                pruge.remove(pruga);
                logError(0, "Pruga " + pruga.getOznakaPruge() + " ima samo jednu stanicu", "");
            }
            if(pruga.getStations().get(0).equals(pruga.getStations().get(pruga.getStations().size() - 1))) {
                pruge.remove(pruga);
                logError(0, "Pruga " + pruga.getOznakaPruge() + " ima istu početnu i završnu stanicu", "");
            }
        }
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void checkExistingRailway(String oznakaPruge, Stanica stanica, int duzina) {
        ZeljeznickaPruga existingRailway = pruge.stream()
                .filter(r -> r.getOznakaPruge().equals(oznakaPruge))
                .findFirst()
                .orElse(null);

        if (existingRailway != null) {
            existingRailway.getStations().add(stanica);
            existingRailway.getDistances().add(duzina);
            existingRailway.incrementTotalDistance(duzina);


            if (stanica.getVrijemeUbrzaniVlak() != null) {
                existingRailway.getUbrzaneStanice().add(stanica);
            }

            if (stanica.getVrijemeBrziVlak() != null) {
                existingRailway.getBrzeStanice().add(stanica);
            }

        } else {
            ZeljeznickaPruga newRailway = new ZeljeznickaPruga.Builder(oznakaPruge)
                    .addStation(stanica)
                    .addDistance(duzina)
                    .build();


            if (stanica.getVrijemeUbrzaniVlak() != null) {
                newRailway.getUbrzaneStanice().add(stanica);
            }

            if (stanica.getVrijemeBrziVlak() != null) {
                newRailway.getBrzeStanice().add(stanica);
            }

            pruge.add(newRailway);
        }
    }


    private static boolean validateStanicaData(String[] values, int lineNumber, String line) {

        List<Validator> validators = Arrays.asList(
                new Validator(0, "[A-Za-zčćžšđČĆŽŠĐ \\-]+", "Naziv stanice"),
                new Validator(1, "[A-Z][0-9]{3}", "Oznaka pruge"),
                new Validator(2, "kol\\.|staj\\.", "Vrsta stanice"),
                new Validator(3, "[OA]", "Status stanice"),
                new Validator(4, "DA|NE", "Putnici ul/iz"),
                new Validator(5, "DA|NE", "Roba ut/ist"),
                new Validator(6, "[A-Z]", "Kategorija pruge"),
                new Validator(7, "\\d+", "Broj perona"),
                new Validator(8, "[EK]", "Vrsta pruge"),
                new Validator(9, "\\d+", "Broj kolosjeka"),
                new Validator(10, "\\d{1,2}(,\\d{1,2})?", "DO po osovini"),
                new Validator(11, "\\d{1,2}(,\\d{1,2})?", "DO po dužnom m"),
                new Validator(12, "[I|II|III]", "Status pruge"),
                new Validator(13, "\\d+", "Dužina")
        );

        List<Validator> optionalValidators = Arrays.asList(
                new Validator(14, "^\\d*$", "Vrijeme normalni vlak"),
                new Validator(15, "^\\d*$", "Vrijeme ubrzani vlak"),
                new Validator(16, "^\\d*$", "Vrijeme brzi vlak")
        );

        if (values.length < 14 || values.length > 17) {
            logError(lineNumber, "Neispravan broj stupaca: Očekivano je 14-17, pronađeno " + values.length, line);
            return false;
        }

        for (Validator validator : validators) {
            if (validator.validate(values)) continue;
            logError(lineNumber, "Neispravan format za " + validator.getDescription() + ": " + values[validator.getIndex()], line);
            return false;
        }

        for (int i = 14; i < Math.min(values.length, 17); i++) {
            Validator optValidator = optionalValidators.get(i - 14);
            if (!optValidator.validate(values)) {
                logError(lineNumber, "Neispravan format za " + optValidator.getDescription() + ": " + values[i], line);
                return false;
            }
        }

        return true;
    }



    private static void logError(int lineNumber, String message, String line) {
        povecajBrojUkupnihGreski();
        System.err.println("Pogreška #" + dohvatiBrojUkupnihGreski() + " prilikom čitanja datoteke stanica na liniji " + lineNumber + ": " + message + " - " + line);
    }
}
