package com.parkinglot.service;

import com.parkinglot.entity.Car;
import com.parkinglot.entity.ParkingLot;
import com.parkinglot.entity.ParkingSlot;
import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.exception.ParkingSlotNotFoundException;
import com.parkinglot.strategy.NearestParkingStrategy;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ParkingService {

    ParkingLot parkingLot;

//    Registration numbers of all cars of a particular colour.
//    Slot number in which a car with a given registration number is parked.
//    Slot numbers of all slots where a car of a particular colour is parked.
    Map<Integer, ParkingSlot> parkingSlotMap;


    //    create_parking_lot 6
//Created a parking lot with 6 slots
    public void createParkingLot(int n){
        this.parkingLot = new ParkingLot(n, new NearestParkingStrategy(n));
        this.parkingSlotMap = new HashMap<>();
        System.out.println("Created a parking lot with " +  n + " slots");
    }


//    park KA-01-HH-1234 White
//    Allocated slot number: 1
    public int park(String registration_no, String color){
        Optional<ParkingSlot> checkParkingAvailable = parkingLot.getParkingStrategy().getNextAvailableSlot();
        if (!checkParkingAvailable.isPresent()){
            throw new ParkingFullException("Parking is full!!");
        }
        ParkingSlot parkingSlot = checkParkingAvailable.get();
        parkingSlot.setOccupied(true);
        parkingSlot.setCar(new Car(registration_no, color.toLowerCase()));
        parkingSlotMap.put(parkingSlot.getSlot_no(), parkingSlot);
        return parkingSlot.getSlot_no();
    }

//    leave 4
    //Slot number 4 is free
    public int freeParking(int slotNumber){
        if (parkingSlotMap.containsKey(slotNumber)){
            ParkingSlot parkingSlot = parkingSlotMap.get(slotNumber);
            parkingSlotMap.remove(slotNumber);
            parkingLot.getParkingStrategy().addSlot(slotNumber);
            return parkingSlot.getSlot_no();
        }

        throw new ParkingSlotNotFoundException("No car has been found in this parkingSlot");
    }



    //status
    /**
     * Slot No. Registration No Colour
     * 1 KA-01-HH-1234 White
     * 2 KA-01-HH-9999 White
     * 3 KA-01-BB-0001 Black
     * 5 KA-01-HH-2701 Blue
     * 6 KA-01-HH-3141 Black
     */

    public void printParkingStatus(){
        System.out.println("Slot No. Registration No Colour");
        for (Map.Entry<Integer, ParkingSlot> parkedCar : parkingSlotMap.entrySet()){
            ParkingSlot parkingSlot = parkedCar.getValue();
            System.out.println(parkingSlot.getSlot_no() + "  " + parkingSlot.getCar().getRegistration_no() + "  " + parkingSlot.getCar().getColor());
        }
    }

//    registration_numbers_for_cars_with_colour White
//    KA-01-HH-1234, KA-01-HH-9999, KA-01-P-333
      public String getRegistratoinNumberForCarsWithColor(String color){
        return parkingSlotMap.values().stream().filter(parkingSlot -> parkingSlot.getCar().getColor().equalsIgnoreCase(color)).map( slot -> slot.getCar().getRegistration_no()).collect(Collectors.joining(", "));
      }


    /**
     * $ slot_numbers_for_cars_with_colour White
     * 1, 2, 4
     */

    public String getSlotNumbersForCarWithColor(String color){
        return parkingSlotMap.values().stream().filter(parkingSlot -> parkingSlot.getCar().getColor().equalsIgnoreCase(color)).map(parkingSlot -> String.valueOf(parkingSlot.getSlot_no())).collect(Collectors.joining(", "));
    }

    /**
     * $ slot_number_for_registration_number KA-01-HH-3141
     * 6
     * $ slot_number_for_registration_number MH-04-AY-1111
     * Not found
     */
    public String getSlotNumberForRegistrationNumber(String registrationNo){
        Optional<ParkingSlot> parkingSlot = parkingSlotMap.values().stream().filter(parkingSlot1 -> parkingSlot1.getCar().getRegistration_no().equalsIgnoreCase(registrationNo)).findFirst();

        return parkingSlot.map(slot -> String.valueOf(slot.getSlot_no())).orElse("Not found");

    }


}
