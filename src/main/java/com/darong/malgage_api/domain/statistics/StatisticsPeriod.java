package com.darong.malgage_api.domain.statistics;

import java.util.Arrays;

public enum StatisticsPeriod {
    MONTHLY("monthly"),
    YEARLY("yearly");

    private final String value;

    StatisticsPeriod(String value) {
        this.value = value;
    }

    public static StatisticsPeriod from(String value) {
        return Arrays.stream(values())
                .filter(p -> p.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid statistics type: " + value));
    }
}
