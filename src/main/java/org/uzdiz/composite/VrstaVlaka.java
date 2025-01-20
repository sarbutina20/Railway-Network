package org.uzdiz.composite;

public enum VrstaVlaka {
    NORMALNI("N"),
    UBRZANI("U"),
    BRZI("B");

    private final String code;

    VrstaVlaka(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VrstaVlaka fromCode(String code) {
        for (VrstaVlaka vrsta : values()) {
            if (vrsta.getCode().equalsIgnoreCase(code)) {
                return vrsta;
            }
        }
        return NORMALNI;
    }
}