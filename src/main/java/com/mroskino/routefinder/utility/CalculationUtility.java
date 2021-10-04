package com.mroskino.routefinder.utility;

import com.mroskino.routefinder.model.entity.Country;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Double.MAX_VALUE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculationUtility {

    public static double calculateDistance(double[] origin, double[] destination) {
        double latitudeDistance = destination[0] - origin[0];
        double lengthDistance = Math.abs(destination[1] - origin[1]) > 180.0
                ? destination[1] - origin[1] - 360.0
                : destination[1] - origin[1];

        return Math.sqrt(latitudeDistance * latitudeDistance + lengthDistance * lengthDistance);
    }

    public static void maintainCountryPrices(Map<String, double[]> countryPrices, List<Country> neighbors,
                                             Country origin, Country destination) {

        for (Country country : neighbors) {
            if (countryPrices.containsKey(country.getCode())) {
                var costs = countryPrices.get(country.getCode());
                var dist = calculateDistance(origin.getCoordinates(), country.getCoordinates());
                if (dist < costs[0]) {
                    costs[0] = dist + countryPrices.get(origin.getCode())[0];
                    costs[2] = costs[0] + costs[1];
                }
            } else {
                var costs = new double[3];
                costs[0] = calculateDistance(origin.getCoordinates(), country.getCoordinates())
                        + countryPrices.get(origin.getCode())[0];
                costs[1] = calculateDistance(destination.getCoordinates(), country.getCoordinates());
                costs[2] = costs[0] + costs[1];
                countryPrices.put(country.getCode(), costs);
            }
        }
    }

    public static Country findBestNext(Set<Country> openedCountries, Map<String, double[]> countryPrices) {
        var min = MAX_VALUE;
        Country bestCountry = null;

        for (Country openedCountry : openedCountries) {
            var price = countryPrices.get(openedCountry.getCode())[2];

            if (price < min) {
                min = price;
                bestCountry = openedCountry;
            }
        }

        return bestCountry;
    }


}
