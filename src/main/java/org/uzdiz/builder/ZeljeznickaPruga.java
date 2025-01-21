package org.uzdiz.builder;

import org.uzdiz.composite.VrstaVlaka;
import org.uzdiz.state.RelacijaPruge;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ZeljeznickaPruga {
    private String oznakaPruge;
    private List<Stanica> stations = new ArrayList<>();
    private List<Integer> distances = new ArrayList<>();
    private int totalDistance = 0;

    private int brojKolosjeka;

    private List<Stanica> normalneStanice = new ArrayList<>();
    private List<Stanica> ubrzaneStanice = new ArrayList<>();
    private List<Stanica> brzeStanice = new ArrayList<>();

    private List<RelacijaPruge> relacije;

    private ZeljeznickaPruga(Builder builder) {
        this.oznakaPruge = builder.oznakaPruge;
        this.stations = builder.stations;
        this.distances = builder.distances;
        this.totalDistance = builder.totalDistance;
        this.brojKolosjeka = builder.brojKolosjeka;

    }

    public static class Builder {
        private String oznakaPruge;
        private List<Stanica> stations = new ArrayList<>();
        private List<Integer> distances = new ArrayList<>();
        private int totalDistance = 0;

        private int brojKolosjeka;

        public Builder(String oznakaPruge) {
            this.oznakaPruge = oznakaPruge;
        }

        public Builder addStation(Stanica station) {
            stations.add(station);
            return this;
        }

        public Builder setBrojKolosjeka(int brojKolosjeka) {
            this.brojKolosjeka = brojKolosjeka;
            return this;
        }

        public Builder addDistance(int distance) {
            distances.add(distance);
            totalDistance += distance;
            return this;
        }

        public ZeljeznickaPruga build() {
            return new ZeljeznickaPruga(this);
        }
    }

    public void incrementTotalDistance(double distance) {
        this.totalDistance += distance;
    }

    public String getOznakaPruge() {
        return oznakaPruge;
    }

    public List<Stanica> getStations() {
        return stations;
    }

    public List<Integer> getDistances() {
        return distances;
    }

    public int getTotalDistance() {
        return totalDistance;
    }


    public List<Stanica> getUbrzaneStanice() {
        return ubrzaneStanice;
    }

    public List<Stanica> getBrzeStanice() {
        return brzeStanice;
    }


    private List<Integer> calculateCumulativeTimesForFilteredStations(List<Stanica> filteredStations, boolean forward, VrstaVlaka type) {
        List<Integer> result = new ArrayList<>();
        if (filteredStations.isEmpty()) {
            return result;
        }
        if (forward) {
            int cumulative = 0;
            result.add(cumulative);
            for (int i = 1; i < filteredStations.size(); i++) {
                Stanica current = filteredStations.get(i);
                Integer t = getTimeForType(current, type);
                cumulative += (t != null ? t : 0);
                result.add(cumulative);
            }
        } else {
            int cumulative = 0;
            List<Integer> reversed = new ArrayList<>();
            reversed.add(cumulative);
            for (int i = filteredStations.size() - 2; i >= 0; i--) {
                Stanica next = filteredStations.get(i + 1);
                Integer t = getTimeForType(next, type);
                cumulative += (t != null ? t : 0);
                reversed.add(cumulative);
            }
            Collections.reverse(reversed);
            result = reversed;
        }

        return result;
    }

    public void kreirajRelacije() {
        this.relacije = new ArrayList<>();
        for (int i = 0; i < stations.size() - 1; i++) {
            Stanica polazna = stations.get(i);
            Stanica odredisna = stations.get(i + 1);

            RelacijaPruge r1 = new RelacijaPruge(polazna, odredisna);
            inicijalizirajStateRelacije(r1, polazna.getStatusPruge());
            relacije.add(r1);

            if (brojKolosjeka > 1) {
                RelacijaPruge r2 = new RelacijaPruge(odredisna, polazna);
                inicijalizirajStateRelacije(r2, odredisna.getStatusPruge());
                relacije.add(r2);
            }
        }
    }

    /*public List<RelacijaPruge> dohvatiRelacijeIzmedu(Stanica s1, Stanica s2) {
        List<RelacijaPruge> rezultat = new ArrayList<>();
        boolean zapoceto = false;


        for (RelacijaPruge rel : relacije) {
            if (rel.getPolaznaStanica().equals(s1)) {
                zapoceto = true;
            }
            if (zapoceto) {
                rezultat.add(rel);
            }
            if (rel.getOdredisnaStanica().equals(s2)) {
                break;
            }
        }
        return rezultat;
    }*/

    public List<RelacijaPruge> dohvatiRelacijeIzmedu(Stanica s1, Stanica s2) {
        List<RelacijaPruge> rezultat = new ArrayList<>();

        int idx1 = stations.indexOf(s1);
        int idx2 = stations.indexOf(s2);

        if (idx1 == -1 || idx2 == -1 || idx1 == idx2) {
            return rezultat;
        }

        if (idx1 > idx2) {
            int tmp = idx1;
            idx1 = idx2;
            idx2 = tmp;
        }

        for (int i = idx1; i < idx2; i++) {
            Stanica start = stations.get(i);
            Stanica end = stations.get(i + 1);

            Optional<RelacijaPruge> relacijaOpt = relacije.stream()
                    .filter(r -> r.getPolaznaStanica().equals(start)
                            && r.getOdredisnaStanica().equals(end))
                    .findFirst();

            relacijaOpt.ifPresent(rezultat::add);
        }

        return rezultat;
    }


    public List<RelacijaPruge> getRelacije() {
        return relacije;
    }

    private void inicijalizirajStateRelacije(RelacijaPruge rel, String status) {
        switch (status) {
            case "K" -> rel.postaviKvar();
            case "Z" -> rel.postaviZatvorena();
            case "T" -> rel.postaviTestiranje();
            default  -> rel.postaviIspravnu();
        }
    }

    public Stanica dohvatiStanicu(String naziv) {
        for (Stanica s : stations) {
            if (s.getNaziv().equals(naziv)) {
                return s;
            }
        }
        return null;
    }

    public Integer getTimeForType(Stanica s, VrstaVlaka type) {
        switch (type) {
            case NORMALNI:
                return s.getVrijemeNormalniVlak();
            case UBRZANI:
                return s.getVrijemeUbrzaniVlak();
            case BRZI:
                return s.getVrijemeBrziVlak();
            default:
                return 0;
        }
    }
}
