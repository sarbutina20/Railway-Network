package org.uzdiz.composite;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.state.RelacijaPruge;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

public class Vlak implements KomponentaVoznogReda {

    private final String oznakaVlaka;
    private final List<KomponentaVoznogReda> etape = new ArrayList<>();
    private final VrstaVlaka vrstaVlaka;

    public Vlak(String oznakaVlaka, VrstaVlaka vrstaVlaka) {
        if (oznakaVlaka == null || oznakaVlaka.trim().isEmpty()) {
            throw new IllegalArgumentException("Vlak identifier is required");
        }
        this.oznakaVlaka = oznakaVlaka;
        this.vrstaVlaka = vrstaVlaka;
    }

    @Override
    public Integer dohvatiBrojLinijeCSV() {
        return 0;
    }

    @Override
    public String dohvatiLinijuCSV() {
        return "";
    }

    public VrstaVlaka dohvatiVrstaVlaka() {
        return vrstaVlaka;
    }


    @Override
    public String dohvatiOznaku() {
        return oznakaVlaka;
    }

    @Override
    public List<Stanica> dohvatiSveStanice() {
        return etape.stream()
                .flatMap(stage -> stage.dohvatiSveStanice().stream())
                .collect(Collectors.toList());
    }

    @Override
    public LocalTime dohvatiVrijemePolaska() {
        return etape.isEmpty() ? null : etape.getFirst().dohvatiVrijemePolaska();
    }

    @Override
    public String dohvatiSmjer() {
        return null;
    }

    @Override
    public String dohvatiOznakuPruge() {
        return etape.isEmpty() ? null : etape.getFirst().dohvatiOznakuPruge();
    }

    @Override
    public List<KomponentaVoznogReda> getChildren() {
        return etape;
    }

