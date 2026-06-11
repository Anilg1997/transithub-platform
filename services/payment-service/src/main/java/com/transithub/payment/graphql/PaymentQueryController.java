package com.transithub.payment.graphql;
import com.transithub.payment.service.PaymentService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class PaymentQueryController {
    private final PaymentService service;
    public PaymentQueryController(PaymentService service) { this.service = service; }

    @QueryMapping
    public PaymentService.Payment paymentStatus(@Argument String transactionId) {
        return service.getPaymentStatus(transactionId);
    }

    @QueryMapping
    public List<PaymentService.Payment> myPayments(@ContextValue UUID userId) {
        return service.getMyPayments(userId);
    }
}
