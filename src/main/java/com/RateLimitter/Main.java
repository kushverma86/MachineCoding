package com.RateLimitter;

import org.example.RateLimitter.service.RateLimitService;
import org.example.RateLimitter.strategy.SlidingWindow;

public class Main {

    public static void main(String[] args) throws InterruptedException {
//        RateLimitService rateLimitService = new RateLimitService(new TokenBucketStrategy(10, 2));
        RateLimitService rateLimitService = new RateLimitService(new SlidingWindow(5, 2000));

        int i=0;
        while(i<12){
            System.out.println(i + " allowed : " + rateLimitService.rateLimit());
            i++;
        }

        Thread.sleep(3000);

        while(i<20){
            System.out.println(i + " allowed : " + rateLimitService.rateLimit());
            i++;
        }

    }
}
