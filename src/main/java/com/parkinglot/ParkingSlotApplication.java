package com.parkinglot;

import com.parkinglot.exception.ParkingFullException;
import com.parkinglot.exception.ParkingSlotNotFoundException;
import com.parkinglot.service.ParkingService;

public class ParkingSlotApplication {
    public static void main(String[] args) {
//        $ create_parking_lot 6
//        Created a parking lot with 6 slots
        ParkingService parkingService = new ParkingService();

        parkingService.createParkingLot(6);

//        $ park KA-01-HH-1234 White
//        Allocated slot number: 1
        try {
            int parkingNo = parkingService.park("KA-01-HH-1234", "White");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }

//        $ park KA-01-HH-9999 White
//        Allocated slot number: 2
        try {
            int parkingNo = parkingService.park("KA-01-HH-9999", "White");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }


//        $ park KA-01-BB-0001 Black
//        Allocated slot number: 3
        try {
            int parkingNo = parkingService.park("KA-01-BB-0001", "Black");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }

//        $ park KA-01-HH-7777 Red
//        Allocated slot number: 4
        try {
            int parkingNo = parkingService.park("KA-01-HH-7777", "Red");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }

//        $ park KA-01-HH-2701 Blue
//        Allocated slot number: 5
        try {
            int parkingNo = parkingService.park("KA-01-HH-2701", "Blue");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }

//        $ park KA-01-HH-3141 Black
//        Allocated slot number: 6
        try {
            int parkingNo = parkingService.park("KA-01-HH-3141", "Black");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }

//        $ leave 4
//        Slot number 4 is free
        try {
            int slot = parkingService.freeParking(4);
            System.out.println("Slot number"  + slot + " is free");
        }
        catch (ParkingSlotNotFoundException parkingSlotNotFoundException){
            System.out.println("No Such Parking slot found in assigned parkingSlots !!!!");
        }


//        $ status
//        Slot No. Registration No Colour
//        1 KA-01-HH-1234 White
//        2 KA-01-HH-9999 White
//        3 KA-01-BB-0001 Black
//        5 KA-01-HH-2701 Blue
//        6 KA-01-HH-3141 Black
        parkingService.printParkingStatus();

//        $ park KA-01-P-333 White
//        Allocated slot number: 4
        try {
            int parkingNo = parkingService.park("KA-01-P-333", "White");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Parking Full !!!!");
        }

//        $ park DL-12-AA-9999 White
//        Sorry, parking lot is full
        try {
            int parkingNo = parkingService.park("DL-12-AA-9999", "White");
            System.out.println("Allocated slot number: " + parkingNo);
        }
        catch (ParkingFullException parkingFullException){
            System.out.println("Sorry, parking lot is full");
        }

//        $ registration_numbers_for_cars_with_colour White
//        KA-01-HH-1234, KA-01-HH-9999, KA-01-P-333
        System.out.println(parkingService.getRegistratoinNumberForCarsWithColor("White"));

//        $ slot_numbers_for_cars_with_colour White
//        1, 2, 4
        System.out.println(parkingService.getSlotNumbersForCarWithColor("White"));

//        $ slot_number_for_registration_number KA-01-HH-3141
//        6
        System.out.println(parkingService.getSlotNumberForRegistrationNumber("KA-01-HH-3141"));

//        $ slot_number_for_registration_number MH-04-AY-1111
//        Not found
        System.out.println(parkingService.getSlotNumberForRegistrationNumber("MH-04-AY-1111"));

//        $ exit
    }
}
