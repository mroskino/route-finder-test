package com.mroskino.routefinder.utility;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CalculationUtility {

    public static double calculateDistance(double[] origin, double[] destination) {
        double latitudeDistance = destination[0] - origin[0];
        double lengthDistance = Math.abs(destination[1] - origin[1]) > 180.0
                ? destination[1] - origin[1] - 360.0
                : destination[1] - origin[1];

        return Math.sqrt(latitudeDistance * latitudeDistance + lengthDistance * lengthDistance);
    }
}
