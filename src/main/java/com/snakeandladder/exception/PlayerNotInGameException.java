package com.snakeandladder.exception;

public class PlayerNotInGameException extends RuntimeException{

    public PlayerNotInGameException(String msg){
        super(msg);
    }

}
