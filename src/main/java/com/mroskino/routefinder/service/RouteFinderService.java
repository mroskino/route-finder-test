package com.mroskino.routefinder.service;

import com.mroskino.routefinder.exception.InvalidCountryException;
import com.mroskino.routefinder.exception.NoRouteFoundException;
import com.mroskino.routefinder.model.entity.Country;
import com.mroskino.routefinder.model.response.RouteResponse;
import com.mroskino.routefinder.utility.CountryComparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import static com.mroskino.routefinder.utility.CalculationUtility.findBestNext;
import static com.mroskino.routefinder.utility.CalculationUtility.maintainCountryPrices;
import static java.lang.Double.MAX_VALUE;

@Service
@Slf4j
public class RouteFinderService {

    private final CachedCountryService cachedCountryService;

    public RouteFinderService(CachedCountryService cachedCountryService) {
        this.cachedCountryService = cachedCountryService;
    }

    public RouteResponse findRoute(String origin, String destination) {
        validateNonEqual(origin, destination);
        validateInputCountryCode(origin);
        validateInputCountryCode(destination);

        var originCountry = cachedCountryService.getCountryByCode(origin);
        var destinationCountry = cachedCountryService.getCountryByCode(destination);
        var visitedCountries = new HashSet<String>();
        var openedCountries = new HashSet<Country>();
        var countryPrices = new HashMap<String, double[]>();

        countryPrices.put(originCountry.getCode(), new double[]{0.0, 0.0, MAX_VALUE});

        if (!searchRecursive(originCountry, destinationCountry, visitedCountries, countryPrices, openedCountries)) {
            throw new NoRouteFoundException("No route found.");
        }

        var route = new ArrayList<String>();
        var queue = new PriorityQueue<>(new CountryComparator(countryPrices));

        backTrackRecursive(queue, visitedCountries, destinationCountry.getBorders(), originCountry, route);

        route.add(destinationCountry.getCode());

        return RouteResponse.builder()
                .route(route)
                .build();
    }

    private boolean searchRecursive(Country origin, Country destination,
                                    Set<String> visitedCountries, Map<String, double[]> countryPrices,
                                    Set<Country> openedCountries) {

        visitedCountries.add(origin.getCode());

        if (origin.getBorders().contains(destination.getCode())) {
            return true;
        }

        var unvisitedNeighbors = origin.getBorders().stream()
                .filter(code -> !visitedCountries.contains(code))
                .map(cachedCountryService::getCountryByCode)
                .collect(Collectors.toList());

        if (unvisitedNeighbors.isEmpty()) {
            return false;
        }

        openedCountries.addAll(unvisitedNeighbors);
        maintainCountryPrices(countryPrices, unvisitedNeighbors, origin, destination);

        while (!openedCountries.isEmpty()) {
            var neighborCountry = findBestNext(openedCountries, countryPrices);
            openedCountries.remove(neighborCountry);

            if (searchRecursive(neighborCountry, destination, visitedCountries, countryPrices, openedCountries)) {
                return true;
            }
        }

        return false;
    }

    public boolean backTrackRecursive(Queue<String> queue, Set<String> visitedCountries,
                                      Set<String> borders, Country destination,
                                      List<String> route) {

        if (borders.contains(destination.getCode())) {
            route.add(destination.getCode());
            return true;
        }

        var neighbors = borders.stream()
                .filter(visitedCountries::contains)
                .collect(Collectors.toList());

        if (neighbors.isEmpty()) {
            return false;
        }

        neighbors.forEach(visitedCountries::remove);
        queue.addAll(neighbors);

        while (!queue.isEmpty()) {

            var country = cachedCountryService.getCountryByCode(queue.poll());

            if (backTrackRecursive(queue, visitedCountries, country.getBorders(), destination, route)) {
                route.add(country.getCode());
                return true;
            }
        }

        return false;
    }

    private void validateInputCountryCode(String code) {
        if (!cachedCountryService.existsCountryByCode(code)) {
            throw new InvalidCountryException("Non existing country code " + code);
        }
    }

    private static void validateNonEqual(String origin, String destination) {
        if (origin.equals(destination)) {
            throw new InvalidCountryException("Origin and destination are the same");
        }
    }

}