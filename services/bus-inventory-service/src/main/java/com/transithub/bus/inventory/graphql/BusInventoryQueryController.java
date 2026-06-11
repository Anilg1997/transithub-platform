package com.transithub.bus.inventory.graphql;
import com.transithub.bus.inventory.service.BusInventoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class BusInventoryQueryController {
    private final BusInventoryService service;
    public BusInventoryQueryController(BusInventoryService service) { this.service = service; }

    @QueryMapping
    public List<BusInventoryService.BusRoute> searchBuses(@Argument String origin, @Argument String destination, @Argument String date) {
        return service.searchBuses(origin, destination, date);
    }

    @QueryMapping
    public BusInventoryService.BusRoute busDetail(@Argument String busId) {
        return service.busDetail(busId);
    }

    @QueryMapping
    public List<BusInventoryService.BusSeat> busSeatMap(@Argument String busId, @Argument String date) {
        return service.busSeatMap(busId, date);
    }
}
