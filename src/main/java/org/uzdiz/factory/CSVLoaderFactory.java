package org.uzdiz.factory;

public abstract class CSVLoaderFactory {
    protected String filePath;

    public CSVLoaderFactory(String filePath) {
        this.filePath = filePath;
    }

    public abstract CSVLoader createLoader();
}