package com.transithub.train.inventory.graphql;
import com.transithub.train.inventory.service.TrainInventoryService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class TrainInventoryQueryController {
    private final TrainInventoryService service;
    public TrainInventoryQueryController(TrainInventoryService service) { this.service = service; }

    @QueryMapping
    public List<TrainInventoryService.Train> searchTrains(@Argument String from, @Argument String to,
            @Argument String date, @Argument String quota) {
        return service.searchTrains(from, to, date, quota);
    }

    @QueryMapping
    public TrainInventoryService.Train trainDetail(@Argument String trainId) {
        return service.trainDetail(trainId);
    }

    @QueryMapping
    public List<TrainInventoryService.Berth> coachMap(@Argument String trainId, @Argument String date, @Argument String coachType) {
        return service.coachMap(trainId, date, coachType);
    }

    @QueryMapping
    public List<TrainInventoryService.Station> stations(@Argument String query) {
        return service.stations(query);
    }
}
