package com.transithub.shared;

/**
 * Sealed interface for fare rules using Java 21 pattern matching.
 */
public sealed interface FareRule permits RefundableFare, PartiallyRefundableFare, NonRefundableFare {}
