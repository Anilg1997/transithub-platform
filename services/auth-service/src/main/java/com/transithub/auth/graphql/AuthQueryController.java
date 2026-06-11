package com.transithub.auth.graphql;
import com.transithub.auth.service.AuthService;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
public class AuthQueryController {
    private final AuthService service;
    public AuthQueryController(AuthService service) { this.service = service; }

    @QueryMapping
    public AuthService.UserProfile me(@ContextValue UUID userId) {
        return service.getUserProfile(userId);
    }
}
