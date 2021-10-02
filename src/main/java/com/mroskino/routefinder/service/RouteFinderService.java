package com.mroskino.routefinder.service;

import com.mroskino.routefinder.exception.InvalidCountryException;
import com.mroskino.routefinder.exception.NoRouteFoundException;
import com.mroskino.routefinder.model.document.CountryDocument;
import com.mroskino.routefinder.model.response.RouteResponse;
import com.mroskino.routefinder.utility.CountryComparator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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

        var comparator = new CountryComparator(destinationCountry);
        var route = new ArrayList<String>();
        var visitedCountries = new HashSet<String>();
        var queue = new PriorityQueue<>(comparator);

        visitedCountries.add(origin);

        if (!searchRecursive(originCountry, destinationCountry, route, visitedCountries, queue)) {
            throw new NoRouteFoundException("No route found.");
        }

        route.add(originCountry.getCode());
        Collections.reverse(route);

        return RouteResponse.builder()
                .route(route)
                .build();
    }

    private boolean searchRecursive(CountryDocument origin, CountryDocument destination, List<String> route,
                                    Set<String> visitedCountries, Queue<CountryDocument> queue) {

        if (origin.getBorders().contains(destination.getCode())) {
            route.add(destination.getCode());
            return true;
        }

        var unvisitedNeighbors = origin.getBorders().stream()
                .filter(code -> !visitedCountries.contains(code))
                .map(cachedCountryService::getCountryByCode)
                .collect(Collectors.toList());

        if (unvisitedNeighbors.isEmpty()) {
            return false;
        }

        visitedCountries.addAll(unvisitedNeighbors.stream()
                .map(CountryDocument::getCode)
                .collect(Collectors.toSet()));

        queue.addAll(unvisitedNeighbors);

        while (!queue.isEmpty()) {
            var neighborCountry = queue.poll();

            if (searchRecursive(neighborCountry, destination, route, visitedCountries, queue)) {
                route.add(neighborCountry.getCode());
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