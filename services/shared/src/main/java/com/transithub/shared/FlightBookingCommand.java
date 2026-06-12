package com.transithub.shared;

import java.util.List;

public record FlightBookingCommand(String userId, String flightId, String date, String cabin,
                                    List<PassengerInfo> passengers, List<String> seats) implements BookingCommand {}
