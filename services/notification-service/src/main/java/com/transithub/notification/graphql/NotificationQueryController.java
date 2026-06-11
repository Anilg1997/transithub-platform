package com.transithub.notification.graphql;
import com.transithub.notification.service.NotificationService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
public class NotificationQueryController {
    private final NotificationService service;
    public NotificationQueryController(NotificationService service) { this.service = service; }

    @QueryMapping
    public List<NotificationService.Notification> myNotifications(@ContextValue UUID userId,
            @Argument Integer page, @Argument Integer size) {
        return service.getMyNotifications(userId.toString(), page, size);
    }

    @QueryMapping
    public int unreadCount(@ContextValue UUID userId) {
        return service.getUnreadCount(userId.toString());
    }
}
