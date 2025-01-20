package org.uzdiz.factory;

public class CompositionsCSVLoaderFactory extends CSVLoaderFactory {
    public CompositionsCSVLoaderFactory(String filePath) {
        super(filePath);
    }

    @Override
    public CSVLoader createLoader() {
        return new CompositionsCSVLoader(filePath);
    }
}