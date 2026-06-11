package com.transithub.train.availability.graphql;
import com.transithub.train.availability.service.TrainAvailabilityService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class TrainAvailabilityQueryController {
    private final TrainAvailabilityService service;
    public TrainAvailabilityQueryController(TrainAvailabilityService service) { this.service = service; }

    @QueryMapping
    public TrainAvailabilityService.Availability availability(@Argument String trainId, @Argument String date, @Argument String coach) {
        return service.getAvailability(trainId, date, coach);
    }
}
