package org.uzdiz.chain_of_responsibility;

import org.uzdiz.builder.Stanica;
import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.composite.VrstaVlaka;
import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.memento.IspisKarti;
import org.uzdiz.memento.KartaMemento;
import org.uzdiz.memento.KartaOriginator;
import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.singleton.PostavkeCijena;
import org.uzdiz.strategy.KontekstKupovine;
import org.uzdiz.strategy.KupovinaAplikacija;
import org.uzdiz.strategy.KupovinaBlagajna;
import org.uzdiz.strategy.KupovinaVlak;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

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
            LocalDate datum = null;
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy.");
                datum = LocalDate.parse(datumPutovanja, formatter);
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
        if (vlak == null) {
            System.out.println("Pogreška: Vlak s oznakom " + oznakaVlaka + " ne postoji.");
            return;
        }

        List<Stanica> stanice = vlak.dohvatiSveStanice();
        boolean postojiPolazna = stanice.stream().anyMatch(s -> s.getNaziv().equalsIgnoreCase(polaznaStanica));
        boolean postojiOdredisna = stanice.stream().anyMatch(s -> s.getNaziv().equalsIgnoreCase(odredisnaStanica));
        if (!postojiPolazna || !postojiOdredisna) {
            System.out.println("Pogreška: Stanice nisu na ruti vlaka.");
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

        double popusti = kontekst.vratiPopuste(datumPutovanja);


        KartaOriginator upravljanje = new KartaOriginator(oznakaVlaka, vlak.dohvatiVrstaVlaka(), nacinKupovine, udaljenost, datumPutovanja, vlak.izracunajVrijemePolaska(polaznaStanica), vlak.izracunajVrijemeDolaska(polaznaStanica, odredisnaStanica), osnovnaCijena, ukupnaCijena, popusti, LocalDateTime.now(), polaznaStanica+"-"+odredisnaStanica);
        KartaMemento karta = upravljanje.kreirajMemento();

        hrvatskeZeljeznice.getPovijestKarata().dodajKartu(karta);

        IspisKarti ispisKarti = new IspisKarti();

        System.out.println(ispisKarti.formatirajPodatkeOKarti(karta));
    }
}