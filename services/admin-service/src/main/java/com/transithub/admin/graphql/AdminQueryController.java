package com.transithub.admin.graphql;
import com.transithub.admin.service.AdminService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class AdminQueryController {
    private final AdminService service;
    public AdminQueryController(AdminService service) { this.service = service; }

    @QueryMapping
    public List<AdminService.FraudAlert> fraudAlerts() {
        return service.getFraudAlerts();
    }

    @QueryMapping
    public List<AdminService.AdminUser> allUsers(@Argument Integer page, @Argument Integer size) {
        return service.getAllUsers(page, size);
    }
}
