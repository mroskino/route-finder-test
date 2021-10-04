package com.mroskino.routefinder.utility;

import java.util.Comparator;
import java.util.Map;

public class CountryComparator implements Comparator<String> {

    private final Map<String, double[]> countryPrices;

    public CountryComparator(Map<String, double[]> countryPrices) {
        this.countryPrices = countryPrices;
    }

    @Override
    public int compare(String country1, String country2) {

        double difference = countryPrices.get(country1)[0] - countryPrices.get(country2)[0];

        if (difference > 0) return 1;
        if (difference < 0) return -1;

        return 0;
    }
}