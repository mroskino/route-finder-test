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
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
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

        CountryDocument originCountryDocument = cachedCountryService.getCountryByCode(origin);
        CountryDocument destinationCountryDocument = cachedCountryService.getCountryByCode(destination);

        var comparator = buildCountryComparator(destinationCountryDocument);
        var route = new ArrayList<String>();

        if (!expand(originCountryDocument, destinationCountryDocument, comparator, route)) {
            throw new NoRouteFoundException();
        }

        route.add(originCountryDocument.getCode());
        Collections.reverse(route);

        return RouteResponse.builder()
                .route(route)
                .build();
    }

    private boolean expand(CountryDocument origin, CountryDocument destination, Comparator<CountryDocument> comparator, List<String> route) {

        if (origin.getBorders().contains(destination.getCode())) {
            route.add(destination.getCode());
            return true;
        }

        Queue<CountryDocument> queue = new PriorityQueue<>(comparator);
        queue.addAll(origin.getBorders().stream()
                .map(cachedCountryService::getCountryByCode)
                .collect(Collectors.toList()));

        while (!queue.isEmpty()) {

            CountryDocument neighborCountry = queue.poll();

            if (expand(neighborCountry, destination, comparator, route)) {
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

    private Comparator<CountryDocument> buildCountryComparator(CountryDocument destination) {
        return new CountryComparator(destination);
    }

}