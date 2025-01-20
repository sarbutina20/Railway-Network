package org.uzdiz.factory;

import java.util.List;

public abstract class CSVLoader<T> {
    protected String putanjaDatoteke;

    private static int totalErrors = 0;

    public CSVLoader(String putanjaDatoteke) {
        this.putanjaDatoteke = putanjaDatoteke;
    }

    protected static void povecajBrojUkupnihGreski() {
        totalErrors++;
    }

    public static int dohvatiBrojUkupnihGreski() {
        return totalErrors;
    }

    public abstract List<T> loadCSV();
}