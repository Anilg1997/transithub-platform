# TransitHub GraphQL Playground

## Authentication

### Register
```graphql
mutation { register(email: "user@example.com", phone: "+919999999999", fullName: "John Doe", password: "SecurePass123!") { accessToken refreshToken user { id email fullName } } }
```

### Login
```graphql
mutation { login(emailOrPhone: "user@example.com", password: "SecurePass123!") { accessToken refreshToken user { id email fullName role } } }
```

## Booking Search

### Flights
```graphql
query { searchFlights(origin: "DEL", destination: "BOM", date: "2024-12-15", cabinClass: ECONOMY, passengers: 1) { id flightNumber airline departureTime arrivalTime duration totalFare availableSeats } }
```

### Buses
```graphql
query { searchBuses(origin: "Mumbai", destination: "Pune", date: "2024-12-15") { id operator busType departureTime arrivalTime fare availableSeats } }
```

### Trains
```graphql
query { searchTrains(from: "NDLS", to: "BCT", date: "2024-12-15") { id trainNumber trainName departureTime arrivalTime coaches { coachType availableBerths fare } } }
```

## Payment

### Initiate
```graphql
mutation { initiatePayment(input: { bookingRef: "BK-ABC123", amount: 5500.0, method: UPI }) { transactionId amount gst convenienceFee totalAmount status } }
```