    @Override
    public Set<OznakeDana> dohvatiOznakuDana() {
        return etape.stream()
                .filter(stage -> stage instanceof EtapaVlaka)
                .map(stage -> stage.dohvatiOznakuDana())
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    @Override
    public void dodajKomponentu(KomponentaVoznogReda component) {
        etape.add(component);
        etape.sort((s1, s2) -> s1.dohvatiVrijemePolaska().compareTo(s2.dohvatiVrijemePolaska()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vlak vlak = (Vlak) o;
        return Objects.equals(oznakaVlaka, vlak.oznakaVlaka) &&
                Objects.equals(etape, vlak.etape);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oznakaVlaka, etape);
    }

    public List<KomponentaVoznogReda> dohvatiEtape() {
        return new ArrayList<>(etape);
    }

    @Override
    public Stanica dohvatiPolaznuStanicu() {
        return etape.isEmpty() ? null : etape.getFirst().dohvatiPolaznuStanicu();
    }

    @Override
    public Stanica dohvatiOdredisnuStanicu() {
        return etape.isEmpty() ? null : etape.getLast().dohvatiOdredisnuStanicu();
    }

    @Override
    public String dohvatiNazivPolazneStanice() {
        return etape.isEmpty() ? "Nepoznato" : etape.getFirst().dohvatiNazivPolazneStanice();
    }

    @Override
    public LocalTime izracunajVrijemePolaska(String nazivStanice) {
        if (etape.isEmpty()) return null;

        LocalTime departureTime = null;
        String trenutnaOznakaPruge = null;
        Stanica prethodnaStanica = null;

        for (KomponentaVoznogReda komponenta : etape) {
            if (!(komponenta instanceof EtapaVlaka etapa)) continue;


            if (trenutnaOznakaPruge == null || !trenutnaOznakaPruge.equalsIgnoreCase(etapa.dohvatiOznakuPruge())) {
                departureTime = etapa.dohvatiVrijemePolaska();
                trenutnaOznakaPruge = etapa.dohvatiOznakuPruge();
            }

            ZeljeznickaPruga pruga = HrvatskeZeljeznice.getInstance().getRailwayByOznaka(etapa.dohvatiOznakuPruge());
            if (pruga == null) continue;

            List<Stanica> stanice = etapa.dohvatiSveStanice();
            for (Stanica trenutnaStanica : stanice) {
                if (prethodnaStanica != null) {
                    if (etapa.dohvatiSmjer().equalsIgnoreCase("N")) {
                        Integer vrijemeVoznje = pruga.getTimeForType(trenutnaStanica, vrstaVlaka);
                        if (vrijemeVoznje != null && departureTime != null) {
                            departureTime = departureTime.plusMinutes(vrijemeVoznje);
                        }
                    } else {
                        Integer vrijemeVoznje = pruga.getTimeForType(prethodnaStanica, vrstaVlaka);
                        if (vrijemeVoznje != null && departureTime != null) {
                            departureTime = departureTime.plusMinutes(vrijemeVoznje);
                        }
                    }
                }

                if (trenutnaStanica.getNaziv().equalsIgnoreCase(nazivStanice)) {
                    return departureTime;
                }

                prethodnaStanica = trenutnaStanica;
            }
        }

        return null;
    }

    @Override
    public LocalTime izracunajVrijemeDolaska(String polaznaStanica, String odredisnaStanica) {
        for (KomponentaVoznogReda etapa : etape) {
            LocalTime departureTime = etapa.dohvatiVrijemePolaska();
            String lastPrintedStation = null;
            Integer travelTime;
            ZeljeznickaPruga pruga = HrvatskeZeljeznice.getInstance().getRailwayByOznaka(etapa.dohvatiOznakuPruge());

            List<Stanica> sveStanice = etapa.dohvatiSveStanice();
            for (int i = 0; i < sveStanice.size(); i++) {
                Stanica currentStation = sveStanice.get(i);

                if (i > 0) {
                    if (currentStation.getNaziv().equalsIgnoreCase(lastPrintedStation) && !currentStation.getNaziv().equalsIgnoreCase(odredisnaStanica)) {
                        continue;
                    }
                    if (etapa.dohvatiSmjer().equalsIgnoreCase("N")) {
                        travelTime = pruga.getTimeForType(currentStation, etapa.dohvatiVrstaVlaka());
                        if (travelTime != null) {
                            departureTime = departureTime.plusMinutes(travelTime);
                        }
                    } else {
                        travelTime = pruga.getTimeForType(sveStanice.get(i - 1), etapa.dohvatiVrstaVlaka());
                        if (travelTime != null) {
                            departureTime = departureTime.plusMinutes(travelTime);
                        }
                    }
                }

                if (currentStation.getNaziv().equalsIgnoreCase(odredisnaStanica) && pruga.getTimeForType(currentStation, etapa.dohvatiVrstaVlaka()) != null) {
                    return departureTime;
                }

                lastPrintedStation = currentStation.getNaziv();
            }
        }
        return null;
    }

    public double izracunajUdaljenostIzmeduStanica(String polazna, String odredisna) {
        double udaljenost = 0;
        boolean pocetak = false;
        for (KomponentaVoznogReda etapa : etape) {

            String lastPrintedStation = null;

            List<Stanica> sveStanice = etapa.dohvatiSveStanice();
            for (int i = 0; i < sveStanice.size(); i++) {
                Stanica currentStation = sveStanice.get(i);

                if (i > 0 && pocetak) {
                    if (currentStation.getNaziv().equalsIgnoreCase(lastPrintedStation) && !currentStation.getNaziv().equalsIgnoreCase(odredisna)) {
                        continue;
                    }
                    if (etapa.dohvatiSmjer().equalsIgnoreCase("N")) {
                        udaljenost += currentStation.getDuzina();
                    } else {
                        udaljenost += sveStanice.get(i - 1).getDuzina();
                    }
                }

                if (currentStation.getNaziv().equalsIgnoreCase(polazna)) {
                    pocetak = true;
                }

                if (currentStation.getNaziv().equalsIgnoreCase(odredisna)) {
                    return udaljenost;
                }

                lastPrintedStation = currentStation.getNaziv();
            }
        }
        return udaljenost;
    }

    @Override
    public String dohvatiNazivOdredisneStanice() {
        return etape.isEmpty() ? "Nepoznato" : etape.getLast().dohvatiNazivOdredisneStanice();
    }


    public LocalTime izracunajDolazakZadnjeEtape() {
        if (etape.isEmpty()) return null;
        EtapaVlaka zadnjaEtapa = (EtapaVlaka) etape.getLast();
        return zadnjaEtapa.dohvatiVrijemePolaska().plusHours(zadnjaEtapa.getTrajanjeVoznje().getHour())
                .plusMinutes(zadnjaEtapa.getTrajanjeVoznje().getMinute());
    }

    @Override
    public double izracunajUkupnuUdaljenost(UpraviteljStanicama upraviteljStanicama) {
        if (etape.isEmpty()) return 0;

        double totalDistance = 0;

        for (int i = 0; i < etape.size(); i++) {
            EtapaVlaka currentStage = (EtapaVlaka) etape.get(i);

            double distance = upraviteljStanicama.calculateDistanceBetweenStations(
                    currentStage.dohvatiPolaznuStanicu().getNaziv(),
                    currentStage.dohvatiOdredisnuStanicu().getNaziv()
            );

            if (distance < 0) {
                throw new IllegalStateException("No valid path between stations " +
                        currentStage.dohvatiPolaznuStanicu().getNaziv() + " and " +
                        currentStage.dohvatiOdredisnuStanicu().getNaziv());
            }

            totalDistance += distance;
        }

        return totalDistance;
    }


    public boolean validirajEtapeVlaka() {
        if (etape.isEmpty()) return false;

        EtapaVlaka firstStage = (EtapaVlaka) etape.getFirst();
        VrstaVlaka vrstaVlakaType = firstStage.dohvatiVrstaVlaka();

        for (int i = 1; i < etape.size(); i++) {

            EtapaVlaka previousStage = (EtapaVlaka) etape.get(i - 1);
            EtapaVlaka currentStage = (EtapaVlaka) etape.get(i);

            if (!currentStage.dohvatiVrstaVlaka().equals(vrstaVlakaType)) {
                return false;
            }
            LocalTime previousArrivalTime = previousStage.dohvatiVrijemePolaska()
                    .plusHours(previousStage.getTrajanjeVoznje().getHour())
                    .plusMinutes(previousStage.getTrajanjeVoznje().getMinute());

            if (previousArrivalTime.isAfter(currentStage.dohvatiVrijemePolaska())) {
                return false;
            }
            LocalTime calculatedEndTime = izracunajVrijemeDolaska(previousStage.dohvatiPolaznuStanicu().getNaziv(), previousStage.dohvatiOdredisnuStanicu().getNaziv());
            if (calculatedEndTime.isAfter(currentStage.dohvatiVrijemePolaska())) {
                return false;
            }


            LocalTime timetableEndTime = previousStage.dohvatiVrijemePolaska()
                    .plusHours(previousStage.getTrajanjeVoznje().getHour())
                    .plusMinutes(previousStage.getTrajanjeVoznje().getMinute());

            if (calculatedEndTime.isAfter(timetableEndTime)) {
                return false;
            }

        }


        return true;
    }

    public boolean provjeraValidnostiRelacija() {
        for (KomponentaVoznogReda stage : dohvatiEtape()) {
            if (stage instanceof EtapaVlaka etapaVlaka) {
                boolean validacija = etapaVlaka.provjeraValidnostiRelacija();
                if (!validacija) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean provjeraIspravnostiRute(Stanica polaznaStanica, Stanica odredisnaStanica) {

        if (polaznaStanica == null || odredisnaStanica == null) {
            System.out.println("Pogreška: Jedna ili obje stanice ne postoje u mreži.");
            return false;
        }

        if (polaznaStanica.getOznakaPruge().equals(odredisnaStanica.getOznakaPruge())) {
            HrvatskeZeljeznice hrvatskeZeljeznice = HrvatskeZeljeznice.getInstance();
            ZeljeznickaPruga pruga = hrvatskeZeljeznice.getRailwayByOznaka(polaznaStanica.getOznakaPruge());
            List<RelacijaPruge> relacije = pruga.dohvatiRelacijeIzmedu(polaznaStanica, odredisnaStanica);

            if (relacije.isEmpty()) {
                System.out.println("Pogreška: Nema definiranih relacija između " +
                        polaznaStanica.getNaziv() + " i " + odredisnaStanica.getNaziv());
                return false;
            }

            for (RelacijaPruge relacija : relacije) {
                if (!relacija.getStatusRelacije().equals("I")) {
                    System.out.println("Nije moguće kupiti kartu jer relacija između " +
                            polaznaStanica.getNaziv() + " i " + odredisnaStanica.getNaziv() +
                            " nije ispravna. Status: " + relacija.getStatusRelacije());
                    return false;
                }
            }
        } else {
            return provjeraValidnostiRelacija();
        }

        return true;
    }


}

