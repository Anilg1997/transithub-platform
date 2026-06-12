package com.transithub.shared;

/**
 * Sealed interface for type-safe Saga routing (Java 21).
 * Used by all booking services to process commands via pattern matching switch.
 */
public sealed interface BookingCommand 
    permits FlightBookingCommand, BusBookingCommand, TrainBookingCommand, MultiModalBookingCommand {}
