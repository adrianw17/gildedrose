package com.example.gildedrose.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class InventoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void updatesInventorySuccessfully() throws Exception {
        String requestJson = """
                [
                    {"name":"Aged Brie","sellIn":1,"quality":1},
                    {"name":"Backstage passes","sellIn":9,"quality":2},
                    {"name":"Sulfuras","sellIn":0,"quality":80},
                    {"name":"Conjured","sellIn":-1,"quality":5}
                ]
                """;

        mockMvc.perform(post("/api/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(4))
                .andExpect(jsonPath("$[0].name").value("Aged Brie"))
                .andExpect(jsonPath("$[0].sellIn").value(0))
                .andExpect(jsonPath("$[0].quality").value(2))
                .andExpect(jsonPath("$[1].quality").value(4))
                .andExpect(jsonPath("$[2].quality").value(80))
                .andExpect(jsonPath("$[3].sellIn").value(-2))
                .andExpect(jsonPath("$[3].quality").value(1));
    }

    @Test
    void returnsBadRequestForBlankItemName() throws Exception {
        String requestJson = """
                [
                    {"name":" ","sellIn":1,"quality":10}
                ]
                """;

        mockMvc.perform(post("/api/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Item name must not be blank"));
    }

    @Test
    void acceptsEmptyInventory() throws Exception {
        mockMvc.perform(post("/api/inventory/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}