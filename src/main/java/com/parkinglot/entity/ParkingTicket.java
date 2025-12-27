package com.parkinglot.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@RequiredArgsConstructor
@Getter
@Setter
public class ParkingTicket {
    private final String prakingId = UUID.randomUUID().toString();
    private LocalDateTime entryTime = LocalDateTime.now();
    private LocalDateTime exitTime;
    private  String carNumber;
    private int slotAssigned;
    private float ticketPrice;

}
