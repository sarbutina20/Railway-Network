package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.OznakeDana;
import org.uzdiz.composite.VrstaVlaka;
import org.uzdiz.memento.IspisKarti;
import org.uzdiz.memento.KartaMemento;
import org.uzdiz.memento.KartaOriginator;
import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.singleton.PostavkeCijena;

import org.uzdiz.strategy.KontekstKupovine;
import org.uzdiz.strategy.KupovinaAplikacija;
import org.uzdiz.strategy.KupovinaBlagajna;
import org.uzdiz.strategy.KupovinaVlak;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

public class KKPV2SHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public KKPV2SHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("KKPV2S")) {
            String fullCommand = String.join(" ", commandParts);
            String[] segments = fullCommand.split(" - ");
            if (segments.length != 5) {
                System.out.println("Pogrešan broj parametara. Ispravna sintaksa: KKPV2S oznaka - polaznaStanica - odredišnaStanica - datum - načinKupovine");
                return;
            }

            String oznakaVlaka = segments[0].replace("KKPV2S ", "").trim();
            String polaznaStanica = segments[1].trim();
            String odredisnaStanica = segments[2].trim();
            String datumPutovanja = segments[3].trim();
            String nacinKupovine = segments[4].toUpperCase().trim();

            if (!nacinKupovine.equals("B") && !nacinKupovine.equals("WM") && !nacinKupovine.equals("V")) {
                System.out.println("Pogreška: Način kupovine mora biti jedan od: B (blagajna), WM (web/mobilna aplikacija), V (vlak).");
                return;
            }
            LocalDate datum;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                datum = LocalDate.parse(datumPutovanja, formatter);

                if (datum.isBefore(LocalDate.now())) {
                    System.out.println("Pogreška: Datum putovanja ne može biti u prošlosti.");
                    return;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Pogreška: Datum mora biti u formatu dd.MM.yyyy.");
                return;
            }

            handleKKPV2S(oznakaVlaka, polaznaStanica, odredisnaStanica, datum, nacinKupovine);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleKKPV2S(String oznakaVlaka, String polaznaStanica, String odredisnaStanica, LocalDate datumPutovanja, String nacinKupovine) {
        KomponentaVoznogReda vlak = hrvatskeZeljeznice.getVlakovi().stream()
                .filter(v -> v.dohvatiOznaku().equals(oznakaVlaka))
                .findFirst()
                .orElse(null);

        List<Stanica> stanice = vlak.dohvatiSveStanice();
        Stanica polazna = stanice.stream()
                .filter(stanica -> stanica.getNaziv().equalsIgnoreCase(polaznaStanica))
                .findFirst().orElse(null);

        Stanica odredisna = stanice.stream()
                .filter(stanica -> stanica.getNaziv().equalsIgnoreCase(odredisnaStanica))
                .findFirst().orElse(null);

        if(polazna == null || odredisna == null) {
            System.out.println("Pogreška: Jedna ili obje stanice ne postoje u voznom redu vlaka.");
            return;
        }

        boolean ispravnaKupovina = provjeraIspravnostiKupovine(vlak, oznakaVlaka, polazna, odredisna, datumPutovanja);
        boolean provjeraIspravnostiRute = vlak.provjeraIspravnostiRute(polazna, odredisna);

        if (!ispravnaKupovina || !provjeraIspravnostiRute) {
            return;
        }

        double udaljenost = vlak.izracunajUdaljenostIzmeduStanica(polaznaStanica, odredisnaStanica);

        PostavkeCijena postavke = PostavkeCijena.getInstance();
        double cijenaPoKm = switch (vlak.dohvatiVrstaVlaka()) {
            case NORMALNI -> postavke.dohvatiCijenu(VrstaVlaka.NORMALNI);
            case UBRZANI -> postavke.dohvatiCijenu(VrstaVlaka.UBRZANI);
            case BRZI -> postavke.dohvatiCijenu(VrstaVlaka.BRZI);
        };

        double osnovnaCijena = cijenaPoKm * udaljenost;

        KontekstKupovine kontekst = new KontekstKupovine();
        switch (nacinKupovine) {
            case "B" -> kontekst.postaviStrategiju(new KupovinaBlagajna());
            case "WM" -> kontekst.postaviStrategiju(new KupovinaAplikacija());
            case "V" -> kontekst.postaviStrategiju(new KupovinaVlak());
        }

        double ukupnaCijena = kontekst.izracunajCijenu(osnovnaCijena, datumPutovanja,
                nacinKupovine.equals("WM") ? postavke.dohvatiPopustWebMob() :
                        nacinKupovine.equals("V") ? postavke.dohvatiUvecanjeVlak() : 0);

        double tempPopust = PostavkeCijena.getInstance().dohvatiPrivremeniPopust(polaznaStanica, odredisnaStanica);
        ukupnaCijena *= (1 - tempPopust / 100);

        double popusti = kontekst.vratiPopuste(datumPutovanja);

        if (popusti > 0) {
            popusti += tempPopust;
        }

        KartaOriginator upravljanje = new KartaOriginator(oznakaVlaka, vlak.dohvatiVrstaVlaka(), nacinKupovine, udaljenost, datumPutovanja, vlak.izracunajVrijemePolaska(polaznaStanica), vlak.izracunajVrijemeDolaska(polaznaStanica, odredisnaStanica), osnovnaCijena, ukupnaCijena, popusti, LocalDateTime.now(), polaznaStanica + "-" + odredisnaStanica);
        KartaMemento karta = upravljanje.kreirajMemento();

        hrvatskeZeljeznice.getPovijestKarata().dodajKartu(karta);

        IspisKarti ispisKarti = new IspisKarti();

        System.out.println(ispisKarti.formatirajPodatkeOKarti(karta));
    }


