package org.uzdiz.singleton;

import org.uzdiz.builder.Kompozicija;
import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.Vozilo;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.command.DiscountInvoker;
import org.uzdiz.composite.OznakeDana;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.factory.*;
import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.mediator.Mediator;
import org.uzdiz.mediator.KontrolaPrometa;
import org.uzdiz.memento.PovijestKarata;
import org.uzdiz.memento.KartaOriginator;
import org.uzdiz.observer.UpraviteljPutovanjaVlakom;
import org.uzdiz.observer.SubjektPutovanjaVlakom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HrvatskeZeljeznice {
    private static HrvatskeZeljeznice instance;
    private List<ZeljeznickaPruga> pruge;
    private List<Vozilo> vozila;
    private List<Kompozicija> kompozicije;
    private List<KomponentaVoznogReda> vlakovi;
    private List<OznakeDana> oznakeDana;

    private KomponentaVoznogReda vozniRed;

    private final SubjektPutovanjaVlakom journeyManager;
    private final Mediator trafficControl;

    private final UpraviteljStanicama upraviteljStanicama;
    private final PovijestKarata povijestKarata;

    private final DiscountInvoker discountInvoker;


    private HrvatskeZeljeznice() {
        this.pruge = new ArrayList<>();
        this.vozila = new ArrayList<>();
        this.kompozicije = new ArrayList<>();
        this.vlakovi = new ArrayList<>();
        this.oznakeDana = new ArrayList<>();
        this.journeyManager = new UpraviteljPutovanjaVlakom(this);
        this.trafficControl = new KontrolaPrometa(this);
        this.upraviteljStanicama = new UpraviteljStanicama(this);
        this.povijestKarata = new PovijestKarata();
        this.discountInvoker = new DiscountInvoker();
    }

    public DiscountInvoker getDiscountInvoker() {
        return discountInvoker;
    }

    public PovijestKarata getPovijestKarata() {
        return povijestKarata;
    }
    public SubjektPutovanjaVlakom getJourneyManager() {
        return journeyManager;
    }

    public UpraviteljStanicama getStationManager() {
        return upraviteljStanicama;
    }

    public Mediator getTrafficControl() {
        return trafficControl;
    }


    public static HrvatskeZeljeznice getInstance() {
        if (instance == null) {
            instance = new HrvatskeZeljeznice();
        }
        return instance;
    }

    public void initialize(String stationFilePath, String vehicleFilePath, String compositionFilePath, String dayTagFilePath, String railwayScheduleFilePath) {

        CSVLoaderFactory railwaysFactory = new RailwaysCSVLoaderFactory(stationFilePath);
        setPruge(railwaysFactory.createLoader().loadCSV());

        CSVLoaderFactory vehicleFactory = new VehiclesCSVLoaderFactory(vehicleFilePath);
        setVozila(vehicleFactory.createLoader().loadCSV());

        CSVLoaderFactory compositionFactory = new CompositionsCSVLoaderFactory(compositionFilePath);
        setKompozicije(compositionFactory.createLoader().loadCSV());

        CSVLoaderFactory dayTagFactory = new DayTagCSVLoaderFactory(dayTagFilePath);
        setOznakeDana(dayTagFactory.createLoader().loadCSV());

        CSVLoaderFactory trainScheduleFactory = new TrainScheduleCSVLoaderFactory(railwayScheduleFilePath);
        setVlakovi(trainScheduleFactory.createLoader().loadCSV());

        System.out.println("Ukupan broj grešaka prilikom učitavanja CSV datoteka: " + CSVLoader.dohvatiBrojUkupnihGreski());
    }

    public List<KomponentaVoznogReda> getVlakovi() {
        return vlakovi;
    }

    public List<Stanica> getStations() {
        return pruge.stream()
                .flatMap(railway -> railway.getStations().stream())
                .distinct()
                .collect(Collectors.toList());
    }

    public void setVlakovi(List<KomponentaVoznogReda> vlakovi) {
        this.vlakovi = vlakovi;
    }

    public void setOznakeDana(List<OznakeDana> oznakeDana) {
        this.oznakeDana = oznakeDana;
    }

    public List<OznakeDana> getOznakeDana() {
        return oznakeDana;
    }

    public void setPruge(List<ZeljeznickaPruga> pruge) {
        this.pruge = pruge;
    }

    public void setVozila(List<Vozilo> vozila) {
        this.vozila = vozila;
    }

    public void setKompozicije(List<Kompozicija> kompozicije) {
        this.kompozicije = kompozicije;
    }

    public List<ZeljeznickaPruga> getPruge() {
        return pruge;
    }

    public List<Vozilo> getVozila() {
        return vozila;
    }

    public ZeljeznickaPruga getRailwayByOznaka(String oznakaPruge) {
        for (ZeljeznickaPruga pruga : pruge) {
            if (pruga.getOznakaPruge().equals(oznakaPruge)) {
                return pruga;
            }
        }
        return null;
    }

    public List<Kompozicija> getKompozicije() {
        return kompozicije;
    }


    public void setVozniRed(KomponentaVoznogReda vozniRed) {
        this.vozniRed = vozniRed;
    }
}
