package com.snakeandladder.Entity;

import lombok.Value;

import java.util.concurrent.ThreadLocalRandom;

@Value
public class Dice {
    int numDices;

    public int roll(){
        return ThreadLocalRandom.current().nextInt(numDices, (numDices*6)+1);
    }
}
