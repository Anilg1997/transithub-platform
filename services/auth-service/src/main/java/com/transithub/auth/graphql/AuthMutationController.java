package com.transithub.auth.graphql;
import com.transithub.auth.service.AuthService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
public class AuthMutationController {
    private final AuthService service;
    public AuthMutationController(AuthService service) { this.service = service; }

    @MutationMapping
    public AuthService.AuthPayload register(@Argument String email, @Argument String phone,
                                             @Argument String fullName, @Argument String password) {
        return service.register(email, phone, fullName, password);
    }

    @MutationMapping
    public AuthService.AuthPayload login(@Argument String emailOrPhone, @Argument String password) {
        return service.login(emailOrPhone, password);
    }

    @MutationMapping
    public AuthService.AuthPayload refreshToken(@Argument String refreshToken) {
        return service.refreshToken(refreshToken);
    }

    @MutationMapping
    public boolean logout(@ContextValue UUID userId) {
        service.logout(userId);
        return true;
    }

    @MutationMapping
    public boolean verifyOtp(@Argument String phone, @Argument String otp) {
        return service.verifyOtp(phone, otp);
    }
}
