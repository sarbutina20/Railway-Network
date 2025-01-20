package org.uzdiz.observer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Korisnik implements ObserverPutovanjaVlakom {
    private final String ime;
    private static final Map<String, Korisnik> registarKorisnika = new HashMap<>();

    private Korisnik(String ime) {
        this.ime = ime;
    }

    public static Korisnik kreirajKorisnika(String name) {
        return registarKorisnika.computeIfAbsent(name, Korisnik::new);
    }

    public static List<String> vratiListuKorisnika() {
        return new ArrayList<>(registarKorisnika.keySet());
    }

    public static Korisnik dohvatiKorisnikaPoImenu(String name) {
        return registarKorisnika.get(name);
    }

    public static boolean provjeriPostojanjeKorisnika(String ime) {
        return registarKorisnika.containsKey(ime);
    }

    @Override
    public void update(String poruka) {
        System.out.println("Obavijest za korisnika " + ime + ": " + poruka);
    }

    @Override
    public String toString() {
        return ime;
    }
}
