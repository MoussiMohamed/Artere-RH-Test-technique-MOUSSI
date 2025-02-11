package com.artere.cacheservice.model;

import lombok.Getter;

import java.time.Instant;

/**
 * Represents a cache entry with a Time-To-Live (TTL).
 * <p>
 * Each entry consists of:
 * - A stored value.
 * - An expiration timestamp (based on TTL).
 * - A method to check if the entry has expired.
 */
@Getter
public class CacheEntry {
    /**
     * -- GETTER --
     *  Retrieves the stored value.
     */
    private final String value; // The actual value stored in the cache
    /**
     * -- GETTER --
     *  Retrieves the expiration timestamp.
     */
    private final Instant expiryTime; // The expiration timestamp of this entry

    /**
     * Constructs a new cache entry.
     *
     * @param value The value to be stored.
     * @param ttlMillis Time-To-Live (TTL) in milliseconds before expiration.
     */
    public CacheEntry(String value, long ttlMillis) {
        this.value = value;
        this.expiryTime = Instant.now().plusMillis(ttlMillis); // Calculate the expiration time
    }

    /**
     * Checks if the cache entry has expired.
     *
     * @return true if the entry has expired, false otherwise.
     */
    public boolean isExpired() {
        return Instant.now().isAfter(expiryTime);
    }

}
