package com.mroskino.routefinder.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mroskino.routefinder.exception.NoDataException;
import com.mroskino.routefinder.model.document.CountryDocument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.function.Function.identity;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
@Slf4j
public class CachedCountryService {

    @Value("${routes.source.url}")
    private String url;

    @Value("classpath:countries.json")
    Resource resourceFile;

    private Map<String, CountryDocument> cache;

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public CachedCountryService(ObjectMapper objectMapper, RestTemplate restTemplate) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
    }

    public CountryDocument getCountryByCode(String code) {
        return cache.get(code);
    }

    public boolean existsCountryByCode(String code) {
        return cache.containsKey(code);
    }

    @PostConstruct
    private void fetchCountries() throws JsonProcessingException {
        String response = null;

        try {
            response = restTemplate.getForEntity(url, String.class).getBody();
        } catch (RestClientException ex) {
            log.warn("Problem with fetching online data occurred.");
        }

        if (isEmpty(response)) {
            log.warn("No online data. Going to use offline copy.");
            try (Reader reader = new InputStreamReader(resourceFile.getInputStream(), UTF_8)) {
                response = FileCopyUtils.copyToString(reader);
            } catch (IOException e) {
                log.error("Problem with reading offline data occurred.");
                throw new NoDataException();
            }
        }

        cache = objectMapper.readValue(response, new TypeReference<List<CountryDocument>>(){}).stream()
                .collect(Collectors.toMap(CountryDocument::getCode, identity()));
    }
}
