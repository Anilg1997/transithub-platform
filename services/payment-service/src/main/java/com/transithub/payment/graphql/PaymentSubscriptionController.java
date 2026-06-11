package com.transithub.payment.graphql;
import com.transithub.payment.service.PaymentService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class PaymentSubscriptionController {
    private final PaymentService service;
    public PaymentSubscriptionController(PaymentService service) { this.service = service; }

    @SubscriptionMapping
    public Publisher<PaymentService.Payment> paymentStatusUpdate(@Argument String transactionId) {
        return Flux.interval(java.time.Duration.ofSeconds(2))
            .map(i -> service.getPaymentStatus(transactionId))
            .filter(p -> p != null)
            .distinctUntilChanged();
    }
}
