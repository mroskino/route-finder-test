package com.mroskino.routefinder.utility;

import com.mroskino.routefinder.model.document.CountryDocument;

import java.util.Comparator;

import static com.mroskino.routefinder.utility.CalculationUtility.calculateDistance;

public class CountryComparator implements Comparator<CountryDocument> {

    private final double[] destinationCoordinates;

    public CountryComparator(CountryDocument destination) {
        this.destinationCoordinates = destination.getCoordinates();
    }

    @Override
    public int compare(CountryDocument cd1, CountryDocument cd2) {

        double difference = calculateDistance(cd1.getCoordinates(), destinationCoordinates)
                - calculateDistance(cd2.getCoordinates(), destinationCoordinates);

        if (difference > 0) return 1;
        if (difference < 0) return -1;

        return 0;
    }
}