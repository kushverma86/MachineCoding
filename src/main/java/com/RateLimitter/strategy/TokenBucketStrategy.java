package com.RateLimitter.strategy;

public class TokenBucketStrategy implements RateLimitStrategy{

    final long maxCapacity;
    double currentToken;
    final double tokenPerSecond;
    long lastRefillTime;


    public TokenBucketStrategy(long maxCapacity, long tokenPerSecond) {
        this.maxCapacity = maxCapacity;
        this.currentToken = maxCapacity;
        this.tokenPerSecond = tokenPerSecond;
        this.lastRefillTime = System.currentTimeMillis();
    }


    @Override
    public synchronized boolean allowRequest() {
        refill();
        if (currentToken>0){
            currentToken--;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double numberOfTokensToRefil = ((now - lastRefillTime))/1000.0 * tokenPerSecond;
        currentToken = Math.min(maxCapacity, currentToken + numberOfTokensToRefil);
        lastRefillTime = now;
    }
}
