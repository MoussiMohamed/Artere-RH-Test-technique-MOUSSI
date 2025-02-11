package com.artere.cacheservice.service;

import com.artere.cacheservice.model.CacheEntry;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * In-memory caching service with automatic expiration management.
 * <p>
 * Features:
 * - Thread-safe storage using ConcurrentHashMap.
 * - Automatic removal of expired entries.
 * - Uses a scheduled task for periodic cleanup.
 */
@Service // Marks this class as a Spring service component
public class CacheService {
    // Thread-safe map for storing cache entries
    private final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // Priority queue for efficient expiration management (earliest expiration first)
    private final PriorityQueue<Map.Entry<String, CacheEntry>> expiryQueue =
            new PriorityQueue<>(Comparator.comparing(e -> e.getValue().getExpiryTime()));

    // Scheduled executor service for periodic cache cleanup
    private final ScheduledExecutorService cleanupScheduler = Executors.newScheduledThreadPool(1);

    // Read-Write Lock to handle concurrent access safely
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Constructor that initializes scheduled cleanup.
     * The cleanup task runs every 5 seconds to remove expired entries.
     */
    public CacheService() {
        cleanupScheduler.scheduleAtFixedRate(this::cleanup, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * Stores a value in the cache with a specified Time-To-Live (TTL).
     *
     * @param key Unique identifier for the cache entry.
     * @param value The data to be stored.
     * @param ttlMillis Time-To-Live (TTL) in milliseconds before expiration.
     */
    public void put(String key, String value, long ttlMillis) {
        if (key == null || value == null || ttlMillis <= 0) {
            throw new IllegalArgumentException("Key, value, and TTL must be valid!");
        }

        lock.writeLock().lock(); // Ensures thread safety while writing to the cache
        try {
            CacheEntry entry = new CacheEntry(value, ttlMillis);
            cache.put(key, entry);
            expiryQueue.offer(new AbstractMap.SimpleEntry<>(key, entry)); // Add entry to expiration queue
        } finally {
            lock.writeLock().unlock(); // Release lock
        }
    }

    /**
     * Retrieves a value from the cache if it exists and has not expired.
     *
     * @param key The unique key for the stored value.
     * @return The cached value, or null if expired/non-existent.
     */
    public String get(String key) {
        lock.readLock().lock(); // Ensures thread safety while reading from the cache
        try {
            CacheEntry entry = cache.get(key);
            if (entry == null || entry.isExpired()) {
                cache.remove(key); // Remove expired entry if it exists
                return null;
            }
            return entry.getValue();
        } finally {
            lock.readLock().unlock(); // Release lock
        }
    }

    /**
     * Removes an entry from the cache based on its key.
     *
     * @param key The unique identifier of the entry to remove.
     */
    public void delete(String key) {
        lock.writeLock().lock(); // Ensures thread safety while modifying the cache
        try {
            cache.remove(key);
        } finally {
            lock.writeLock().unlock(); // Release lock
        }
    }

    /**
     * Periodically removes expired entries using a priority queue.
     * The queue ensures that the oldest (earliest expiring) entries are checked first.
     */
    void cleanup() {
        lock.writeLock().lock(); // Prevents modifications while cleanup is running
        try {
            while (!expiryQueue.isEmpty() && expiryQueue.peek().getValue().isExpired()) {
                Map.Entry<String, CacheEntry> expiredEntry = expiryQueue.poll();
                cache.remove(expiredEntry.getKey()); // Remove expired entry from cache
            }
        } finally {
            lock.writeLock().unlock(); // Release lock
        }
    }
}
