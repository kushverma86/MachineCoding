package com.RateLimitter.strategy;

import java.util.concurrent.ConcurrentLinkedDeque;

public class SlidingWindow implements RateLimitStrategy{

    public final int maxCapacity;

    public final long windowInMs;

    public ConcurrentLinkedDeque<Long> logs = new ConcurrentLinkedDeque<>();

    public SlidingWindow(int maxCapacity, long windowInMs) {
        this.maxCapacity = maxCapacity;
        this.windowInMs = windowInMs;
    }


    @Override
    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        long window = now - windowInMs;

        while (!logs.isEmpty() && logs.peekFirst() < window){
            logs.pollFirst();
        }

        if (logs.size() < maxCapacity){
            logs.addLast(now);
            return true;
        }

        return false;

    }
}
