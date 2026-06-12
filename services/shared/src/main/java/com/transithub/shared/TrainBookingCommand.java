package com.transithub.shared;

import java.util.List;

public record TrainBookingCommand(String userId, String trainId, String date, String coachType,
                                   String quota, List<PassengerInfo> passengers) implements BookingCommand {}
