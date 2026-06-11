package com.transithub.shared;

/**
 * Sealed interface for fare rules using Java 21 pattern matching.
 */
public sealed interface FareRule permits RefundableFare, PartiallyRefundableFare, NonRefundableFare {}

public record RefundableFare(double cancellationFee) implements FareRule {}

public record PartiallyRefundableFare(double refundPercent, int hoursBeforeDeparture) implements FareRule {}

public record NonRefundableFare() implements FareRule {}
