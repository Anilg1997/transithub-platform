package com.transithub.shared;

public record PartiallyRefundableFare(double refundPercent, int hoursBeforeDeparture) implements FareRule {}
