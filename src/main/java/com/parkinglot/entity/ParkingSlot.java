package com.parkinglot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ParkingSlot {
    private final int slot_no;
    private boolean isOccupied;
    private Car car;
}
