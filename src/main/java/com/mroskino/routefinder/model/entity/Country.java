package com.mroskino.routefinder.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "code")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {

    @JsonProperty("cca3")
    private String code;

    private Set<String> borders;

    @JsonProperty("latlng")
    private double[] coordinates;
}
