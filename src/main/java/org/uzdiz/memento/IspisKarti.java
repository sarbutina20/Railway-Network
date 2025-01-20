package org.uzdiz.memento;

import java.time.format.DateTimeFormatter;

public class IspisKarti {
    public String formatirajPodatkeOKarti(KartaMemento karta) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy. H:mm");

        String formatiranoVrijemeKupovine = karta.getTrenutnoVrijeme().format(formatter);

        String formatiranDatumPutovanja = karta.getDatumPutovanja().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));

        String formatiranNacinPlacanja = switch (karta.getNacinKupovine()) {
            case "B" -> "Blagajna";
            case "WM" -> "Web/mobilna aplikacija";
            case "V" -> "Vlak";
            default -> "Nepoznat";
        };

        return String.format(
                "-----------------------------------\n" +
                        "K A R T A\n" +
                        "-----------------------------------\n" +
                        "Vlak: %s (%s)\n" +
                        "Relacija: %s\n" +
                        "Datum putovanja: %s\n" +
                        "Vrijeme polaska: %s\n" +
                        "Vrijeme dolaska: %s\n" +
                        "Način kupovine: %s\n" +
                        "Udaljenost: %.2f km\n" +
                        "Izvorna cijena: %.2f EUR\n" +
                        "Popusti: %.2f posto\n" +
                        "Konačna cijena: %.2f EUR\n" +
                        "Vrijeme kupovine: %s\n" +
                        "-----------------------------------",
                karta.getOznakaVlaka(), karta.getVrstaVlaka(), karta.getRelacija(), formatiranDatumPutovanja, karta.getVrijemePolaska(), karta.getVrijemeDolaska(), formatiranNacinPlacanja,
                karta.getUdaljenost(), karta.getIzvornaCijena(), karta.getPopusti(), karta.getKonacnaCijena(), formatiranoVrijemeKupovine
        );
    }
}
