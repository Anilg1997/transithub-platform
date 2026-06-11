package com.transithub.refund.graphql;
import com.transithub.refund.service.RefundService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class RefundMutationController {
    private final RefundService service;
    public RefundMutationController(RefundService service) { this.service = service; }

    @MutationMapping
    public RefundService.RefundResult processRefund(@Argument String bookingRef) {
        return service.processRefund(bookingRef);
    }
}
