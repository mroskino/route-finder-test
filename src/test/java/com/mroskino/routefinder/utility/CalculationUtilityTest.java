package com.mroskino.routefinder.utility;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculationUtilityTest {

    @Test
    void testDistanceCalculationForCoordinateDistanceLessThan180() {
        double[] origin = new double[2];
        origin[0] = 1.0;
        origin[1] = 1.0;
        double[] destination = new double[2];
        destination[0] = 2.0;
        destination[1] = 2.0;

        double distance = CalculationUtility.calculateDistance(origin, destination);

        assertEquals(Math.sqrt(2), distance);
    }

    @Test
    void testDistanceCalculationForCoordinateDistanceMoreThan180() {
        double[] origin = new double[2];
        origin[0] = 0.0;
        origin[1] = -175.0;
        double[] destination = new double[2];
        destination[0] = 0.0;
        destination[1] = 165.0;

        double distance = CalculationUtility.calculateDistance(origin, destination);

        assertEquals(20.0, distance);
    }
}
