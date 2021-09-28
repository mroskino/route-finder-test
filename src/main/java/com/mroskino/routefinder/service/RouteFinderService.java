package com.mroskino.routefinder.service;

import com.mroskino.routefinder.model.document.CountryDocument;
import com.mroskino.routefinder.model.response.RouteResponse;
import org.springframework.stereotype.Service;

@Service
public class RouteFinderService {

    private final CachedCountryService cachedCountryService;

    public RouteFinderService(CachedCountryService cachedCountryService) {
        this.cachedCountryService = cachedCountryService;
    }

    public RouteResponse findRoute(String origin, String destination) {

        CountryDocument countryDocument = cachedCountryService.getCountryByCode(origin);

        return RouteResponse.builder()
                .route(countryDocument.getBorders())
                .build();
    }




}
