package com.artere.cacheservice.controller;

import com.artere.cacheservice.service.CacheService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the CacheController REST API.
 * Ensures that the API endpoints function correctly.
 */
@WebMvcTest(controllers = CacheController.class) // Loads only the CacheController (not the full Spring context)
class CacheControllerTest {

    @Autowired
    private MockMvc mockMvc; // MockMvc is used to perform HTTP requests in tests.

    @MockBean // Mocks CacheService to prevent dependency injection issues.
    private CacheService cacheService;

    /**
     * Tests if the API can store a value successfully.
     */
    @Test
    void shouldStoreValueViaApi() throws Exception {
        mockMvc.perform(post("/cache/put")
                        .param("key", "employee:201")
                        .param("value", "Frank")
                        .param("ttl", "5000"))
                .andExpect(status().isOk()); // API should return HTTP 200 OK
    }

    /**
     * Tests if the API can retrieve a stored value correctly.
     */
    @Test
    void shouldRetrieveValueViaApi() throws Exception {
        // Simulate that "employee:202" is already stored in cache
        when(cacheService.get("employee:202")).thenReturn("Grace");

        mockMvc.perform(get("/cache/get")
                        .param("key", "employee:202"))
                .andExpect(status().isOk()) // API should return HTTP 200 OK
                .andExpect(content().string("Grace")); // Should return "Grace"
    }

    /**
     * Tests if an expired entry returns null (empty response).
     */
    @Test
    void shouldReturnNullForExpiredEntry() throws Exception {
        // Simulate that "employee:203" has expired
        when(cacheService.get("employee:203")).thenReturn(null);

        mockMvc.perform(get("/cache/get")
                        .param("key", "employee:203"))
                .andExpect(status().isOk()) // API should return HTTP 200 OK
                .andExpect(content().string("")); // Response should be empty
    }

    /**
     * Tests if an entry can be deleted successfully via the API.
     */
    @Test
    void shouldDeleteValueViaApi() throws Exception {
        mockMvc.perform(delete("/cache/delete")
                        .param("key", "employee:204"))
                .andExpect(status().isOk()); // API should return HTTP 200 OK
    }
}
