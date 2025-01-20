package org.uzdiz.composite;

import org.uzdiz.builder.Stanica;
import org.uzdiz.builder.ZeljeznickaPruga;
import org.uzdiz.managers.UpraviteljStanicama;
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
        List<Stanica> sveStanicePruge = HrvatskeZeljeznice.getInstance().getPruge().stream()
                .filter(railway -> railway.getOznakaPruge().equals(oznakaPruge))
                .findFirst()
                .map(ZeljeznickaPruga::getStations)
                .orElse(new ArrayList<>());

        List<Stanica> staniceEtape = new ArrayList<>();

        boolean isNormal = smjer.equals("N");
        boolean isReverse = smjer.equals("O");
        if(isNormal) {
            boolean start = false;
            for (Stanica stanica : sveStanicePruge) {
                if (stanica.equals(polaznaStanica)) {
                    start = true;
                }
                if (start) {
                    staniceEtape.add(stanica);
                }
                if (stanica.equals(odredisnaStanica)) {
                    break;
                }
            }
        } else if(isReverse) {
            boolean start = false;
            for (int i = sveStanicePruge.size() - 1; i >= 0; i--) {
                Stanica stanica = sveStanicePruge.get(i);
                if (stanica.equals(polaznaStanica)) {
                    start = true;
                }
                if (start) {
                    staniceEtape.add(stanica);
                }
                if (stanica.getNaziv().equals(odredisnaStanica.getNaziv())) {
                    break;
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
}