package com.transithub.notification.graphql;
import com.transithub.notification.service.NotificationService;
import org.reactivestreams.Publisher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import java.util.UUID;

@Controller
public class NotificationSubscriptionController {
    private final NotificationService service;
    public NotificationSubscriptionController(NotificationService service) { this.service = service; }

    @SubscriptionMapping
    public Publisher<NotificationService.Notification> newNotification(@ContextValue UUID userId) {
        return Flux.interval(java.time.Duration.ofSeconds(5))
            .map(i -> {
                var list = service.getMyNotifications(userId.toString(), 0, 1);
                return list.isEmpty() ? null : list.get(0);
            })
            .filter(n -> n != null && !n.isRead())
            .distinctUntilChanged();
    }
}
