package com.transithub.shared;

import java.util.List;

/**
 * Sealed interface for type-safe Saga routing (Java 21).
 * Used by all booking services to process commands via pattern matching switch.
 */
public sealed interface BookingCommand 
    permits FlightBookingCommand, BusBookingCommand, TrainBookingCommand, MultiModalBookingCommand {}

public record FlightBookingCommand(String userId, String flightId, String date, String cabin, 
                                    List<PassengerInfo> passengers, List<String> seats) implements BookingCommand {}

public record BusBookingCommand(String userId, String busId, String date, List<String> seats, 
                                 String boardingPoint) implements BookingCommand {}

public record TrainBookingCommand(String userId, String trainId, String date, String coachType, 
                                   String quota, List<PassengerInfo> passengers) implements BookingCommand {}

public record MultiModalBookingCommand(String userId, List<BookingCommand> segments) implements BookingCommand {}

public record PassengerInfo(String name, int age, String gender, String idType, String idNumber) {}
