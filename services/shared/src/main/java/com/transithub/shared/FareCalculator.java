package com.transithub.shared;

/**
 * Uses Java 21 pattern matching switch to evaluate refund amounts.
 */
public class FareCalculator {
    
    public static double calculateRefund(FareRule fareRule, double totalFare, int hoursLeft) {
        return switch (fareRule) {
            case RefundableFare r -> totalFare - r.cancellationFee();
            case PartiallyRefundableFare p when hoursLeft > p.hoursBeforeDeparture() -> totalFare * p.refundPercent() / 100.0;
            case PartiallyRefundableFare p -> 0.0;
            case NonRefundableFare n -> 0.0;
        };
    }
    
    public static String describeFareRule(FareRule rule) {
        return switch (rule) {
            case RefundableFare r -> "Refundable (fee: ₹" + r.cancellationFee() + ")";
            case PartiallyRefundableFare p -> "Partially refundable (" + p.refundPercent() + "% if " + p.hoursBeforeDeparture() + "h before)";
            case NonRefundableFare n -> "Non-refundable";
        };
    }
}
