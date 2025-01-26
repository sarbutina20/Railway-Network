package org.uzdiz.composite;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.managers.UpraviteljStanicama;
import org.uzdiz.singleton.HrvatskeZeljeznice;
import org.uzdiz.state.RelacijaPruge;
import org.uzdiz.singleton.HrvatskeZeljeznice;


import java.time.LocalTime;
import java.util.*;


public class EtapaVlaka implements KomponentaVoznogReda {

    private final String oznakaPruge;
    private final String smjer;
    private final Stanica polaznaStanica;
    private final Stanica odredisnaStanica;
    private final LocalTime vrijemePolaska;
    private final VrstaVlaka vrstaVlaka;
    private final String oznakaVlaka;
    private final Set<OznakeDana> oznakaDana;
    private final LocalTime trajanjeVoznje;

    private final String linijaCSV;
    private final int brojLinijeCSV;

    @Override
    public double izracunajUdaljenostIzmeduStanica(String polazna, String odredisna) {
        return 0;
    }

    public EtapaVlaka(String oznakaPruge, String smjer, Stanica polaznaStanica,
                      Stanica odredisnaStanica, LocalTime vrijemePolaska, LocalTime trajanjeVoznje,
                      VrstaVlaka vrstaVlaka, String oznakaVlaka, Set<OznakeDana> oznakaDana, String linijaCSV, int brojLinijeCSV) {
        this.oznakaPruge = oznakaPruge;
        this.smjer = smjer;
        this.polaznaStanica = polaznaStanica;
        this.odredisnaStanica = odredisnaStanica;
        this.vrijemePolaska = vrijemePolaska;
        this.vrstaVlaka = vrstaVlaka == null ? VrstaVlaka.NORMALNI : vrstaVlaka;
        this.oznakaVlaka = oznakaVlaka;
        this.oznakaDana = oznakaDana == null ? EnumSet.allOf(OznakeDana.class) : oznakaDana;
        this.trajanjeVoznje = trajanjeVoznje;
        this.linijaCSV = linijaCSV;
        this.brojLinijeCSV = brojLinijeCSV;
    }

    public LocalTime getTrajanjeVoznje() {
        return trajanjeVoznje;
    }


    @Override
    public String dohvatiLinijuCSV() {
        return linijaCSV;
    }

    @Override
    public Integer dohvatiBrojLinijeCSV() {
        return brojLinijeCSV;
    }



    @Override
    public String dohvatiOznaku() {
        return oznakaVlaka;
    }

    @Override
    public List<Stanica> dohvatiSveStanice() {
        HrvatskeZeljeznice hrvatskeZeljeznice = HrvatskeZeljeznice.getInstance();
        ZeljeznickaPruga pruga = hrvatskeZeljeznice.getRailwayByOznaka(this.oznakaPruge);
        List<Stanica> sveStanicePruge = pruga.getStations();

        List<Stanica> staniceEtape = new ArrayList<>();

        boolean isNormal = smjer.equals("N");
        boolean isReverse = smjer.equals("O");
        if(isNormal) {
            boolean start = false;
            for (Stanica stanica : sveStanicePruge) {
                if (stanica.equals(this.polaznaStanica)) {
                    start = true;
                }
                if (start) {
                    staniceEtape.add(stanica);
                }
                if (stanica.getNaziv().equalsIgnoreCase(this.odredisnaStanica.getNaziv())) {
                    return staniceEtape;
                }
            }
        } else if(isReverse) {
            boolean start = false;
            for (int i = sveStanicePruge.size() - 1; i >= 0; i--) {
                Stanica stanica = sveStanicePruge.get(i);
                if (stanica.equals(this.polaznaStanica)) {
                    start = true;
                }
                if (start) {
                    staniceEtape.add(stanica);
                }
                if (stanica.getNaziv().equals(this.odredisnaStanica.getNaziv())) {
                    return staniceEtape;
                }
            }
        }

        return staniceEtape;
    }

   @Override
    public LocalTime dohvatiVrijemePolaska() {
        return vrijemePolaska;
    }

    @Override
    public List<KomponentaVoznogReda> getChildren() {
        return new ArrayList<>();
    }

    @Override
    public void dodajKomponentu(KomponentaVoznogReda component) {
        throw new UnsupportedOperationException("Nije moguće dodati komponentu na etapu vlaka");
    }

    @Override
    public String dohvatiNazivPolazneStanice() {
        return polaznaStanica != null ? polaznaStanica.getNaziv() : "Nepoznato";
    }

