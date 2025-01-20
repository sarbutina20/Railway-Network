package org.uzdiz.factory;

public class VehiclesCSVLoaderFactory extends CSVLoaderFactory {
    public VehiclesCSVLoaderFactory(String filePath) {
        super(filePath);
    }

    @Override
    public CSVLoader createLoader() {
        return new VehiclesCSVLoader(filePath);
    }
}
