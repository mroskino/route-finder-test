package com.mroskino.routefinder.service;

import com.mroskino.routefinder.exception.InvalidCountryException;
import com.mroskino.routefinder.model.document.CountryDocument;
import com.mroskino.routefinder.model.response.RouteResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.mroskino.routefinder.utility.CalculationUtility.calculateDistance;

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

        log.info("Calculated distance: {}", calculateDistance(
                originCountryDocument.getCoordinates(),
                destinationCountryDocument.getCoordinates()));

        return RouteResponse.builder()
                .route(originCountryDocument.getBorders())
                .build();
    }

    private void validateInputCountryCode(String code) {
        if (!cachedCountryService.existsCountryByCode(code)) {
            throw new InvalidCountryException("Non existing country code " + code);
        }
    }

}