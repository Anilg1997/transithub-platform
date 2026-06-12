package com.transithub.shared;

import java.util.List;

public record MultiModalBookingCommand(String userId, List<BookingCommand> segments) implements BookingCommand {}
