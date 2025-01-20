package org.uzdiz.factory;

public class TrainScheduleCSVLoaderFactory extends CSVLoaderFactory {
    public TrainScheduleCSVLoaderFactory(String filePath) {
        super(filePath);
    }

    @Override
    public CSVLoader createLoader() {
        return new TrainScheduleCSVLoader(filePath);
    }
}

