package com.artere.cacheservice.controller;

import com.artere.cacheservice.service.CacheService;
import org.springframework.web.bind.annotation.*;

/**
 * REST API Controller to manage cache interactions.
 *
 * Provides endpoints for storing, retrieving, and deleting cache entries.
 */
@RestController // Marks this class as a RESTful web controller
@RequestMapping("/cache") // Base URL for all API endpoints
public class CacheController {
    private final CacheService cacheService;

    /**
     * Constructor-based dependency injection for the CacheService.
     * @param cacheService The service handling cache operations.
     */
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    /**
     * Stores a value in the cache with a specified Time-To-Live (TTL).
     * <p>
     * Example Request:
     * POST /cache/put?key=employee:101&value=Alice&ttl=5000
     *
     * @param key The unique key under which the value is stored.
     * @param value The value to be stored.
     * @param ttl Time-To-Live in milliseconds before expiration.
     */
    @PostMapping("/put")
    public void put(@RequestParam String key, @RequestParam String value, @RequestParam long ttl) {
        cacheService.put(key, value, ttl);
    }

    /**
     * Retrieves a value from the cache if it exists and has not expired.
     * <p>
     * Example Request:
     * GET /cache/get?key=employee:101
     *
     * @param key The unique key of the stored value.
     * @return The cached value or null if the entry does not exist or has expired.
     */
    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return cacheService.get(key);
    }

    /**
     * Deletes a specific cache entry based on its key.
     * <p>
     * Example Request:
     * DELETE /cache/delete?key=employee:101
     *
     * @param key The unique key of the cache entry to delete.
     */
    @DeleteMapping("/delete")
    public void delete(@RequestParam String key) {
        cacheService.delete(key);
    }
}
