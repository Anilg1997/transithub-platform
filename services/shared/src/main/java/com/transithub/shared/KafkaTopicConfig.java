package com.transithub.shared;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {
    
    @Bean
    public NewTopic flightSeatsLocked() { return new NewTopic("th.flight.seats-locked", 3, (short) 1); }
    
    @Bean
    public NewTopic flightBookingConfirmed() { return new NewTopic("th.flight.booking-confirmed", 3, (short) 1); }
    
    @Bean
    public NewTopic flightBookingCancelled() { return new NewTopic("th.flight.booking-cancelled", 3, (short) 1); }
    
    @Bean
    public NewTopic flightCheckinCompleted() { return new NewTopic("th.flight.checkin-completed", 3, (short) 1); }
    
    @Bean
    public NewTopic busSeatsLocked() { return new NewTopic("th.bus.seats-locked", 3, (short) 1); }
    
    @Bean
    public NewTopic busBookingConfirmed() { return new NewTopic("th.bus.booking-confirmed", 3, (short) 1); }
    
    @Bean
    public NewTopic busLocationUpdated() { return new NewTopic("th.bus.location-updated", 3, (short) 1); }
    
    @Bean
    public NewTopic trainBerthsLocked() { return new NewTopic("th.train.berths-locked", 3, (short) 1); }
    
    @Bean
    public NewTopic trainBookingConfirmed() { return new NewTopic("th.train.booking-confirmed", 3, (short) 1); }
    
    @Bean
    public NewTopic trainWaitlistAdded() { return new NewTopic("th.train.waitlist-added", 3, (short) 1); }
    
    @Bean
    public NewTopic paymentInitiated() { return new NewTopic("th.payment.initiated", 3, (short) 1); }
    
    @Bean
    public NewTopic paymentSuccess() { return new NewTopic("th.payment.success", 3, (short) 1); }
    
    @Bean
    public NewTopic paymentFailed() { return new NewTopic("th.payment.failed", 3, (short) 1); }
    
    @Bean
    public NewTopic paymentRefundCompleted() { return new NewTopic("th.payment.refund-completed", 3, (short) 1); }
    
    @Bean
    public NewTopic documentsGenerated() { return new NewTopic("th.documents.generated", 3, (short) 1); }
    
    @Bean
    public NewTopic documentsFailed() { return new NewTopic("th.documents.generation-failed", 3, (short) 1); }
    
    @Bean
    public NewTopic notificationsSend() { return new NewTopic("th.notifications.send", 3, (short) 1); }
    
    @Bean
    public NewTopic searchRouteIndexed() { return new NewTopic("th.search.route-indexed", 3, (short) 1); }
    
    @Bean
    public NewTopic auditEvents() { return new NewTopic("th.audit.events", 3, (short) 1); }
}
