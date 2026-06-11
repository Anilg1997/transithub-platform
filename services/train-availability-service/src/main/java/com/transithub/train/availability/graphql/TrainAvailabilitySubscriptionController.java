package com.transithub.train.availability.graphql;
import com.transithub.train.availability.service.TrainAvailabilityService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class TrainAvailabilitySubscriptionController {
    private final TrainAvailabilityService service;
    public TrainAvailabilitySubscriptionController(TrainAvailabilityService service) { this.service = service; }

    @SubscriptionMapping
    public Publisher<TrainAvailabilityService.Availability> seatAvailabilityUpdate(@Argument String trainId, @Argument String date) {
        return Flux.interval(java.time.Duration.ofSeconds(10))
            .map(i -> service.getAvailability(trainId, date, "AC3"))
            .distinctUntilChanged();
    }
}
