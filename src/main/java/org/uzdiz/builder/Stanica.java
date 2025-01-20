package org.uzdiz.builder;

public class Stanica {
    private String naziv;
    private String oznakaPruge;
    private String vrstaStanice;
    private String statusStanice;
    private boolean putniciUlIz;
    private boolean robaUtIst;
    private String kategorijaPruge;
    private int brojPerona;
    private String vrstaPruge;
    private int brojKolosjeka;
    private double doPoOsovini;
    private double doPoDuznomM;
    private String statusPruge;
    private int duzina;
    private Integer vrijemeNormalniVlak;
    private Integer vrijemeUbrzaniVlak;
    private Integer vrijemeBrziVlak;

    private Stanica(Builder builder) {
        this.naziv = builder.naziv;
        this.oznakaPruge = builder.oznakaPruge;
        this.vrstaStanice = builder.vrstaStanice;
        this.statusStanice = builder.statusStanice;
        this.putniciUlIz = builder.putniciUlIz;
        this.robaUtIst = builder.robaUtIst;
        this.kategorijaPruge = builder.kategorijaPruge;
        this.brojPerona = builder.brojPerona;
        this.vrstaPruge = builder.vrstaPruge;
        this.brojKolosjeka = builder.brojKolosjeka;
        this.doPoOsovini = builder.doPoOsovini;
        this.doPoDuznomM = builder.doPoDuznomM;
        this.statusPruge = builder.statusPruge;
        this.duzina = builder.duzina;
        this.vrijemeNormalniVlak = builder.vrijemeNormalniVlak;
        this.vrijemeUbrzaniVlak = builder.vrijemeUbrzaniVlak;
        this.vrijemeBrziVlak = builder.vrijemeBrziVlak;
    }

    public String getNaziv() {
        return naziv;
    }

    public String getOznakaPruge() {
        return oznakaPruge;
    }

    public Integer getDuzina() {
        return duzina;
    }

    public String getVrstaStanice() {
        return vrstaStanice;
    }

    public Integer getVrijemeNormalniVlak() {
        return vrijemeNormalniVlak;
    }

    public Integer getVrijemeUbrzaniVlak() {
        return vrijemeUbrzaniVlak;
    }

    public Integer getVrijemeBrziVlak() {
        return vrijemeBrziVlak;
    }

    public static class Builder {
        private String naziv;
        private String oznakaPruge;
        private String vrstaStanice;
        private String statusStanice;
        private boolean putniciUlIz;
        private boolean robaUtIst;
        private String kategorijaPruge;
        private int brojPerona;
        private String vrstaPruge;
        private int brojKolosjeka;
        private double doPoOsovini;
        private double doPoDuznomM;
        private String statusPruge;
        private int duzina;

        private Integer vrijemeNormalniVlak = 0;
        private Integer vrijemeUbrzaniVlak;
        private Integer vrijemeBrziVlak;

        public Builder naziv(String naziv) {
            this.naziv = naziv;
            return this;
        }

        public Builder oznakaPruge(String oznakaPruge) {
            this.oznakaPruge = oznakaPruge;
            return this;
        }

        public Builder vrstaStanice(String vrstaStanice) {
            this.vrstaStanice = vrstaStanice;
            return this;
        }

        public Builder statusStanice(String statusStanice) {
            this.statusStanice = statusStanice;
            return this;
        }

        public Builder putniciUlIz(boolean putniciUlIz) {
            this.putniciUlIz = putniciUlIz;
            return this;
        }

        public Builder robaUtIst(boolean robaUtIst) {
            this.robaUtIst = robaUtIst;
            return this;
        }

        public Builder kategorijaPruge(String kategorijaPruge) {
            this.kategorijaPruge = kategorijaPruge;
            return this;
        }

        public Builder brojPerona(int brojPerona) {
            this.brojPerona = brojPerona;
            return this;
        }

        public Builder vrstaPruge(String vrstaPruge) {
            this.vrstaPruge = vrstaPruge;
            return this;
        }

        public Builder brojKolosjeka(int brojKolosjeka) {
            this.brojKolosjeka = brojKolosjeka;
            return this;
        }

        public Builder doPoOsovini(double doPoOsovini) {
            this.doPoOsovini = doPoOsovini;
            return this;
        }

        public Builder doPoDuznomM(double doPoDuznomM) {
            this.doPoDuznomM = doPoDuznomM;
            return this;
        }

        public Builder statusPruge(String statusPruge) {
            this.statusPruge = statusPruge;
            return this;
        }

        public Builder duzina(int duzina) {
            this.duzina = duzina;
            return this;
        }


        public Builder vrijemeNormalniVlak(Integer vrijemeNormalniVlak) {
            this.vrijemeNormalniVlak = vrijemeNormalniVlak;
            return this;
        }

        public Builder vrijemeUbrzaniVlak(Integer vrijemeUbrzaniVlak) {
            this.vrijemeUbrzaniVlak = vrijemeUbrzaniVlak;
            return this;
        }

        public Builder vrijemeBrziVlak(Integer vrijemeBrziVlak) {
            this.vrijemeBrziVlak = vrijemeBrziVlak;
            return this;
        }

        public Stanica build() {
            return new Stanica(this);
        }


    }
}
