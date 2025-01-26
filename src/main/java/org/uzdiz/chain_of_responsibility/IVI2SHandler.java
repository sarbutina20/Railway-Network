package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.composite.EtapaVlaka;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.OznakeDana;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class IVI2SHandler extends CommandHandler {
    private final HrvatskeZeljeznice hrvatskeZeljeznice;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    public IVI2SHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("IVI2S")) {
            String fullCommand = String.join(" ", commandParts);
            String[] parts = fullCommand.split("\\s*-\\s*");

            if (parts.length != 6) {
                System.out.println("Pogrešna sintaksa. Koristite: IVI2S polaznaStanica - odredišnaStanica - dan - odVr - doVr - prikaz");
                return;
            }

            String[] firstPart = parts[0].trim().split("\\s+", 2);
            if (firstPart.length != 2) {
                System.out.println("Pogrešna sintaksa. Polazna stanica nedostaje ili nije ispravno navedena.");
                return;
            }

            String polaznaStanica = firstPart[1].trim();
            String odredisnaStanica = parts[1].trim();
            String dani = parts[2].trim();
            String odVrStr = parts[3].trim();
            String doVrStr = parts[4].trim();
            String prikaz = parts[5].trim();

            LocalTime odVr;
            LocalTime doVr;
            try {
                odVr = LocalTime.parse(odVrStr, TIME_FORMATTER);
                doVr = LocalTime.parse(doVrStr, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Nevažeći format vremena. Koristite format H:mm");
                return;
            }

            if (!dani.matches("^[PoUČSrPeSuN]+$")) {
                System.out.println("Greška: Nevažeći format dana.");
                return;
            }

            if (!prikaz.matches("^[SPKV]+$")) {
                System.out.println("Greška: Nevažeći format prikaza. Koristite samo slova S, P, K i V.");
                return;
            }

            Set<OznakeDana> odredeniDani = OznakeDana.fromCompositeCode(dani);

            if (odredeniDani.isEmpty()) {
                System.out.println("Greška: Nisu prepoznati dani.");
                return;
            }

            handleIVI2S(polaznaStanica, odredisnaStanica, odredeniDani, odVr, doVr, prikaz);
        } else {
            super.handle(command, commandParts);
        }
    }


    private void handleIVI2S(String polaznaStanica, String odredisnaStanica, Set<OznakeDana> dan, LocalTime odVr, LocalTime doVr, String prikaz) {
        List<KomponentaVoznogReda> vlakoviPoStanicama = hrvatskeZeljeznice.getVlakovi().stream()
                .filter(vlak -> vlak.dohvatiSveStanice().stream()
                        .anyMatch(stanica -> stanica.getNaziv().equalsIgnoreCase(polaznaStanica)) &&
                        vlak.dohvatiSveStanice().stream()
                                .anyMatch(stanica -> stanica.getNaziv().equalsIgnoreCase(odredisnaStanica)))
                .collect(Collectors.toList());

        List<KomponentaVoznogReda> vlakoviPoDanima = vlakoviPoStanicama.stream()
                .filter(vlak -> {
                    Set<OznakeDana> vlakDani = vlak.dohvatiOznakuDana();
                    return vlakDani != null && !Collections.disjoint(vlakDani, dan);
                })
                .collect(Collectors.toList());



        List<KomponentaVoznogReda> filtriraniVlakovi = vlakoviPoDanima.stream()
                .filter(vlak -> {
                    List<Stanica> sveStanice = vlak.dohvatiSveStanice();
                    Stanica polazna = sveStanice.stream()
                            .filter(stanica -> stanica.getNaziv().equalsIgnoreCase(polaznaStanica))
                            .findFirst().orElse(null);
                    Stanica odredisna = sveStanice.stream()
                            .filter(stanica -> stanica.getNaziv().equalsIgnoreCase(odredisnaStanica))
                            .findFirst().orElse(null);
                    if (polazna == null || odredisna == null) {
                        return false;
                    }

                    LocalTime vrijemePolaskaIzPolazneStanice = vlak.izracunajVrijemePolaska(polazna.getNaziv());
                    LocalTime vrijemePolaskaIzOdredisneStanice = vlak.izracunajVrijemePolaska(odredisna.getNaziv());

                    if(vrijemePolaskaIzOdredisneStanice.isBefore(vrijemePolaskaIzPolazneStanice)) {
                        return false;
                    }

                    LocalTime vrijemePolaska = vlak.dohvatiVrijemePolaska();
                    LocalTime vrijemeDolaska = vlak.izracunajDolazakZadnjeEtape();

                    if(vrijemePolaska.isAfter(doVr)) {
                        return false;
                    }

                    if(vrijemeDolaska.isBefore(odVr) || vrijemePolaska.isBefore(odVr)) {
                        return false;
                    }

                    if(vrijemeDolaska.isAfter(doVr)) {
                        return false;
                    }

                    boolean provjeraIspravnostiRute = vlak.provjeraIspravnostiRute(polazna, odredisna);
                    if(!provjeraIspravnostiRute) {
                        System.out.println("Relacije na pruzi po kojoj vozi vlak " + vlak.dohvatiOznaku() + " nisu ispravne pa nije moguće prikazati vozni red.");
                        return false;
                    }

                    return vrijemePolaska != null && vrijemeDolaska != null;
                })
                .collect(Collectors.toList());


        if(filtriraniVlakovi.isEmpty()) {
            System.out.println("Nema vlakova koji zadovoljavaju uvjete.");
            return;
        }


        filtriraniVlakovi.forEach(vlak -> ispisiRutuVlaka(vlak, polaznaStanica, odredisnaStanica, prikaz));

    }



    private void ispisiRutuVlaka(KomponentaVoznogReda train, String polazna, String odredisna, String prikaz) {

        System.out.println("===================================================================");
        System.out.printf("Vozni red za vlak: %s%n", train.dohvatiOznaku());
        System.out.println("-------------------------------------------------------------------");

        for (char znak : prikaz.toCharArray()) {
            switch (znak) {
                case 'S' -> System.out.printf("%-20s", "Stanica");
                case 'P' -> System.out.printf("%-15s", "Pruga");
                case 'K' -> System.out.printf("%-10s", "Km");
                case 'V' -> System.out.printf("%-10s", "Vrijeme");
                default -> System.out.printf("%-10s", "Nepoznato");
            }
        }
        System.out.println();

        System.out.println("-------------------------------------------------------------------");


        List<StanicaInfo> staniceZaIzracun = spojiSveStaniceZaIzracun(train);
        List<StanicaInfo> staniceZaIspis  = spojiSveStaniceZaIspis(train);

        boolean uRasponu = false;
        double totalDistance = 0;
        LocalTime departureTime = null;
        Stanica zadnjaStanica = null;
        String oznakaPruge = null;
        boolean pronadenaPolaznaStanica = false;


        for (int i = 0; i < staniceZaIzracun.size(); i++) {
            StanicaInfo stanicaInfo = staniceZaIzracun.get(i);
            Stanica trenutnaStanica = stanicaInfo.stanica();
            ZeljeznickaPruga pruga = stanicaInfo.pruga();
            EtapaVlaka etapa = stanicaInfo.etapa();

            if (oznakaPruge == null) {
                oznakaPruge = etapa.dohvatiOznakuPruge();
            }

            if (zadnjaStanica != null) {
                if (!oznakaPruge.equalsIgnoreCase(etapa.dohvatiOznakuPruge())) {
                    departureTime = etapa.dohvatiVrijemePolaska();
                    oznakaPruge = etapa.dohvatiOznakuPruge();
                }

                if (etapa.dohvatiSmjer().equalsIgnoreCase("N")) {
                    totalDistance += trenutnaStanica.getDuzina();
                    Integer travelTime = pruga.getTimeForType(
                            trenutnaStanica,
                            etapa.dohvatiVrstaVlaka()
                    );
                    if (travelTime != null && departureTime != null) {
                        departureTime = departureTime.plusMinutes(travelTime);
                    }
                } else {
                    totalDistance += zadnjaStanica.getDuzina();
                    Integer travelTime = pruga.getTimeForType(
                            zadnjaStanica,
                            etapa.dohvatiVrstaVlaka()
                    );
                    if (travelTime != null && departureTime != null) {
                        departureTime = departureTime.plusMinutes(travelTime);
                    }
                }
            } else {
                departureTime = etapa.dohvatiVrijemePolaska();
                oznakaPruge = etapa.dohvatiOznakuPruge();
            }

            if (!uRasponu && (trenutnaStanica.getNaziv().equalsIgnoreCase(polazna) || trenutnaStanica.getNaziv().equalsIgnoreCase(odredisna))) {
                uRasponu = true;
            }

            if (uRasponu) {
                if(trenutnaStanica.getNaziv().equalsIgnoreCase(polazna)) {
                    pronadenaPolaznaStanica = true;
                }

                boolean isLastStationOfEtapa = trenutnaStanica.getNaziv()
                        .equalsIgnoreCase(etapa.dohvatiOdredisnuStanicu().getNaziv());

                boolean trebaIspisati = staniceZaIspis.stream()
                        .anyMatch(s -> s.stanica().getNaziv().equals(trenutnaStanica.getNaziv())
                                && s.pruga().getOznakaPruge() != null && !s.pruga().getOznakaPruge().isEmpty())
                        || isLastStationOfEtapa;

                if (trebaIspisati) {
                    String polazakZaIspis = isLastStationOfEtapa
                            ? "-"
                            : (departureTime == null ? "-" : departureTime.toString());

                    for (char znak : prikaz.toCharArray()) {
                        switch (znak) {
                            case 'S' -> System.out.printf("%-20s", trenutnaStanica.getNaziv());
                            case 'P' -> System.out.printf("%-15s", pruga.getOznakaPruge());
                            case 'K' -> System.out.printf("%-15.2f", zadnjaStanica == null ? 0 : totalDistance);
                            case 'V' -> System.out.printf("%-10s", polazakZaIspis);
                            default -> System.out.printf("%-10s", "-");
                        }
                    }
                    System.out.println();

                    if (trenutnaStanica.getNaziv().equalsIgnoreCase(odredisna) && pronadenaPolaznaStanica) {
                        break;
                    }
                }
            }

            zadnjaStanica = trenutnaStanica;
        }
        System.out.println("===================================================================");
    }

    private int findEtapaIndex(List<KomponentaVoznogReda> etape, String stationName) {
        for (int i = 0; i < etape.size(); i++) {
            KomponentaVoznogReda stage = etape.get(i);
            if (stage instanceof EtapaVlaka etapaVlaka) {
                List<Stanica> staniceEtape = etapaVlaka.dohvatiSveStanice();
                boolean sadrziStanicu = staniceEtape.stream()
                        .anyMatch(s -> s.getNaziv().equalsIgnoreCase(stationName));
                if (sadrziStanicu) {
                    return i;
                }
            }
        }
        return -1;
    }



    private List<Stanica> filterStationByRange(List<Stanica> stationsAlongPath, EtapaVlaka etapaVlaka) {
        boolean withinRange = false;
        List<Stanica> filteredStations = new ArrayList<>();
        if (etapaVlaka.dohvatiSmjer().equalsIgnoreCase("O")) {
            for (int i = stationsAlongPath.size() - 1; i >= 0; i--) {
                Stanica station = stationsAlongPath.get(i);

                if (station.getNaziv().equalsIgnoreCase(etapaVlaka.dohvatiPolaznuStanicu().getNaziv())) {
                    withinRange = true;
                }

                if (withinRange) {
                    filteredStations.add(station);
                }

                if (station.equals(etapaVlaka.dohvatiOdredisnuStanicu())) {
                    break;
                }
            }
        } else {
            for (Stanica station : stationsAlongPath) {
                if (station.getNaziv().equalsIgnoreCase(etapaVlaka.dohvatiPolaznuStanicu().getNaziv())) {
                    withinRange = true;
                }
                if (withinRange) {
                    filteredStations.add(station);
                }
                if (station.equals(etapaVlaka.dohvatiOdredisnuStanicu())) {
                    break;
                }
            }
        }
        return filteredStations;
    }



    private record StanicaInfo(Stanica stanica, ZeljeznickaPruga pruga, EtapaVlaka etapa) {
    }


    private List<StanicaInfo> spojiSveStaniceZaIspis(KomponentaVoznogReda train) {
        List<StanicaInfo> rezultat = new ArrayList<>();
        for (KomponentaVoznogReda stage : train.dohvatiEtape()) {
            if (stage instanceof EtapaVlaka etapaVlaka) {
                ZeljeznickaPruga pruga = hrvatskeZeljeznice.getRailwayByOznaka(etapaVlaka.dohvatiOznakuPruge());
                List<Stanica> stationsAlongPath = switch (etapaVlaka.dohvatiVrstaVlaka()) {
                    case NORMALNI -> pruga.getStations();
                    case UBRZANI -> pruga.getUbrzaneStanice();
                    case BRZI -> pruga.getBrzeStanice();
                };

                List<Stanica> filtered = filterStationByRange(stationsAlongPath, etapaVlaka);


                for (Stanica stanica : filtered) {
                    rezultat.add(new StanicaInfo(stanica, pruga, etapaVlaka));
                }
            }
        }
        return rezultat;
    }

    private List<StanicaInfo> spojiSveStaniceZaIzracun(KomponentaVoznogReda train) {
        List<StanicaInfo> rezultat = new ArrayList<>();
        for (KomponentaVoznogReda stage : train.dohvatiEtape()) {
            if (stage instanceof EtapaVlaka etapaVlaka) {
                ZeljeznickaPruga pruga = hrvatskeZeljeznice.getRailwayByOznaka(etapaVlaka.dohvatiOznakuPruge());
                List<Stanica> sveStaniceEtape = etapaVlaka.dohvatiSveStanice();
                for (Stanica stanica : sveStaniceEtape) {
                    rezultat.add(new StanicaInfo(stanica, pruga, etapaVlaka));
                }
            }
        }
        return rezultat;
    }

}
