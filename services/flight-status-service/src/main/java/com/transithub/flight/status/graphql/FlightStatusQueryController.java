package com.transithub.flight.status.graphql;
import com.transithub.flight.status.service.FlightStatusService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class FlightStatusQueryController {
    private final FlightStatusService service;
    public FlightStatusQueryController(FlightStatusService service) { this.service = service; }

    @QueryMapping
    public FlightStatusService.FlightStatus flightStatus(@Argument String flightNumber, @Argument String date) {
        return service.getFlightStatus(flightNumber, date);
    }
}
