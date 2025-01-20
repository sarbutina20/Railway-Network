package org.uzdiz.factory;

public class RailwaysCSVLoaderFactory extends CSVLoaderFactory {
    public RailwaysCSVLoaderFactory(String filePath) {
        super(filePath);
    }

    @Override
    public CSVLoader createLoader() {
        return new RailwaysCSVLoader(filePath);
    }
}

