package com.snakeandladder.Entity;

import com.customerissueresolutionportal.excpetion.InvalidStateException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Board {

    int totalCells;

    // cell head to snake
    Map<Integer, Snake> snakes;

    // ladder start to ladder
    Map<Integer, Ladder> ladders;

    public Board(int n){
        if (n<1){
            throw new IllegalStateException();
        }
        this.totalCells = n;
        snakes = new HashMap<Integer, Snake>();
        ladders = new HashMap<Integer, Ladder>();
    }

    public void addSnake(int head, int tail){
        if (head < tail){
            throw new InvalidStateException("snake head should be greater than tail");
        }
        Snake snake = new Snake(head, tail);
        snakes.put(head, snake);
    }

    public void addLadder(int start, int end){
        if (start>end){
            throw new IllegalStateException("ladder start should be less than end");
        }
        ladders.put(start, new Ladder(start, end));
    }

}
