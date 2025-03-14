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
import java.util.stream.Collectors;

public class UKP2SHandler extends CommandHandler {

    HrvatskeZeljeznice hrvatskeZeljeznice;

    public UKP2SHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("UKP2S")) {
            String fullCommand = String.join(" ", commandParts);
            String[] segments = fullCommand.split(" - ");
            if (segments.length != 6) {
                System.out.println("Pogrešan broj parametara. Ispravna sintaksa: UKP2S polaznaStanica - odredišnaStanica - datum - odVr - doVr - načinKupovine");
                return;
            }

            String polaznaStanica = segments[0].replace("UKP2S ", "").trim();
            String odredisnaStanica = segments[1].trim();
            String datumPutovanja = segments[2].trim();
            String odVr = segments[3].trim();
            String doVr = segments[4].trim();
            String nacinKupovine = segments[5].toUpperCase().trim();

            if (!nacinKupovine.equals("B") && !nacinKupovine.equals("WM") && !nacinKupovine.equals("V")) {
                System.out.println("Pogreška: Način kupovine mora biti jedan od: B (blagajna), WM (web/mobilna aplikacija), V (vlak).");
                return;
            }
            LocalDate datum;
            LocalTime odVrijeme;
            LocalTime doVrijeme;
            try {
                odVrijeme = LocalTime.parse(odVr, TIME_FORMATTER);
                doVrijeme = LocalTime.parse(doVr, TIME_FORMATTER);
                datum = LocalDate.parse(datumPutovanja, formatter);

            } catch (DateTimeParseException e) {
                System.out.println("Pogreška: Datum mora biti u formatu dd.MM.yyyy.");
                return;
            }

            Set<OznakeDana> prepoznatiDan = prepoznajDan(datum);

            handleUKP2S(polaznaStanica, odredisnaStanica, datum, odVrijeme, doVrijeme, nacinKupovine, prepoznatiDan);
        } else {
            super.handle(command, commandParts);
        }
    }


    private void handleUKP2S(String polaznaStanica, String odredisnaStanica, LocalDate datumPutovanja, LocalTime odVrijeme, LocalTime doVrijeme, String nacinKupovine, Set<OznakeDana> dan) {

        if (polaznaStanica.equalsIgnoreCase(odredisnaStanica)) {
            System.out.println("Polazna i odredišna stanica ne mogu biti iste.");
            return;
        }

        List<KomponentaVoznogReda> vlakoviPoStanicama = hrvatskeZeljeznice.getVlakovi().stream().filter(vlak -> vlak.dohvatiSveStanice().stream().anyMatch(stanica -> stanica.getNaziv().equalsIgnoreCase(polaznaStanica)) && vlak.dohvatiSveStanice().stream().anyMatch(stanica -> stanica.getNaziv().equalsIgnoreCase(odredisnaStanica))).collect(Collectors.toList());

        List<KomponentaVoznogReda> vlakoviPoDanima = vlakoviPoStanicama.stream().filter(vlak -> {
            Set<OznakeDana> vlakDani = vlak.dohvatiOznakuDana();
            return vlakDani != null && !Collections.disjoint(vlakDani, dan);
        }).collect(Collectors.toList());


        List<KomponentaVoznogReda> filtriraniVlakovi = vlakoviPoDanima.stream().filter(vlak -> {
            List<Stanica> sveStanice = vlak.dohvatiSveStanice();
            Stanica polazna = sveStanice.stream().filter(stanica -> stanica.getNaziv().equalsIgnoreCase(polaznaStanica)).findFirst().orElse(null);
            Stanica odredisna = sveStanice.stream().filter(stanica -> stanica.getNaziv().equalsIgnoreCase(odredisnaStanica)).findFirst().orElse(null);
            if (polazna == null || odredisna == null) {
                return false;
            }

            LocalTime vrijemePolaska = vlak.dohvatiVrijemePolaska();
            LocalTime vrijemeDolaska = vlak.izracunajDolazakZadnjeEtape();

            LocalTime vrijemePolaskaIzPolazneStanice = vlak.izracunajVrijemePolaska(polazna.getNaziv());
            LocalTime vrijemePolaskaIzOdredisneStanice = vlak.izracunajVrijemePolaska(odredisna.getNaziv());


            if (vrijemePolaskaIzOdredisneStanice.isBefore(vrijemePolaskaIzPolazneStanice)) {
                return false;
            }

            if (vrijemePolaska.isAfter(doVrijeme)) {
                return false;
            }

            if (vrijemeDolaska.isBefore(odVrijeme) || vrijemePolaska.isBefore(odVrijeme)) {
                return false;
            }

            if (vrijemeDolaska.isAfter(doVrijeme)) {
                return false;
            }

            return vrijemePolaska != null && vrijemeDolaska != null;
        }).collect(Collectors.toList());

        if (filtriraniVlakovi.isEmpty()) {
            System.out.println("Nema vlakova koji zadovoljavaju uvjete.");
            return;
        }

        PostavkeCijena postavke = PostavkeCijena.getInstance();
        double osnovnaCijena = 0;
        KontekstKupovine kontekst = new KontekstKupovine();
        switch (nacinKupovine) {
            case "B" -> kontekst.postaviStrategiju(new KupovinaBlagajna());
            case "WM" -> kontekst.postaviStrategiju(new KupovinaAplikacija());
            case "V" -> kontekst.postaviStrategiju(new KupovinaVlak());
        }
        double ukupnaCijena = 0;
        double popusti = 0;
        double udaljenost = 0;
        IspisKarti ispisKarti = new IspisKarti();

        List<KomponentaVoznogReda> sortiraniVlakovi = filtriraniVlakovi.stream()
                .sorted(Comparator.comparing(vlak -> vlak.dohvatiVrijemePolaska()))
                .collect(Collectors.toList());

        for (KomponentaVoznogReda vlak : sortiraniVlakovi) {
            List<Stanica> stanice = vlak.dohvatiSveStanice();
            Stanica polazna = stanice.stream()
                    .filter(stanica -> stanica.getNaziv().equalsIgnoreCase(polaznaStanica))
                    .findFirst().orElse(null);
            Stanica odredisna = stanice.stream()
                    .filter(stanica -> stanica.getNaziv().equalsIgnoreCase(odredisnaStanica))
                    .findFirst().orElse(null);
            boolean provjeraIspravnostiRute = vlak.provjeraIspravnostiRute(polazna, odredisna);
            if (!provjeraIspravnostiRute) {
                continue;
            }

            udaljenost = vlak.izracunajUdaljenostIzmeduStanica(polaznaStanica, odredisnaStanica);

            double cijenaPoKm = switch (vlak.dohvatiVrstaVlaka()) {
                case NORMALNI -> postavke.dohvatiCijenu(VrstaVlaka.NORMALNI);
                case UBRZANI -> postavke.dohvatiCijenu(VrstaVlaka.UBRZANI);
                case BRZI -> postavke.dohvatiCijenu(VrstaVlaka.BRZI);
            };

            osnovnaCijena = cijenaPoKm * udaljenost;

            ukupnaCijena = kontekst.izracunajCijenu(osnovnaCijena, datumPutovanja,
                    nacinKupovine.equals("WM") ? postavke.dohvatiPopustWebMob() :
                            nacinKupovine.equals("V") ? postavke.dohvatiUvecanjeVlak() : 0);

            popusti = kontekst.vratiPopuste(datumPutovanja);

            double tempPopust = PostavkeCijena.getInstance().dohvatiPrivremeniPopust(polaznaStanica, odredisnaStanica);
            ukupnaCijena *= (1 - tempPopust / 100);

            if (popusti > 0) {
                popusti += tempPopust;
            }

            KartaOriginator upravljanje = new KartaOriginator(vlak.dohvatiOznaku(), vlak.dohvatiVrstaVlaka(), nacinKupovine, udaljenost, datumPutovanja, vlak.izracunajVrijemePolaska(polaznaStanica), vlak.izracunajVrijemeDolaska(polaznaStanica, odredisnaStanica), osnovnaCijena, ukupnaCijena, popusti, LocalDateTime.now(), polaznaStanica + "-" + odredisnaStanica);
            KartaMemento karta = upravljanje.kreirajMemento();

            System.out.println(ispisKarti.formatirajPodatkeOKarti(karta));
        }
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
