package com.transithub.refund.graphql;
import com.transithub.refund.service.RefundService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class RefundQueryController {
    private final RefundService service;
    public RefundQueryController(RefundService service) { this.service = service; }

    @QueryMapping
    public double refundEstimate(@Argument String bookingRef, @Argument String cancelType) {
        return service.estimateRefund(bookingRef, cancelType);
    }

    @QueryMapping
    public List<RefundService.RefundResult> myRefunds() {
        return service.getMyRefunds();
    }
}
