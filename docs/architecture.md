# TransitHub Architecture

## System Architecture

TransitHub follows a microservices architecture with 25+ services communicating via:
- **GraphQL**: All client-to-service communication
- **Kafka**: Event-driven asynchronous communication (Saga choreography)
- **Feign Clients**: Synchronous service-to-service calls
- **WebSocket/STOMP**: Real-time seat updates and notifications

### Key Design Decisions

1. **GraphQL-only**: No REST controllers anywhere. All data access through GraphQL queries, mutations, and subscriptions.
2. **Saga Pattern**: Choreography-based Saga with outbox pattern for reliable event publishing.
3. **CQRS**: MongoDB for reads (audit, tracking, notifications), PostgreSQL for transactional writes.
4. **AWS-Ready**: LocalStack for local dev, real AWS services in production via Spring profiles.
5. **Java 21 Features**: Records for DTOs, sealed classes for booking commands, pattern matching for fare rules, virtual threads for I/O.
