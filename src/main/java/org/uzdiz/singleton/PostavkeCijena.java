package org.uzdiz.singleton;

import org.uzdiz.composite.VrstaVlaka;

public class PostavkeCijena {
    private static PostavkeCijena instance;

    private double cijenaNormalni;
    private double cijenaUbrzani;
    private double cijenaBrzi;
    private double popustSuN;
    private double popustWebMob;
    private double uvecanjeVlak;

    private PostavkeCijena() {
    }

    public static PostavkeCijena getInstance() {
        if (instance == null) {
            instance = new PostavkeCijena();
        }
        return instance;
    }

    public void postaviCijene(double normalni, double ubrzani, double brzi) {
        this.cijenaNormalni = normalni;
        this.cijenaUbrzani = ubrzani;
        this.cijenaBrzi = brzi;
    }

    public void postaviPopuste(double suN, double webMob) {
        this.popustSuN = suN;
        this.popustWebMob = webMob;
    }

    public void postaviUvecanje(double uvecanje) {
        this.uvecanjeVlak = uvecanje;
    }

    public double dohvatiCijenu(VrstaVlaka vrsta) {
        return switch (vrsta) {
            case NORMALNI -> cijenaNormalni;
            case UBRZANI -> cijenaUbrzani;
            case BRZI -> cijenaBrzi;
        };
    }

    public double dohvatiPopustSuN() {
        return popustSuN;
    }

    public double dohvatiPopustWebMob() {
        return popustWebMob;
    }

    public double dohvatiUvecanjeVlak() {
        return uvecanjeVlak;
    }
}
