package org.uzdiz.chain_of_responsibility;

import org.uzdiz.composite.KomponentaVoznogReda;
import org.uzdiz.observer.UpraviteljPutovanjaVlakom;
import org.uzdiz.observer.Korisnik;
import org.uzdiz.singleton.HrvatskeZeljeznice;

public class DPKHandler extends CommandHandler {

    private final HrvatskeZeljeznice hrvatskeZeljeznice;

    public DPKHandler(HrvatskeZeljeznice hrvatskeZeljeznice) {
        this.hrvatskeZeljeznice = hrvatskeZeljeznice;
    }

    @Override
    public void handle(String command, String[] commandParts) {
        if (command.equalsIgnoreCase("DPK")) {
            String[] dpkParts = String.join(" ", commandParts).split(" - ");

            if (dpkParts.length < 2 || dpkParts.length > 4) {
                System.out.println("Upotreba: DPK <ime> <prezime> - <oznakaVlaka> [- <stanica>]");
                return;
            }

            String userName = dpkParts[0].split(" ", 2)[1];
            String trainId = dpkParts[1];
            String stationName = dpkParts.length > 2 ? dpkParts[2] : null;

            handleDPK(userName, trainId, stationName);
        } else {
            super.handle(command, commandParts);
        }
    }

    private void handleDPK(String userName, String trainId, String stationName) {
        Korisnik korisnik = Korisnik.dohvatiKorisnikaPoImenu(userName);
        if (korisnik == null) {
            System.out.println("Pogreška: Korisnik " + userName + " nije u registru korisnika. Iskoriste naredbu DK kako bi dodali.");
            return;
        }

        KomponentaVoznogReda trainExists = hrvatskeZeljeznice.getVlakovi().stream()
                .filter(t -> t.dohvatiOznaku().equalsIgnoreCase(trainId))
                .findFirst()
                .orElse(null);

        if (trainExists == null) {
            System.out.println("Pogreška: Vlak s oznakom " + trainId + " ne postoji.");
            return;
        }

        UpraviteljPutovanjaVlakom journeyManager = (UpraviteljPutovanjaVlakom) hrvatskeZeljeznice.getJourneyManager();


        if (stationName != null) {
            trainExists.dohvatiSveStanice().stream()
                    .filter(station -> station.getNaziv().equalsIgnoreCase(stationName))
                    .findFirst()
                    .ifPresentOrElse(station -> {
                        if (journeyManager.postojiObserverZaStanicu(stationName, korisnik)) {
                            System.out.println("Korisnik " + userName + " je već pretplaćen na stanicu " + stationName + ".");
                        } else {
                            if (journeyManager.postojiObserverZaVlak(trainId, korisnik)) {
                                journeyManager.makniObserverZaVlak(trainId, korisnik);
                            }
                            journeyManager.dodajObserverZaStanicu(stationName, korisnik);
                            System.out.println("Korisnik " + userName + " se pretplatio na stanicu " + stationName + ".");
                        }
                    }, () -> System.out.println("Pogreška: Stanica s imenom " + stationName + " nije pronađena na ruti vlaka " + trainId + "."));
        } else {
            if (journeyManager.postojiObserverZaVlak(trainId, korisnik)) {
                System.out.println("Korisnik " + userName + " je već pretplaćen na vlak " + trainId + ".");
            } else {
                trainExists.dohvatiSveStanice().forEach(station -> {
                    if (journeyManager.postojiObserverZaStanicu(station.getNaziv(), korisnik)) {
                        journeyManager.makniObserverZaStanicu(station.getNaziv(), korisnik);
                    }
                });
                journeyManager.dodajObserverZaVlak(trainId, korisnik);
                System.out.println("Korisnik " + userName + " se pretplatio na vlak " + trainId + ".");
            }
        }

    }
}
