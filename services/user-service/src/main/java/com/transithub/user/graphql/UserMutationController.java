package com.transithub.user.graphql;
import com.transithub.user.service.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
public class UserMutationController {
    private final UserService service;
    public UserMutationController(UserService service) { this.service = service; }

    @MutationMapping
    public UserService.UserProfile updateProfile(@ContextValue UUID userId,
            @Argument String fullName, @Argument String dateOfBirth, @Argument String gender) {
        return service.updateProfile(userId, fullName, dateOfBirth, gender);
    }

    @MutationMapping
    public UserService.Traveller addTraveller(@ContextValue UUID userId,
            @Argument String name, @Argument int age, @Argument String gender,
            @Argument String idType, @Argument String idNumber) {
        return service.addTraveller(userId, name, age, gender, idType, idNumber);
    }

    @MutationMapping
    public UserService.Traveller updateTraveller(@Argument UUID id,
            @Argument String name, @Argument Integer age, @Argument String gender,
            @Argument String idType, @Argument String idNumber) {
        return service.updateTraveller(id, name, age, gender, idType, idNumber);
    }

    @MutationMapping
    public boolean deleteTraveller(@Argument UUID id) {
        return service.deleteTraveller(id);
    }

    @MutationMapping
    public UserService.Wallet addMoneyToWallet(@ContextValue UUID userId, @Argument double amount) {
        return service.addMoneyToWallet(userId, amount);
    }
}
