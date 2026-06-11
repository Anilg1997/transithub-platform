package com.transithub.user.graphql;
import com.transithub.user.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class UserQueryController {
    private final UserService service;
    public UserQueryController(UserService service) { this.service = service; }

    @QueryMapping
    public UserService.UserProfile myProfile(@ContextValue UUID userId) {
        return service.getMyProfile(userId);
    }

    @QueryMapping
    public List<UserService.Traveller> myTravellers(@ContextValue UUID userId) {
        return service.getMyTravellers(userId);
    }

    @QueryMapping
    public int loyaltyBalance(@ContextValue UUID userId) {
        return service.getLoyaltyBalance(userId);
    }

    @QueryMapping
    public double walletBalance(@ContextValue UUID userId) {
        return service.getWalletBalance(userId);
    }

    @QueryMapping
    public List<UserService.BookingHistory> myBookings(@ContextValue UUID userId) {
        return service.getMyBookings(userId);
    }
}
