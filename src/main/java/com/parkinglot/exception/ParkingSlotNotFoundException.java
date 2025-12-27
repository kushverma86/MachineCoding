package com.parkinglot.exception;

public class ParkingSlotNotFoundException extends RuntimeException{
    public ParkingSlotNotFoundException(String msg){
        super(msg);
    }
}
