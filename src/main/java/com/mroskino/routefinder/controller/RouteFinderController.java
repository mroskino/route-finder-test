package com.mroskino.routefinder.controller;

import com.mroskino.routefinder.model.response.RouteResponse;
import com.mroskino.routefinder.service.RouteFinderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RouteFinderController {

    private final RouteFinderService routeFinderService;

    public RouteFinderController(RouteFinderService routeFinderService) {
        this.routeFinderService = routeFinderService;
    }

    @GetMapping("/routing/{origin}/{destination}")
    public ResponseEntity<RouteResponse> findRoute(@PathVariable String origin, @PathVariable String destination) {
        return ResponseEntity.ok(routeFinderService.findRoute(origin, destination));
    }
}