    private boolean provjeraIspravnostiKupovine(KomponentaVoznogReda vlak, String oznakaVlaka, Stanica polaznaStanica, Stanica odredisnaStanica, LocalDate datumPutovanja) {
        if (vlak == null) {
            System.out.println("Pogreška: Vlak s oznakom " + oznakaVlaka + " ne postoji.");
            return false;
        }

        Set<OznakeDana> dan = prepoznajDan(datumPutovanja);

        Set<OznakeDana> vlakDani = vlak.dohvatiOznakuDana();
        if (vlakDani == null || vlakDani.isEmpty() || Collections.disjoint(vlakDani, dan)) {
            System.out.println("Pogreška: Vlak s oznakom " + vlak.dohvatiOznaku() + " ne vozi tim danom.");
            return false;
        }


        if (polaznaStanica.getNaziv().equalsIgnoreCase(odredisnaStanica.getNaziv())) {
            System.out.println("Pogreška: Polazna i odredišna stanica ne mogu biti iste.");
            return false;
        }



        if (polaznaStanica == null || odredisnaStanica == null) {
            System.out.println("Pogreška: Jedna ili obje stanice ne postoje u mreži.");
            return false;
        }

        LocalTime vrijemePolaskaIzPolazneStanice = vlak.izracunajVrijemePolaska(polaznaStanica.getNaziv());
        LocalTime vrijemePolaskaIzOdredisneStanice = vlak.izracunajVrijemePolaska(odredisnaStanica.getNaziv());

        if (vrijemePolaskaIzOdredisneStanice.isBefore(vrijemePolaskaIzPolazneStanice)) {
            System.out.println("Pogreška: Vlak s tom oznakom ne putuje tim smjerom na taj datum.");
            return false;
        }

        return true;
    }
    private Set<OznakeDana> prepoznajDan(LocalDate datum) {
        DayOfWeek dayOfWeek = datum.getDayOfWeek();
        Set<OznakeDana> danSet = EnumSet.noneOf(OznakeDana.class);

        switch (dayOfWeek) {
            case MONDAY:
                danSet.add(OznakeDana.PONEDJELJAK);
                break;
            case TUESDAY:
                danSet.add(OznakeDana.UTORAK);
                break;
            case WEDNESDAY:
                danSet.add(OznakeDana.SRIJEDA);
                break;
            case THURSDAY:
                danSet.add(OznakeDana.CETVRTAK);
                break;
            case FRIDAY:
                danSet.add(OznakeDana.PETAK);
                break;
            case SATURDAY:
                danSet.add(OznakeDana.SUBOTA);
                break;
            case SUNDAY:
                danSet.add(OznakeDana.NEDJELJA);
                break;
        }
        return danSet;
    }



}