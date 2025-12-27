package com.parkinglot.strategy;

import com.parkinglot.entity.ParkingSlot;
import lombok.Getter;

import java.util.Optional;
import java.util.PriorityQueue;

@Getter
public class NearestParkingStrategy implements ParkingStrategy{

    private PriorityQueue<ParkingSlot> availableSlots;

    public NearestParkingStrategy(int n){
        availableSlots = new PriorityQueue<>((a, b) -> a.getSlot_no() - b.getSlot_no());
        for (int i=1; i<=n; i++){
            availableSlots.add(new ParkingSlot(i));
        }
    }

    @Override
    public Optional<ParkingSlot> getNextAvailableSlot() {
        return Optional.ofNullable(availableSlots.poll());
    }

    @Override
    public void addSlot(int slotNo) {
        ParkingSlot parkingSlot = new ParkingSlot(slotNo);
        availableSlots.add(parkingSlot);
    }
}
