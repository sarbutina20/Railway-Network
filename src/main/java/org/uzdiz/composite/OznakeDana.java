package org.uzdiz.composite;


import java.util.*;
import java.util.stream.Collectors;

public enum OznakeDana {
    PONEDJELJAK("Po"),
    UTORAK("U"),
    SRIJEDA("Sr"),
    CETVRTAK("Č"),
    PETAK("Pe"),
    SUBOTA("Su"),
    NEDJELJA("N");

    private final String code;

    private static final Map<String, OznakeDana> CODE_MAP =
            Arrays.stream(values()).collect(Collectors.toMap(OznakeDana::getCode, od -> od));

    private static Map<Integer, Set<OznakeDana>> dynamicMappings = new HashMap<>();

    OznakeDana(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static List<OznakeDana> loadMappings(List<Map.Entry<Integer, String>> rawMappings) {
        dynamicMappings = rawMappings.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> parseDayCodes(entry.getValue())
                ));
        return dynamicMappings.values().stream()
                .flatMap(Set::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private static Set<OznakeDana> parseDayCodes(String dayCodes) {
        if (dayCodes == null || dayCodes.isEmpty()) {
            return EnumSet.allOf(OznakeDana.class);
        }

        Set<OznakeDana> result = new HashSet<>();
        for (int i = 0; i < dayCodes.length(); i++) {
            if (i < dayCodes.length() - 1) {
                String twoCharCode = dayCodes.substring(i, i + 2);
                OznakeDana dan = findDayByCode(twoCharCode);
                if (dan != null) {
                    result.add(dan);
                    i++;
                    continue;
                }
            }

            String charCode = String.valueOf(dayCodes.charAt(i));
            OznakeDana dan = findDayByCode(charCode);
            if (dan != null) {
                result.add(dan);
            }
        }

        return result;
    }

    private static OznakeDana findDayByCode(String code) {
        return Arrays.stream(OznakeDana.values())
                .filter(d -> d.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public static Set<OznakeDana> fromCompositeCode(String compositeCode) {
        if (compositeCode == null || compositeCode.trim().isEmpty()) {
            return EnumSet.allOf(OznakeDana.class);
        }
        try {
            int key = Integer.parseInt(compositeCode);
            return dynamicMappings.getOrDefault(key, EnumSet.noneOf(OznakeDana.class));
        } catch (NumberFormatException e) {
            return parseDayCodes(compositeCode);
        }
    }

    public static String toCompositeCode(Set<OznakeDana> days) {
        if (days == null || days.isEmpty()) {
            return "";
        }
        return days.stream()
                .map(OznakeDana::getCode)
                .collect(Collectors.joining());
    }

}