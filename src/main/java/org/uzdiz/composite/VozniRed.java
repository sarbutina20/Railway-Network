package org.uzdiz.composite;

import org.uzdiz.builder.Stanica;
import org.uzdiz.managers.UpraviteljStanicama;

import java.time.LocalTime;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class VozniRed implements KomponentaVoznogReda {

    private final List<Vlak> vlakovi;

    private final Integer trainScheduleID;

    @Override
    public VrstaVlaka dohvatiVrstaVlaka() {
        return null;
    }

    public VozniRed(List<Vlak> vlakovi) {
        this.vlakovi = vlakovi;
        this.trainScheduleID = new Random().nextInt(1000) + 1;

    }

    @Override
    public LocalTime izracunajVrijemePolaska(String polaznaStanica) {
        return null;
    }

    @Override
    public double izracunajUdaljenostIzmeduStanica(String polazna, String odredisna) {
        return 0;
    }

    @Override
    public LocalTime izracunajVrijemeDolaska(String polaznaStanica, String odredisnaStanica) {
        return null;
    }

    @Override
    public String dohvatiLinijuCSV() {
        return "";
    }

    @Override
    public Integer dohvatiBrojLinijeCSV() {
        return 0;
    }

    @Override
    public String dohvatiOznaku() {
        return trainScheduleID.toString();
    }

    @Override
    public List<Stanica> dohvatiSveStanice() {
        return vlakovi.stream()
                .flatMap(train -> train.dohvatiSveStanice().stream())
                .collect(Collectors.toList());
    }

    @Override
    public LocalTime dohvatiVrijemePolaska() {
        return null;
    }

    @Override
    public String dohvatiSmjer() {
        return null;
    }

    @Override
    public String dohvatiOznakuPruge() {
        return null;
    }

    @Override
    public List<KomponentaVoznogReda> getChildren() {
        return vlakovi.stream()
                .map(train -> (KomponentaVoznogReda) train)
                .collect(Collectors.toList());
    }

    @Override
    public void dodajKomponentu(KomponentaVoznogReda component) {
        this.getChildren().add(component);
    }

    @Override
    public Stanica dohvatiPolaznuStanicu() {
        return null;
    }

    @Override
    public Stanica dohvatiOdredisnuStanicu() {
        return null;
    }

    @Override
    public String dohvatiNazivPolazneStanice() {
        return null;
    }

    @Override
    public String dohvatiNazivOdredisneStanice() {
        return null;
    }


    @Override
    public List<KomponentaVoznogReda> dohvatiEtape() {
        return null;
    }

    @Override
    public Set<OznakeDana> dohvatiOznakuDana() {
        return null;
    }

    @Override
    public LocalTime izracunajDolazakZadnjeEtape() {
        return null;
    }

    @Override
    public boolean validirajEtapeVlaka() {
        return false;
    }

    @Override
    public boolean provjeraValidnostiRelacija() {
        return false;
    }

    @Override
    public boolean provjeraIspravnostiRute(Stanica polaznaStanica, Stanica odredisnaStanica) {
        return false;
    }

    @Override
    public double izracunajUkupnuUdaljenost(UpraviteljStanicama upraviteljStanicama) {
        return 0;
    }

}