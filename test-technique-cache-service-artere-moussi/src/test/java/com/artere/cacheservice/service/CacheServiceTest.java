package com.artere.cacheservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CacheService.
 * Ensures correct behavior of the caching system, including storage, expiration, and cleanup.
 */
class CacheServiceTest {
    private CacheService cacheService;

    /**
     * Initializes a new CacheService instance before each test.
     */
    @BeforeEach
    void setUp() {
        cacheService = new CacheService();
    }

    /**
     * Test that a value is correctly stored and retrieved.
     */
    @Test
    void shouldStoreAndRetrieveValue() {
        cacheService.put("employee:101", "Alice", 5000);

        // The stored value should be retrievable
        assertEquals("Alice", cacheService.get("employee:101"));
    }

    /**
     * Test that a value is correctly removed from the cache after expiration.
     */
    @Test
    void shouldReturnNullForExpiredEntry() throws InterruptedException {
        cacheService.put("employee:102", "Bob", 100);

        // Wait for expiration
        Thread.sleep(200);

        // The value should no longer exist in the cache
        assertNull(cacheService.get("employee:102"));
    }

    /**
     * Test that deleting an entry manually removes it from the cache.
     */
    @Test
    void shouldDeleteValue() {
        cacheService.put("employee:103", "Charlie", 5000);

        // Ensure value exists before deletion
        assertEquals("Charlie", cacheService.get("employee:103"));

        // Delete the value
        cacheService.delete("employee:103");

        // The value should be removed
        assertNull(cacheService.get("employee:103"));
    }

    /**
     * Test that invalid inputs (null keys, null values, and negative TTL) throw exceptions.
     */
    @Test
    void shouldNotAllowInvalidInputs() {
        // Null key should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> cacheService.put(null, "Data", 5000));

        // Null value should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> cacheService.put("invalid", null, 5000));

        // Negative TTL should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> cacheService.put("invalid", "Data", -1));
    }

    /**
     * Test that expired entries are removed during cleanup.
     */
    @Test
    void shouldCleanupExpiredEntries() throws InterruptedException {
        cacheService.put("employee:104", "David", 100);
        cacheService.put("employee:105", "Eve", 3000);

        // Wait for the first entry to expire
        Thread.sleep(200);

        // Perform cleanup
        cacheService.cleanup();

        // The first entry should be removed, while the second remains
        assertNull(cacheService.get("employee:104"));
        assertEquals("Eve", cacheService.get("employee:105"));
    }
}
