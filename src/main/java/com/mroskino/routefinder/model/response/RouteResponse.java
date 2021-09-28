package com.mroskino.routefinder.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class RouteResponse {
    private List<String> route;
}
