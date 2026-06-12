package com.transithub.shared;

public record RefundableFare(double cancellationFee) implements FareRule {}
