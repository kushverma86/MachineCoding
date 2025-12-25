package com.snakeandladder.Entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class Player {
    private final String name;
    private int currentPos = 0;
    private boolean isWon = false;
}
