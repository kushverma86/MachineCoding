package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSlot;

import java.util.Optional;

public interface ParkingStrategy {

    Optional<ParkingSlot> getNextAvailableSlot();

    void addSlot(int slotNo);
}
