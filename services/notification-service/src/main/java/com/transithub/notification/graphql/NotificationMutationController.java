package com.transithub.notification.graphql;
import com.transithub.notification.service.NotificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationMutationController {
    private final NotificationService service;
    public NotificationMutationController(NotificationService service) { this.service = service; }

    @MutationMapping
    public NotificationService.Notification markRead(@Argument String notificationId) {
        return service.markRead(notificationId);
    }

    @MutationMapping
    public boolean markAllRead(@org.springframework.graphql.data.method.annotation.ContextValue java.util.UUID userId) {
        return service.markAllRead(userId.toString());
    }
}
