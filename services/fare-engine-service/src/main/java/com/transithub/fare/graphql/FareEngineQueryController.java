package com.transithub.fare.graphql;
import com.transithub.fare.service.FareEngineService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class FareEngineQueryController {
    private final FareEngineService service;
    public FareEngineQueryController(FareEngineService service) { this.service = service; }

    @QueryMapping
    public FareEngineService.FareCalculation calculateFare(@Argument String inventoryType, @Argument String itemId,
            @Argument String cabin, @Argument Integer pax) {
        return service.calculateFare(inventoryType, itemId, cabin, pax);
    }

    @QueryMapping
    public FareEngineService.RefundPolicy refundPolicy(@Argument String bookingRef, @Argument int hoursBeforeDeparture) {
        return service.getRefundPolicy(bookingRef, hoursBeforeDeparture);
    }
}
