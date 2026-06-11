package com.transithub.flight.status.graphql;
import com.transithub.flight.status.service.FlightStatusService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class FlightStatusSubscriptionController {
    private final FlightStatusService service;
    public FlightStatusSubscriptionController(FlightStatusService service) { this.service = service; }

    @SubscriptionMapping
    public Publisher<FlightStatusService.FlightStatus> flightStatusUpdate(@Argument String flightNumber) {
        return Flux.interval(java.time.Duration.ofSeconds(5))
            .map(i -> service.getFlightStatus(flightNumber, java.time.LocalDate.now().toString()))
            .filter(s -> s != null)
            .distinctUntilChanged();
    }
}
