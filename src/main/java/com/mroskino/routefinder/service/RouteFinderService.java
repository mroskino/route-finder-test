package com.mroskino.routefinder.service;

import com.mroskino.routefinder.model.document.CountryDocument;
import com.mroskino.routefinder.model.response.RouteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class RouteFinderService {

    @Value("${routes.source.url}")
    private String url;

    private final RestTemplate restTemplate;

    public RouteFinderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public RouteResponse findRoute(String origin, String destination) {

        List<CountryDocument> countryDocuments = fetchCountries();

        return RouteResponse.builder()
                .route(List.of(origin, destination))
                .build();
    }

    public List<CountryDocument> fetchCountries() {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//        ResponseEntity<List<CountryDocument>> response = restTemplate
//                .exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});

//        return response.getBody();

        ResponseEntity<String> response = restTemplate
                .getForEntity(url, String.class);

        return null;

    }


}
