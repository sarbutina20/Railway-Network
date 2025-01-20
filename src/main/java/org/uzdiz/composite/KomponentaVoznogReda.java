package org.uzdiz.composite;

import org.uzdiz.builder.Stanica;
import org.uzdiz.managers.UpraviteljStanicama;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface KomponentaVoznogReda {
    String dohvatiOznaku();
    List<Stanica> dohvatiSveStanice();
    LocalTime dohvatiVrijemePolaska();
    String dohvatiSmjer();
    String dohvatiOznakuPruge();
    List<KomponentaVoznogReda> getChildren();
    void dodajKomponentu(KomponentaVoznogReda component);
    Stanica dohvatiPolaznuStanicu();
    Stanica dohvatiOdredisnuStanicu();
    List<KomponentaVoznogReda> dohvatiEtape();
    String dohvatiNazivPolazneStanice();
    String dohvatiNazivOdredisneStanice();
    String dohvatiLinijuCSV();
    Integer dohvatiBrojLinijeCSV();
    public LocalTime izracunajDolazakZadnjeEtape();
    public boolean validirajEtapeVlaka();
    public double izracunajUkupnuUdaljenost(UpraviteljStanicama upraviteljStanicama);

    public double izracunajUdaljenostIzmeduStanica(String polazna, String odredisna);
    public VrstaVlaka dohvatiVrstaVlaka();

    public Set<OznakeDana> dohvatiOznakuDana();

    LocalTime izracunajVrijemePolaska(String polaznaStanica);

    LocalTime izracunajVrijemeDolaska(String polaznaStanica, String odredisnaStanica);
}

