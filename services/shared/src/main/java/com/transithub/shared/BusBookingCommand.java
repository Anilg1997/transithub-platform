package com.transithub.shared;

import java.util.List;

public record BusBookingCommand(String userId, String busId, String date, List<String> seats,
                                 String boardingPoint) implements BookingCommand {}
