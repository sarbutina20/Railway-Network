package org.uzdiz.factory;

public class DayTagCSVLoaderFactory extends CSVLoaderFactory {
    public DayTagCSVLoaderFactory(String filePath) {
        super(filePath);
    }

    @Override
    public CSVLoader createLoader() {
        return new DayTagCSVLoader(filePath);
    }
}
