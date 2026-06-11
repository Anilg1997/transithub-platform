package com.transithub.bus.tracking.graphql;
import com.transithub.bus.tracking.service.BusTrackingService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class BusTrackingSubscriptionController {
    private final BusTrackingService service;
    public BusTrackingSubscriptionController(BusTrackingService service) { this.service = service; }

    @SubscriptionMapping
    public Publisher<BusTrackingService.BusLocation> busLocation(@Argument String busId) {
        return Flux.interval(java.time.Duration.ofSeconds(3))
            .map(i -> service.getBusLocation(busId))
            .filter(loc -> loc != null)
            .distinctUntilChanged();
    }
}
