package com.mroskino.routefinder.service;

import com.mroskino.routefinder.exception.InvalidCountryException;
import com.mroskino.routefinder.exception.NoRouteFoundException;
import com.mroskino.routefinder.model.document.CountryDocument;
import com.mroskino.routefinder.model.response.RouteResponse;
import com.mroskino.routefinder.utility.CountryComparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteFinderService {

    private final CachedCountryService cachedCountryService;

    public RouteFinderService(CachedCountryService cachedCountryService) {
        this.cachedCountryService = cachedCountryService;
    }

    public RouteResponse findRoute(String origin, String destination) {
        validateInputCountryCode(origin);
        validateInputCountryCode(destination);

        var originCountry = cachedCountryService.getCountryByCode(origin);
        var destinationCountry = cachedCountryService.getCountryByCode(destination);

        var comparator = new CountryComparator(destinationCountry);
        var route = new ArrayList<String>();
        var visitedCountries = new HashSet<String>();

        if (!search(originCountry, destinationCountry, comparator, route, visitedCountries)) {
            throw new NoRouteFoundException();
        }

        route.add(originCountry.getCode());
        Collections.reverse(route);

        return RouteResponse.builder()
                .route(route)
                .build();
    }

    private boolean search(CountryDocument origin, CountryDocument destination,
                           Comparator<CountryDocument> comparator, List<String> route,
                           Set<String> visitedCountries) {

        if (origin.getBorders().contains(destination.getCode())) {
            route.add(destination.getCode());
            return true;
        }

        visitedCountries.add(origin.getCode());

        Queue<CountryDocument> queue = new PriorityQueue<>(comparator);
        queue.addAll(origin.getBorders().stream()
                .filter(code -> !visitedCountries.contains(code))
                .map(cachedCountryService::getCountryByCode)
                .collect(Collectors.toList()));

        while (!queue.isEmpty()) {
            CountryDocument neighborCountry = queue.poll();

            if (search(neighborCountry, destination, comparator, route, visitedCountries)) {
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

}