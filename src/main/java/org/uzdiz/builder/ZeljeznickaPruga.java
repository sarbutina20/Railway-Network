package org.uzdiz.builder;

import org.uzdiz.composite.VrstaVlaka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ZeljeznickaPruga {
    private String oznakaPruge;
    private List<Stanica> stations = new ArrayList<>();
    private List<Integer> distances = new ArrayList<>();
    private int totalDistance = 0;

    private List<Stanica> normalneStanice = new ArrayList<>();
    private List<Stanica> ubrzaneStanice = new ArrayList<>();
    private List<Stanica> brzeStanice = new ArrayList<>();



    private ZeljeznickaPruga(Builder builder) {
        this.oznakaPruge = builder.oznakaPruge;
        this.stations = builder.stations;
        this.distances = builder.distances;
        this.totalDistance = builder.totalDistance;
    }

    public static class Builder {
        private String oznakaPruge;
        private List<Stanica> stations = new ArrayList<>();
        private List<Integer> distances = new ArrayList<>();
        private int totalDistance = 0;

        public Builder(String oznakaPruge) {
            this.oznakaPruge = oznakaPruge;
        }

        public Builder addStation(Stanica station) {
            stations.add(station);
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
