package com.transithub.train.booking.graphql;
import com.transithub.train.booking.service.TrainBookingService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class TrainBookingMutationController {
    private final TrainBookingService service;
    public TrainBookingMutationController(TrainBookingService service) { this.service = service; }

    @MutationMapping
    public TrainBookingService.TrainBooking bookTrain(@ContextValue UUID userId,
            @Argument String trainId, @Argument String date, @Argument String coachType,
            @Argument String quota, @Argument List<Map<String, Object>> passengers,
            @Argument List<String> berthPreferences) {
        return service.bookTrain(userId, trainId, date, coachType, quota, passengers, berthPreferences);
    }

    @MutationMapping
    public TrainBookingService.TrainBooking cancelTrain(@Argument String bookingRef) {
        return service.cancelTrain(bookingRef);
    }
}
