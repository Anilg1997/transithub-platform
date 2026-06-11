# TransitHub Platform

Enterprise-grade, all-in-one transport ticket booking platform for Bus, Train & Flight.

## Architecture

- **Backend:** 25+ Java 21 / Spring Boot 3.3 microservices with GraphQL (no REST)
- **Frontend:** Angular 20 with Apollo Client, signal-based state management
- **Messaging:** Apache Kafka for event-driven Saga choreography
- **Databases:** PostgreSQL (transactional), MongoDB (audit/tracking), Redis (caching/locking)
- **Search:** Elasticsearch for autocomplete & popular routes
- **Infrastructure:** Docker Compose, Terraform (AWS EKS/RDS/ECR), GitHub Actions CI/CD

## Microservices

| Service | Purpose | Port |
|---------|---------|------|
| api-gateway | Entry point, JWT auth, route by X-Service header | 8080 |
| service-registry | Eureka discovery | 8761 |
| config-server | Centralized config | 8888 |
| auth-service | Login, register, JWT tokens | 8081 |
| user-service | Profile, travellers, wallet | 8082 |
| flight-inventory-service | Flight search, seat maps | 8083 |
| flight-booking-service | Flight booking, cancellation | 8084 |
| flight-status-service | Real-time flight status (WebSocket) | 8085 |
| bus-inventory-service | Bus search, seat maps | 8086 |
| bus-booking-service | Bus booking | 8087 |
| bus-tracking-service | Real-time bus tracking (WebSocket) | 8088 |
| train-inventory-service | Train search, coach maps | 8089 |
| train-booking-service | Train booking, PNR check | 8090 |
| train-availability-service | Train availability | 8091 |
| payment-service | Payment processing, GST | 8092 |
| refund-service | Refund estimation & processing | 8093 |
| search-service | Elasticsearch autocomplete | 8094 |
| notification-service | Push notifications (MongoDB) | 8095 |
| admin-service | User management, fraud alerts | 8096 |
| analytics-service | Revenue, bookings, top routes | 8097 |
| audit-service | Audit logging (Kafka consumer) | 8098 |
| fare-engine-service | Fare calculation with Redis cache | 8099 |
| inventory-lock-service | Seat locking with Redis TTL | 8100 |
| document-service | S3 document storage | 8101 |
| booking-aggregator-service | Multi-modal trip planning | 8102 |

## API Gateway Routing

All GraphQL requests go through the API gateway (port 8080) and are routed to the correct microservice using the `X-Service` HTTP header:

| X-Service Header | Routes To |
|------------------|----------|
| auth | auth-service |
| user | user-service |
| flight-inventory | flight-inventory-service |
| flight-booking | flight-booking-service |
| bus-inventory | bus-inventory-service |
| bus-booking | bus-booking-service |
| train-inventory | train-inventory-service |
| train-booking | train-booking-service |
| payment | payment-service |
| search | search-service |
| notification | notification-service |
| admin | admin-service |
| booking-aggregator | booking-aggregator-service |

## Quick Start

### Prerequisites
- Docker & Docker Compose
- Java 21+
- Node.js 20+
- Angular CLI
- Maven

### 1. Start Infrastructure

```bash
docker-compose up -d postgres mongodb redis kafka zookeeper elasticsearch localstack
```

### 2. Start Backend Services

```bash
# Start service registry and config server first
cd services/service-registry && mvn spring-boot:run &
cd services/config-server && mvn spring-boot:run &

# Start all other services
for service in services/*/; do
  cd "$service" && mvn spring-boot:run &
  cd ../..
done
```

### 3. Start Frontend

```bash
cd frontend/transithub-ui
npm install --legacy-peer-deps
ng serve
```

Open http://localhost:4200

## Frontend

The Angular 20 frontend connects to the backend via Apollo GraphQL with service-based routing:

- **Home:** Search flights, buses, trains with popular routes
- **Auth:** Login/Register with JWT authentication
- **Search Results:** Filtered results with real-time seat availability
- **Booking:** Passenger details, seat selection, fare calculation
- **Payment:** Multi-method payment with GST calculation
- **Trips:** View all bookings with tab filtering
- **Notifications:** Real-time notifications with mark-as-read
- **Profile:** User profile, travellers, wallet top-up
- **Admin:** Dashboard with user management and fraud alerts

## Tech Stack

| Layer | Technology |
|-------|------------|
| API | GraphQL (exclusively) |
| Backend | Java 21, Spring Boot 3.3, Spring Cloud Gateway |
| Frontend | Angular 20, Apollo Client 3, NgRx 19 |
| Auth | JWT (RS256), BCrypt |
| Database | PostgreSQL 16, MongoDB 7, Redis 7 |
| Search | Elasticsearch 8 |
| Messaging | Apache Kafka (Saga choreography) |
| Cache | Redis (fare engine, seat locking) |
| Storage | AWS S3 (LocalStack for dev) |
| Monitoring | Zipkin, Prometheus, Grafana |
| Deploy | Docker Compose, Terraform (AWS EKS), GitHub Actions |

## License

MIT
