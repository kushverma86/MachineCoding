package com.parkinglot.exception;

public class ParkingFullException extends RuntimeException{
    public ParkingFullException(String msg){
        super(msg);
    }
}
