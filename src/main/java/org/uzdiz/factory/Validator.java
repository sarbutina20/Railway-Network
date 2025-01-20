package org.uzdiz.factory;

public class Validator {
    private int index;
    private String regex;
    private String description;

    public Validator(int index, String regex, String description) {
        this.index = index;
        this.regex = regex;
        this.description = description;
    }

    public boolean validate(String[] values) {
        return index < values.length && values[index].matches(regex);
    }

    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }
}
