package org.uzdiz.command;

import org.uzdiz.singleton.PostavkeCijena;

public class DPPCommand implements DiscountCommand {

    private final String polazna;
    private final String odredisna;
    private final double popustZaPostaviti;

    private Double stariPopust;


    public DPPCommand (String polazna, String odredisna, double popust) {
        this.polazna = polazna;
        this.odredisna = odredisna;
        this.popustZaPostaviti = popust;
    }

    @Override
    public void execute() {
        PostavkeCijena postavke = PostavkeCijena.getInstance();
        stariPopust = postavke.dohvatiPrivremeniPopust(polazna, odredisna);
        postavke.postaviPrivremeniPopust(polazna, odredisna, popustZaPostaviti);
    }

    @Override
    public void undo() {
        PostavkeCijena postavke = PostavkeCijena.getInstance();
        if (stariPopust == 0.0) {
            postavke.ukloniPrivremeniPopust(polazna, odredisna);
        } else {
            postavke.postaviPrivremeniPopust(polazna, odredisna, stariPopust);
        }
    }
}
