package com.RateLimitter.service;

import org.example.RateLimitter.strategy.RateLimitStrategy;

public class RateLimitService {

    RateLimitStrategy rateLimitStrategy;

    public RateLimitService(RateLimitStrategy rateLimitStrategy){
        this.rateLimitStrategy = rateLimitStrategy;
    }

    public boolean rateLimit(){
        return rateLimitStrategy.allowRequest();
    }
}
