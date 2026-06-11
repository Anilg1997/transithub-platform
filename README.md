# TransitHub — All-in-One Transport Booking Platform

[![CI](https://github.com/YOUR_USERNAME/transithub-platform/actions/workflows/ci.yml/badge.svg)](https://github.com/YOUR_USERNAME/transithub-platform/actions/workflows/ci.yml)
[![Java 21](https://img.shields.io/badge/Java-21-blue)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot 3.3](https://img.shields.io/badge/Spring%20Boot-3.3-brightgreen)](https://spring.io/projects/spring-boot)
[![Angular 20](https://img.shields.io/badge/Angular-20-red)](https://angular.dev)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

TransitHub is an enterprise-grade, all-in-one transport ticket booking platform covering **Bus**, **Train**, and **Flight** bookings. Built with **Java 21**, **Spring Boot 3.3**, and **Angular 20**, it uses **GraphQL** exclusively for all data access, **Apache Kafka** for event-driven Saga choreography, and is **AWS-ready** with LocalStack emulation for local development.

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│  Angular 20 PWA (transithub-ui) — Apollo Client + STOMP WebSocket           │
│  http://localhost:4200                                                       │
└────────────────────────────────────┬────────────────────────────────────────┘
                                     │ GraphQL /graphql + /graphql-ws
┌────────────────────────────────────▼────────────────────────────────────────┐
│  API Gateway (Spring Cloud Gateway + JWT filter)              :8080         │
│  Routes: auth → user → flight → bus → train → payment → search → ...       │
└──────┬─────────┬────────┬────────┬────────┬─────────┬──────────┬──────────┘
       │         │        │        │        │         │          │
  auth-service user   flight  bus     train   payment  search    admin
  :8081       :8082  8083-85  8086-88 8089-91 8094-95  :8098     :8100
       │         │        │        │        │         │          │
       └─────────┴────────┴────────┴────────┴─────────┴──────────┘
                              │
                   Apache Kafka 3.7 (38 topics)
                   Saga Choreography + Outbox Pattern
                              │
       ┌──────────────────────┴────────────────────────────┐
       │                                                   │
  PostgreSQL 16                                     MongoDB 7
  (11 schemas, Flyway migrations)              (audit, docs, tracking)
       │                                                   │
    Redis 7                                     Elasticsearch 8
  (seat locks, JWT cache)                    (search, autocomplete)
       │
  S3 / LocalStack
  (tickets, boarding passes, receipts)
```

## Tech Stack

| Component | Technology | Version | Purpose |
|-----------|-----------|---------|---------|
| Language | Java | 21 LTS | Records, Sealed classes, Pattern matching, Virtual threads |
| Framework | Spring Boot | 3.3.5 | Microservices foundation |
| API Layer | Spring for GraphQL | 1.3.3 | GraphQL-only (no REST) |
| Service Discovery | Netflix Eureka | 2023.0 | Service registry |
| Config | Spring Cloud Config | 2023.0 | Centralized configuration |
| Gateway | Spring Cloud Gateway | 2023.0 | API gateway + JWT filter |
| RDBMS | PostgreSQL | 16 | Transactional data (Flyway migrations) |
| NoSQL | MongoDB | 7 | Audit logs, documents, tracking |
| Cache | Redis | 7 | Seat locks, JWT cache, rate limiting |
| Search | Elasticsearch / OpenSearch | 8.13 | Full-text search, autocomplete |
| Messaging | Apache Kafka | 3.7 | Event streaming, Saga choreography |
| PDF | iText7 | 8.0 | Tickets, boarding passes, receipts |
| Cloud | LocalStack / AWS | — | S3, SQS, SNS, SES, Secrets Manager |
| Monitoring | Zipkin + Prometheus + Grafana | — | Distributed tracing, metrics |
| Frontend | Angular | 20 | Standalone components, Signals |
| GraphQL Client | Apollo Angular | — | GraphQL queries, mutations, subscriptions |
| Charts | Chart.js + ng2-charts | 4/19 | Admin analytics dashboards |
| Map | Leaflet.js | — | Live bus tracking (free, no API key) |
| CI/CD | GitHub Actions | — | Build, test, deploy to EKS |
| IaC | Terraform | 1.6+ | AWS infrastructure as code |
| Containers | Docker + Kubernetes | — | Docker Compose (dev), EKS (prod) |

## Services Port Reference

| Service | Port | GraphQL Endpoint | Subscriptions | Description |
|---------|------|-----------------|---------------|-------------|
| service-registry | 8761 | — | — | Netflix Eureka Server |
| config-server | 8888 | — | — | Spring Cloud Config Server |
| api-gateway | 8080 | /graphql | /graphql-ws | Spring Cloud Gateway + JWT filter |
| auth-service | 8081 | /graphiql | — | Register, login, JWT, OTP mock |
| user-service | 8082 | /graphiql | — | Profile, travellers, wallet, loyalty |
| flight-inventory-service | 8083 | /graphiql | — | Flights, airports, seat maps |
| flight-booking-service | 8084 | /graphiql | flightBookingStatus | Flight booking lifecycle, PNR |
| flight-status-service | 8085 | /graphiql | flightStatusUpdate | Real-time flight status (Kafka) |
| bus-inventory-service | 8086 | /graphiql | — | Bus routes, operators, seat layouts |
| bus-booking-service | 8087 | /graphiql | busSeatLockStatus | Bus booking, seat locking |
| bus-tracking-service | 8088 | /graphiql | busLocation | Live GPS tracking (Kafka mock) |
| train-inventory-service | 8089 | /graphiql | — | Trains, stations, coaches, berths |
| train-booking-service | 8090 | /graphiql | — | Train booking, waitlist, PNR |
| train-availability-service | 8091 | /graphiql | seatAvailabilityUpdate | Real-time berth availability |
| booking-aggregator-service | 8092 | /graphiql | — | Multi-modal planning, combined ref |
| inventory-lock-service | 8093 | /graphiql | — | Redis seat locking (10 min TTL) |
| payment-service | 8094 | /graphiql | paymentStatusUpdate | Mock payment gateway, GST |
| refund-service | 8095 | /graphiql | — | Refund policy engine |
| document-service | 8096 | /graphiql | — | PDF generation (iText7) → S3 |
| notification-service | 8097 | /graphiql | newNotification | Email + in-app push (Kafka) |
| search-service | 8098 | /graphiql | — | Elasticsearch, autocomplete |
| analytics-service | 8099 | /graphiql | — | Revenue KPIs, JDBC aggregation |
| admin-service | 8100 | /graphiql | — | Inventory CRUD, user management |
| audit-service | 8101 | — | — | Kafka consumer → MongoDB audit logs |
| fare-engine-service | 8102 | /graphiql | — | Dynamic pricing, fare rules |

## Prerequisites

- **Java 21** (Temurin recommended)
- **Maven 3.9+**
- **Node.js 20** (for Angular frontend)
- **Angular CLI 20** (`npm install -g @angular/cli@20`)
- **Docker 24+** with Docker Compose v2
- **GitHub CLI** (optional, for repo setup)

## Local Setup

### 1. Clone the repository
```bash
git clone https://github.com/YOUR_USERNAME/transithub-platform.git
cd transithub-platform
```

### 2. Copy environment file
```bash
cp .env.example .env
```

### 3. Start infrastructure services
```bash
docker compose up -d postgres mongodb redis zookeeper kafka elasticsearch localstack zipkin prometheus grafana kafka-ui mongo-express pgadmin
```

### 4. Wait for Kafka to be ready
```bash
docker compose logs -f kafka | grep -q "started (kafka.server.KafkaServer)"
```

### 5. Build all services
```bash
mvn clean package -DskipTests
```

### 6. Start all application services
```bash
docker compose up -d
```

### 7. Start Angular frontend
```bash
cd frontend/transithub-ui
npm ci
npx ng serve
```

### 8. Access the application

| URL | Description |
|-----|-------------|
| http://localhost:4200 | Angular UI |
| http://localhost:8080/graphql | API Gateway GraphQL endpoint |
| http://localhost:8081/graphiql | Auth Service GraphiQL |
| http://localhost:8083/graphiql | Flight Inventory GraphiQL |
| http://localhost:8084/graphiql | Flight Booking GraphiQL |
| http://localhost:8086/graphiql | Bus Inventory GraphiQL |
| http://localhost:8089/graphiql | Train Inventory GraphiQL |
| http://localhost:8094/graphiql | Payment Service 