    @Override
    public String dohvatiNazivOdredisneStanice() {
        return odredisnaStanica != null ? odredisnaStanica.getNaziv() : "Nepoznato";
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EtapaVlaka that = (EtapaVlaka) o;
        return Objects.equals(oznakaPruge, that.oznakaPruge) &&
                Objects.equals(smjer, that.smjer) &&
                Objects.equals(polaznaStanica, that.polaznaStanica) &&
                Objects.equals(odredisnaStanica, that.odredisnaStanica) &&
                Objects.equals(vrijemePolaska, that.vrijemePolaska) &&
                Objects.equals(oznakaVlaka, that.oznakaVlaka) &&
                Objects.equals(oznakaDana, that.oznakaDana);
    }

    @Override
    public int hashCode() {
        return Objects.hash(oznakaPruge, smjer, polaznaStanica, odredisnaStanica, vrijemePolaska, oznakaVlaka, oznakaDana);
    }

    @Override
    public String dohvatiOznakuPruge() {
        return oznakaPruge;
    }

    @Override
    public String dohvatiSmjer() {
        return smjer;
    }

    @Override
    public Stanica dohvatiPolaznuStanicu() {
        return polaznaStanica;
    }

    @Override
    public Stanica dohvatiOdredisnuStanicu() {
        return odredisnaStanica;
    }

    public LocalTime getVrijemePolaska() {
        return vrijemePolaska;
    }

    public VrstaVlaka dohvatiVrstaVlaka() {
        return vrstaVlaka;
    }

    public String getOznakaVlaka() {
        return oznakaVlaka;
    }

    public String getOznakaDanaAsString() {
        return OznakeDana.toCompositeCode(oznakaDana);
    }

    @Override
    public Set<OznakeDana> dohvatiOznakuDana() {
        return oznakaDana;
    }

    @Override
    public LocalTime izracunajVrijemePolaska(String polaznaStanica) {
        return null;
    }

    @Override
    public LocalTime izracunajVrijemeDolaska(String polaznaStanica, String odredisnaStanica) {
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
    public double izracunajUkupnuUdaljenost(UpraviteljStanicama upraviteljStanicama) {
        return 0;
    }

    @Override
    public List<KomponentaVoznogReda> dohvatiEtape() {
        return null;
    }

    public boolean provjeraValidnostiRelacija() {
        ZeljeznickaPruga pruga = HrvatskeZeljeznice.getInstance().getPruge().stream()
                .filter(railway -> railway.getOznakaPruge().equals(oznakaPruge))
                .findFirst()
                .orElse(null);
        List<Stanica> sveStanicaPruge = pruga.getStations();

        List<Stanica> filtered = filtrirajStanicePoRasponu(sveStanicaPruge);

        List<RelacijaPruge> relacije = pruga.dohvatiRelacijeIzmedu(filtered.getFirst(), filtered.getLast());
        for (RelacijaPruge relacija : relacije) {
            if (!relacija.getStatusRelacije().equals("I")) {
                System.out.println("Nije moguće kupiti kartu jer relacija između " +
                        relacija.getPolaznaStanica().getNaziv() + " i " + relacija.getOdredisnaStanica().getNaziv() +
                        " nije ispravna. Status: " + relacija.getStatusRelacije());
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean provjeraIspravnostiRute(Stanica polaznaStanica, Stanica odredisnaStanica) {

        if(polaznaStanica == null || odredisnaStanica == null) {
            System.out.println("Pogreška: Jedna ili obje stanice ne postoje u mreži.");
            return false;
        }

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
        return true;
    }

    public List<Stanica> filtrirajStanicePoRasponu(List<Stanica> sveStanicePruge) {
        boolean withinRange = false;
        List<Stanica> filteredStations = new ArrayList<>();
        if (smjer.equalsIgnoreCase("O")) {
            for (int i = sveStanicePruge.size() - 1; i >= 0; i--) {
                Stanica station = sveStanicePruge.get(i);

                if (station.getNaziv().equalsIgnoreCase(polaznaStanica.getNaziv())) {
                    withinRange = true;
                }

                if (withinRange) {
                    filteredStations.add(station);
                }

                if (station.equals(odredisnaStanica)) {
                    break;
                }
            }
        } else {
            for (Stanica station : sveStanicePruge) {
                if (station.getNaziv().equalsIgnoreCase(polaznaStanica.getNaziv())) {
                    withinRange = true;
                }
                if (withinRange) {
                    filteredStations.add(station);
                }
                if (station.equals(odredisnaStanica)) {
                    break;
                }
            }
        }
        return filteredStations;
    }


}