package com.mroskino.routefinder.utility;

import com.mroskino.routefinder.model.document.CountryDocument;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculationUtility {

    public static double calculateDistance(double[] origin, double[] destination) {
        double latitudeDistance = destination[0] - origin[0];
        double lengthDistance = Math.abs(destination[1] - origin[1]) > 180.0
                ? destination[1] - origin[1] - 360.0
                : destination[1] - origin[1];

        return Math.sqrt(latitudeDistance * latitudeDistance + lengthDistance * lengthDistance);
    }

    public static void maintainTravels(Map<String, double[]> countryPrices, List<CountryDocument> neighbors,
                                       CountryDocument origin, CountryDocument destination) {

        for (CountryDocument countryDocument : neighbors) {
            if (countryPrices.containsKey(countryDocument.getCode())) {
                var costs = countryPrices.get(countryDocument.getCode());
                var dist = CalculationUtility.calculateDistance(origin.getCoordinates(), countryDocument.getCoordinates());
                if (dist < costs[0]) {
                    costs[0] = dist + countryPrices.get(origin.getCode())[0];
                    costs[2] = costs[0] + costs[1];
                }
            } else {
                var costs = new double[3];
                costs[0] = CalculationUtility.calculateDistance(origin.getCoordinates(), countryDocument.getCoordinates()) + countryPrices.get(origin.getCode())[0];
                costs[1] = CalculationUtility.calculateDistance(destination.getCoordinates(), countryDocument.getCoordinates());
                costs[2] = costs[0] + costs[1];
                countryPrices.put(countryDocument.getCode(), costs);
            }
        }
    }

    public static CountryDocument findBestNext(Set<CountryDocument> openedCountries, Map<String, double[]> countryPrices) {
        var min = Double.MAX_VALUE;
        CountryDocument bestCountry = null;

        for (CountryDocument openedCountry : openedCountries) {
            var price = countryPrices.get(openedCountry.getCode())[2];

            if (price < min) {
                min = price;
                bestCountry = openedCountry;
            }
        }

        return bestCountry;
    }


}
