package org.uzdiz.builder;

public class Vozilo {
    private final String oznaka;
    private final String opis;
    private final String proizvodjac;
    private final int godina;
    private final String namjena;
    private final String vrstaPrijevoza;
    private final String vrstaPogona;
    private final double maksBrzina;
    private final double maksSnaga;
    private final int brojSjedecihMjesta;
    private final int brojStajecihMjesta;
    private final int brojBicikala;
    private final int brojKreveta;
    private final int brojAutomobila;
    private final double nosivost;
    private final double povrsina;
    private final double zapremina;
    private final String status;

    private Vozilo(Builder builder) {
        this.oznaka = builder.oznaka;
        this.opis = builder.opis;
        this.proizvodjac = builder.proizvodjac;
        this.godina = builder.godina;
        this.namjena = builder.namjena;
        this.vrstaPrijevoza = builder.vrstaPrijevoza;
        this.vrstaPogona = builder.vrstaPogona;
        this.maksBrzina = builder.maksBrzina;
        this.maksSnaga = builder.maksSnaga;
        this.brojSjedecihMjesta = builder.brojSjedecihMjesta;
        this.brojStajecihMjesta = builder.brojStajecihMjesta;
        this.brojBicikala = builder.brojBicikala;
        this.brojKreveta = builder.brojKreveta;
        this.brojAutomobila = builder.brojAutomobila;
        this.nosivost = builder.nosivost;
        this.povrsina = builder.povrsina;
        this.zapremina = builder.zapremina;
        this.status = builder.status;
    }

    public static class Builder {
        private String oznaka;
        private String opis;
        private String proizvodjac;
        private int godina;
        private String namjena;
        private String vrstaPrijevoza;
        private String vrstaPogona;
        private double maksBrzina;
        private double maksSnaga;
        private int brojSjedecihMjesta;
        private int brojStajecihMjesta;
        private int brojBicikala;
        private int brojKreveta;
        private int brojAutomobila;
        private double nosivost;
        private double povrsina;
        private double zapremina;
        private String status;

        public Builder oznaka(String oznaka) {
            this.oznaka = oznaka;
            return this;
        }

        public Builder opis(String opis) {
            this.opis = opis;
            return this;
        }

        public Builder proizvodjac(String proizvodjac) {
            this.proizvodjac = proizvodjac;
            return this;
        }

        public Builder godina(int godina) {
            this.godina = godina;
            return this;
        }

        public Builder namjena(String namjena) {
            this.namjena = namjena;
            return this;
        }

        public Builder vrstaPrijevoza(String vrstaPrijevoza) {
            this.vrstaPrijevoza = vrstaPrijevoza;
            return this;
        }

        public Builder vrstaPogona(String vrstaPogona) {
            this.vrstaPogona = vrstaPogona;
            return this;
        }

        public Builder maksBrzina(double maksBrzina) {
            this.maksBrzina = maksBrzina;
            return this;
        }

        public Builder maksSnaga(double maksSnaga) {
            this.maksSnaga = maksSnaga;
            return this;
        }

        public Builder brojSjedecihMjesta(int brojSjedecihMjesta) {
            this.brojSjedecihMjesta = brojSjedecihMjesta;
            return this;
        }

        public Builder brojStajecihMjesta(int brojStajecihMjesta) {
            this.brojStajecihMjesta = brojStajecihMjesta;
            return this;
        }

        public Builder brojBicikala(int brojBicikala) {
            this.brojBicikala = brojBicikala;
            return this;
        }

        public Builder brojKreveta(int brojKreveta) {
            this.brojKreveta = brojKreveta;
            return this;
        }

        public Builder brojAutomobila(int brojAutomobila) {
            this.brojAutomobila = brojAutomobila;
            return this;
        }

        public Builder nosivost(double nosivost) {
            this.nosivost = nosivost;
            return this;
        }

        public Builder povrsina(double povrsina) {
            this.povrsina = povrsina;
            return this;
        }

        public Builder zapremina(double zapremina) {
            this.zapremina = zapremina;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Vozilo build() {
            return new Vozilo(this);
        }
    }
}