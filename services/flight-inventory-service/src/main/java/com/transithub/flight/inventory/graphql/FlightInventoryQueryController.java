package com.transithub.flight.inventory.graphql;
import com.transithub.flight.inventory.service.FlightInventoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class FlightInventoryQueryController {
    private final FlightInventoryService service;
    public FlightInventoryQueryController(FlightInventoryService service) { this.service = service; }

    @QueryMapping
    public List<FlightInventoryService.Flight> searchFlights(@Argument String origin, @Argument String destination,
            @Argument String date, @Argument String cabinClass, @Argument Integer passengers) {
        return service.searchFlights(origin, destination, date, cabinClass, passengers);
    }

    @QueryMapping
    public FlightInventoryService.Flight flightDetail(@Argument String flightId) {
        return service.flightDetail(flightId);
    }

    @QueryMapping
    public FlightInventoryService.SeatMap seatMap(@Argument String flightId, @Argument String date) {
        return service.seatMap(flightId, date);
    }

    @QueryMapping
    public List<FlightInventoryService.Airport> airports(@Argument String query) {
        return service.airports(query);
    }
}
