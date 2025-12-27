package com.parkinglot.entity;


import com.parkinglot.strategy.ParkingStrategy;
import lombok.Getter;

@Getter
public class ParkingLot {
    private int totalSlots;
    private ParkingStrategy parkingStrategy;

    public ParkingLot(int n, ParkingStrategy parkingStrategy){
        this.totalSlots = n;
        this.parkingStrategy = parkingStrategy;
    }



}
