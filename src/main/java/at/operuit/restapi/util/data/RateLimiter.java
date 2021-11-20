package at.operuit.restapi.util.data;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class RateLimiter {

    public static final Cache<String, RateLimiter> LIMITER_CACHE = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    private final int limit;
    private int remaining;
    private long reset;

    public RateLimiter(int limit) {
        this.limit = limit;
        this.remaining = limit;
        this.reset = System.currentTimeMillis() + 1000;
    }

    public boolean get() {
        if (System.currentTimeMillis() > reset) {
            remaining = limit;
            reset = System.currentTimeMillis() + 1000;
        }
        if (remaining > 0) {
            remaining--;
            return false;
        }
        return true;
    }

    public static RateLimiter compute(String key, int limit) {
        return LIMITER_CACHE.asMap().computeIfAbsent(key, (k) -> new RateLimiter(limit));
    }

}
