package com.transithub.admin.graphql;
import com.transithub.admin.service.AdminService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class AdminMutationController {
    private final AdminService service;
    public AdminMutationController(AdminService service) { this.service = service; }

    @MutationMapping
    public AdminService.Flight addFlight(@Argument String flightNumber, @Argument String airlineCode,
            @Argument String origin, @Argument String destination, @Argument String departureTime,
            @Argument String arrivalTime, @Argument int duration, @Argument String cabinClass,
            @Argument int totalSeats, @Argument double baseFare) {
        return service.addFlight(flightNumber, airlineCode, origin, destination, departureTime,
            arrivalTime, duration, cabinClass, totalSeats, baseFare);
    }

    @MutationMapping
    public AdminService.Flight updateFlight(@Argument String flightId, @Argument String flightNumber,
            @Argument String airlineCode, @Argument String origin, @Argument String destination,
            @Argument String departureTime, @Argument String arrivalTime, @Argument int duration,
            @Argument String cabinClass, @Argument int totalSeats, @Argument double baseFare) {
        return service.updateFlight(flightId, flightNumber, airlineCode, origin, destination,
            departureTime, arrivalTime, duration, cabinClass, totalSeats, baseFare);
    }

    @MutationMapping
    public boolean deleteFlight(@Argument String flightId) {
        return service.deleteFlight(flightId);
    }

    @MutationMapping
    public AdminService.BusRoute addBusRoute(@Argument String operatorId, @Argument String busType,
            @Argument String origin, @Argument String destination, @Argument String departureTime,
            @Argument String arrivalTime, @Argument int duration, @Argument int totalSeats,
            @Argument double fare, @Argument List<String> boardingPoints, @Argument List<String> droppingPoints) {
        return service.addBusRoute(operatorId, busType, origin, destination, departureTime,
            arrivalTime, duration, totalSeats, fare, boardingPoints, droppingPoints);
    }

    @MutationMapping
    public AdminService.BusRoute updateBusRoute(@Argument String routeId, @Argument String operatorId,
            @Argument String busType, @Argument String origin, @Argument String destination,
            @Argument String departureTime, @Argument String arrivalTime, @Argument int duration,
            @Argument int totalSeats, @Argument double fare, @Argument List<String> boardingPoints,
            @Argument List<String> droppingPoints) {
        return service.updateBusRoute(routeId, operatorId, busType, origin, destination, departureTime,
            arrivalTime, duration, totalSeats, fare, boardingPoints, droppingPoints);
    }

    @MutationMapping
    public boolean deleteBusRoute(@Argument String routeId) {
        return service.deleteBusRoute(routeId);
    }

    @MutationMapping
    public AdminService.Train addTrain(@Argument String trainNumber, @Argument String trainName,
            @Argument String originCode, @Argument String destinationCode, @Argument String departureTime,
            @Argument String arrivalTime, @Argument int duration, @Argument List<String> runsOnDays) {
        return service.addTrain(trainNumber, trainName, originCode, destinationCode, departureTime,
            arrivalTime, duration, runsOnDays);
    }

    @MutationMapping
    public AdminService.Train updateTrain(@Argument String trainId, @Argument String trainNumber,
            @Argument String trainName, @Argument String originCode, @Argument String destinationCode,
            @Argument String departureTime, @Argument String arrivalTime, @Argument int duration,
            @Argument List<String> runsOnDays) {
        return service.updateTrain(trainId, trainNumber, trainName, originCode, destinationCode,
            departureTime, arrivalTime, duration, runsOnDays);
    }

    @MutationMapping
    public boolean deleteTrain(@Argument String trainId) {
        return service.deleteTrain(trainId);
    }

    @MutationMapping
    public boolean banUser(@Argument String userId) {
        return service.banUser(userId);
    }

    @MutationMapping
    public boolean unbanUser(@Argument String userId) {
        return service.unbanUser(userId);
    }

    @MutationMapping
    public boolean overrideBooking(@Argument String bookingRef, @Argument String action) {
        return service.overrideBooking(bookingRef, action);
    }
}
