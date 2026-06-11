package com.transithub.payment.graphql;
import com.transithub.payment.service.PaymentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
public class PaymentMutationController {
    private final PaymentService service;
    public PaymentMutationController(PaymentService service) { this.service = service; }

    @MutationMapping
    public PaymentService.Payment initiatePayment(@ContextValue UUID userId,
            @Argument String bookingRef, @Argument double amount, @Argument String method, @Argument String gstNumber) {
        return service.initiatePayment(userId, bookingRef, amount, method, gstNumber);
    }

    @MutationMapping
    public PaymentService.Payment confirmPayment(@Argument String transactionId, @Argument Boolean mockSuccess) {
        return service.confirmPayment(transactionId, mockSuccess);
    }

    @MutationMapping
    public PaymentService.Payment walletPay(@ContextValue UUID userId, @Argument String bookingRef) {
        return service.walletPay(userId, bookingRef);
    }
}
