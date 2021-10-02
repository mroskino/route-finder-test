package com.mroskino.routefinder.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RouteFinderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void whenFindRoute_andInvalidCountryProvided_thenReturn400() throws Exception {
        mockMvc.perform(get("/routing/XXX/AAA"))
                .andExpect(status().is(400));
    }

    @Test
    void whenFindRoute_andTheSameCountriesProvided_thenReturn400() throws Exception {
        mockMvc.perform(get("/routing/CZE/CZE"))
                .andExpect(status().is(400));
    }

    @Test
    void whenFindRoute_andNoRouteCountriesProvided_thenReturn400() throws Exception {
        mockMvc.perform(get("/routing/CZE/ABW"))
                .andExpect(status().is(400));
    }

    @Test
    void whenFindRoute_andValidCountriesProvided_thenReturn200() throws Exception {
        mockMvc.perform(get("/routing/CZE/BEL"))
                .andExpect(status().is(200))
                .andExpect(content().json("{\"route\":[\"CZE\",\"DEU\",\"BEL\"]}"));
    }

}
