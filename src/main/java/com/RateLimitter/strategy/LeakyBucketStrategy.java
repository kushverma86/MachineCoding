package com.RateLimitter.strategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LeakyBucketStrategy implements RateLimitStrategy{

    public final int maxCapacity;
    public final int leakPerSecond;
    public AtomicInteger currentCapacity;

    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public LeakyBucketStrategy(int maxCapacity, int leakPerSecond) {
        this.maxCapacity = maxCapacity;
        this.leakPerSecond = leakPerSecond;
        this.currentCapacity = new AtomicInteger(0);
        scheduler.scheduleAtFixedRate(() -> {
            currentCapacity.updateAndGet(current -> Math.max(0, current - leakPerSecond));
        }, 1, 1, TimeUnit.SECONDS);
    }


    @Override
    public boolean allowRequest() {
        if (currentCapacity.get() >= maxCapacity){
            return false;
        }
        currentCapacity.incrementAndGet();
        return true;
    }
}
