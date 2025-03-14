package org.uzdiz.managers;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.singleton.HrvatskeZeljeznice;

import java.time.LocalTime;
import java.util.*;

public class UpraviteljStanicama {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public UpraviteljStanicama(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    public double calculateDistanceBetweenStations(String startStation, String endStation) {
        List<ZeljeznickaPruga> allRailways = hrvatskeZeljeznice.getPruge();

        PathResult pathResult = handleDifferentRailways(startStation, endStation, allRailways);
        return pathResult.distance == Double.POSITIVE_INFINITY ? -1 : pathResult.distance;
    }

    public PathResult handleDifferentRailways(String startStation, String endStation, List<ZeljeznickaPruga> allRailways) {
        Map<String, Stanica> stationMap = new HashMap<>();
        Map<String, Map<String, Double>> connections = new HashMap<>();

        for (ZeljeznickaPruga railway : allRailways) {
            List<Stanica> stationsForEachRailway = railway.getStations();
            for (int i = 0; i < stationsForEachRailway.size(); i++) {
                Stanica currentStation = stationsForEachRailway.get(i);
                String currentStationName = currentStation.getNaziv();
                stationMap.put(currentStationName, currentStation);

                connections.putIfAbsent(currentStationName, new HashMap<>());

                if (i > 0) {
                    Stanica previousStation = stationsForEachRailway.get(i - 1);
                    String previousStationName = previousStation.getNaziv();
                    double distanceToNext = stationsForEachRailway.get(i).getDuzina();

                    connections.get(previousStationName).put(currentStationName, distanceToNext);
                    connections.putIfAbsent(currentStationName, new HashMap<>());
                    connections.get(currentStationName).put(previousStationName, distanceToNext);
                }
                if (i < stationsForEachRailway.size() - 1) {
                    Stanica nextStation = stationsForEachRailway.get(i + 1);
                    String nextStationName = nextStation.getNaziv();
                    double distanceToNext = nextStation.getDuzina();

                    connections.get(currentStationName).put(nextStationName, distanceToNext);
                    connections.putIfAbsent(nextStationName, new HashMap<>());
                    connections.get(nextStationName).put(currentStationName, distanceToNext);
                }
            }
        }

        return findShortestPath(startStation, endStation, connections);
    }

    private static PathResult findShortestPath(String startStation, String endStation, Map<String, Map<String, Double>> connections) {
        PriorityQueue<Map.Entry<String, Double>> pq = new PriorityQueue<>(Comparator.comparingDouble(Map.Entry::getValue));
        pq.add(new AbstractMap.SimpleEntry<>(startStation, 0.0));

        Map<String, Double> distances = new HashMap<>();
        distances.put(startStation, 0.0);

        Map<String, String> previousStations = new HashMap<>();
        Set<String> visited = new HashSet<>();

        while (!pq.isEmpty()) {
            Map.Entry<String, Double> current = pq.poll();
            String currentStation = current.getKey();
            double currentDistance = current.getValue();

            if (visited.contains(currentStation)) continue;
            visited.add(currentStation);

            if (currentStation.equals(endStation)) {
                List<String> path = new ArrayList<>();
                for (String at = endStation; at != null; at = previousStations.get(at)) {
                    path.add(at);
                }
                Collections.reverse(path);

                Map<String, Double> finalDistancesToNext = new HashMap<>();
                for (int i = 0; i < path.size() - 1; i++) {
                    String station = path.get(i);
                    String nextStation = path.get(i + 1);
                    double dist = connections.get(station).get(nextStation);
                    finalDistancesToNext.put(station, dist);
                }

                return new PathResult(currentDistance, path, finalDistancesToNext);
            }

            Map<String, Double> neighbors = connections.getOrDefault(currentStation, new HashMap<>());

            for (Map.Entry<String, Double> neighbor : neighbors.entrySet()) {
                String neighborStation = neighbor.getKey();
                double distanceToNeighbor = neighbor.getValue();

                if (visited.contains(neighborStation)) continue;

                double newDistance = currentDistance + distanceToNeighbor;

                if (newDistance < distances.getOrDefault(neighborStation, Double.POSITIVE_INFINITY)) {
                    distances.put(neighborStation, newDistance);
                    previousStations.put(neighborStation, currentStation);
                    pq.add(new AbstractMap.SimpleEntry<>(neighborStation, newDistance));
                }
            }
        }
        return new PathResult(Double.POSITIVE_INFINITY, Collections.emptyList(), Collections.emptyMap());
    }

    public static class PathResult {
        double distance;
        List<String> path;

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public List<String> getPath() {
            return path;
        }

        public void setPath(List<String> path) {
            this.path = path;
        }

        public Map<String, Double> getDistancesToNext() {
            return distancesToNext;
        }

        public void setDistancesToNext(Map<String, Double> distancesToNext) {
            this.distancesToNext = distancesToNext;
        }

        Map<String, Double> distancesToNext;

        PathResult(double distance, List<String> path, Map<String, Double> distancesToNext) {
            this.distance = distance;
            this.path = path;
            this.distancesToNext = distancesToNext;
        }
    }

    public LinkedHashMap<Stanica, LocalTime> calculateStationArrivals(KomponentaVoznogReda train, String day) {
        LinkedHashMap<Stanica, LocalTime> arrivals = new LinkedHashMap<>();
        LocalTime departureTime = null;
        String lastPrintedStation = null;
        for (KomponentaVoznogReda etapaVlaka : train.dohvatiEtape()) {
            //EtapaVlaka etapaVlaka = (EtapaVlaka) stageComponent;
            ZeljeznickaPruga pruga = HrvatskeZeljeznice.getInstance().getRailwayByOznaka(etapaVlaka.dohvatiOznakuPruge());

            if (day != null) {
                boolean runsToday = etapaVlaka.dohvatiOznakuDana().stream()
                        .anyMatch(dayTag -> dayTag.getCode().equalsIgnoreCase(day));

                if (!runsToday) {
                    continue;
                }
            }


            List<Stanica> stageStations = switch (etapaVlaka.dohvatiVrstaVlaka()) {
                case NORMALNI -> pruga.getStations();
                case UBRZANI -> pruga.getUbrzaneStanice();
                case BRZI -> pruga.getBrzeStanice();
            };

            if (stageStations.isEmpty()) {
                continue;
            }

            String direction = etapaVlaka.dohvatiSmjer();


            boolean withinRange = false;
            List<Stanica> filteredStations = new ArrayList<>();

            if (etapaVlaka.dohvatiSmjer().equalsIgnoreCase("O")) {
                for (int i = stageStations.size() - 1; i >= 0; i--) {
                    Stanica station = stageStations.get(i);

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
                for (Stanica station : stageStations) {
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

            Integer travelTime = 0;

            for (int i = 0; i < filteredStations.size(); i++) {
                Stanica currentStation = filteredStations.get(i);

                if (currentStation.getNaziv().equalsIgnoreCase(lastPrintedStation)) {
                    if (i == 0) {
                        departureTime = etapaVlaka.dohvatiVrijemePolaska();
                    }
                    continue;
                }

                if (i == 0) {
                    departureTime = etapaVlaka.dohvatiVrijemePolaska();
                    arrivals.put(currentStation, departureTime);
                    lastPrintedStation = currentStation.getNaziv();
                    continue;
                }


                Stanica previousStation = filteredStations.get(i - 1);

                if (etapaVlaka.dohvatiSmjer().equalsIgnoreCase("N")) {
                    travelTime = pruga.getTimeForType(currentStation, etapaVlaka.dohvatiVrstaVlaka());
                    if (travelTime != null) {
                        departureTime = departureTime.plusMinutes(travelTime);
                    }
                } else {
                    travelTime = pruga.getTimeForType(previousStation, etapaVlaka.dohvatiVrstaVlaka());
                    if (travelTime != null) {
                        departureTime = departureTime.plusMinutes(travelTime);
                    }
                }

                if (travelTime == null || travelTime < 0) {
                    System.out.println("Pogreška: Putno vrijeme za stanicu " + currentStation.getNaziv() + " je neispravno.");
                    continue;
                }

                arrivals.put(currentStation, departureTime);
                lastPrintedStation = currentStation.getNaziv();
            }
        }

        return arrivals;
    }
}
