import { gql } from 'apollo-angular';

// ========== AUTH ==========
export const LOGIN = gql`
  mutation Login($emailOrPhone: String!, $password: String!) {
    login(emailOrPhone: $emailOrPhone, password: $password) {
      accessToken refreshToken expiresIn
      user { id email phone fullName role }
    }
  }
`;

export const REGISTER = gql`
  mutation Register($email: String!, $phone: String!, $fullName: String!, $password: String!) {
    register(email: $email, phone: $phone, fullName: $fullName, password: $password) {
      accessToken refreshToken expiresIn
      user { id email phone fullName role }
    }
  }
`;

export const ME = gql`
  query Me { me { id email phone fullName isActive role } }
`;

// ========== FLIGHTS ==========
export const SEARCH_FLIGHTS = gql`
  query SearchFlights($origin: String!, $destination: String!, $date: String!, $cabinClass: String, $passengers: Int) {
    searchFlights(origin: $origin, destination: $destination, date: $date, cabinClass: $cabinClass, passengers: $passengers) {
      id flightNumber airline origin { code name city } destination { code name city }
      departureTime arrivalTime duration stops cabinClass totalSeats availableSeats
      baseFare taxes totalFare isRefundable cancellationFee
    }
  }
`;

export const FLIGHT_DETAIL = gql`
  query FlightDetail($flightId: ID!) {
    flightDetail(flightId: $flightId) {
      id flightNumber airline airlineCode origin { code name city } destination { code name city }
      departureTime arrivalTime duration stops cabinClass totalSeats availableSeats
      baseFare taxes totalFare isRefundable cancellationFee
    }
  }
`;

export const SEAT_MAP = gql`
  query SeatMap($flightId: ID!, $date: String!) {
    seatMap(flightId: $flightId, date: $date) {
      rows { rowNumber seats { seatNumber type isAvailable extraLegroom price } }
    }
  }
`;

export const BOOK_FLIGHT = gql`
  mutation BookFlight($flightId: ID!, $date: String!, $cabinClass: String!, $passengers: [PassengerInput!]!, $seatNumbers: [String!]) {
    bookFlight(input: { flightId: $flightId, date: $date, cabinClass: $cabinClass, passengers: $passengers, seatNumbers: $seatNumbers }) {
      id bookingRef pnr status totalFare
    }
  }
`;

export const MY_FLIGHT_BOOKINGS = gql`
  query MyFlightBookings {
    myFlightBookings { id bookingRef pnr flightId status seatNumbers totalFare bookedAt }
  }
`;

export const CANCEL_FLIGHT = gql`
  mutation CancelFlight($bookingRef: String!) {
    cancelFlight(bookingRef: $bookingRef) { id bookingRef status }
  }
`;

// ========== BUSES ==========
export const SEARCH_BUSES = gql`
  query SearchBuses($origin: String!, $destination: String!, $date: String!) {
    searchBuses(origin: $origin, destination: $destination, date: $date) {
      id operator busType operatorRating origin destination departureTime arrivalTime
      duration totalSeats availableSeats fare boardingPoints droppingPoints cancellationPolicy
    }
  }
`;

export const BUS_SEAT_MAP = gql`
  query BusSeatMap($busId: ID!, $date: String!) {
    busSeatMap(busId: $busId, date: $date) { seatNumber type deck isAvailable price }
  }
`;

export const BOOK_BUS = gql`
  mutation BookBus($busId: ID!, $date: String!, $seats: [String!]!, $boardingPoint: String!, $droppingPoint: String!, $passengers: [PassengerInput!]!) {
    bookBus(input: { busId: $busId, date: $date, seats: $seats, boardingPoint: $boardingPoint, droppingPoint: $droppingPoint, passengers: $passengers }) {
      id bookingRef status totalFare
    }
  }
`;

export const MY_BUS_BOOKINGS = gql`
  query MyBusBookings {
    myBusBookings { id bookingRef busId status seats totalFare bookedAt }
  }
`;

// ========== TRAINS ==========
export const SEARCH_TRAINS = gql`
  query SearchTrains($from: String!, $to: String!, $date: String!) {
    searchTrains(from: $from, to: $to, date: $date) {
      id trainNumber trainName origin { code name city } destination { code name city }
      departureTime arrivalTime duration runsOn coaches { coachType totalBerths availableBerths fare }
    }
  }
`;

export const COACH_MAP = gql`
  query CoachMap($trainId: ID!, $date: String!, $coachType: String!) {
    coachMap(trainId: $trainId, date: $date, coachType: $coachType) { berthNumber type isAvailable price }
  }
`;

export const BOOK_TRAIN = gql`
  mutation BookTrain($trainId: ID!, $date: String!, $coachType: String!, $quota: String!, $passengers: [PassengerInput!]!, $berthPreferences: [String!]) {
    bookTrain(input: { trainId: $trainId, date: $date, coachType: $coachType, quota: $quota, passengers: $passengers, berthPreferences: $berthPreferences }) {
      id bookingRef pnr status coach totalFare
    }
  }
`;

export const MY_TRAIN_BOOKINGS = gql`
  query MyTrainBookings {
    myTrainBookings { id bookingRef pnr trainId status coach berths totalFare bookedAt waitlistNumber }
  }
`;

export const CHECK_PNR = gql`
  query CheckPnr($pnr: String!) {
    checkPnrStatus(pnr: $pnr) { id bookingRef pnr status coach berths totalFare }
  }
`;

// ========== PAYMENT ==========
export const INITIATE_PAYMENT = gql`
  mutation InitiatePayment($bookingRef: String!, $amount: Float!, $method: String!) {
    initiatePayment(bookingRef: $bookingRef, amount: $amount, method: $method) {
      id transactionId totalAmount status
    }
  }
`;

export const CONFIRM_PAYMENT = gql`
  mutation ConfirmPayment($transactionId: String!, $mockSuccess: Boolean) {
    confirmPayment(transactionId: $transactionId, mockSuccess: $mockSuccess) {
      id transactionId status
    }
  }
`;

export const PAYMENT_STATUS = gql`
  query PaymentStatus($transactionId: String!) {
    paymentStatus(transactionId: $transactionId) { id transactionId amount gst totalAmount status method }
  }
`;

export const MY_PAYMENTS = gql`
  query MyPayments {
    myPayments { id transactionId bookingRef amount totalAmount status method initiatedAt }
  }
`;

// ========== SEARCH ==========
export const AUTOCOMPLETE = gql`
  query Autocomplete($query: String!, $type: String) {
    autocomplete(query: $query, type: $type) { code name type city country }
  }
`;

export const POPULAR_ROUTES = gql`
  query PopularRoutes($mode: String, $limit: Int) {
    popularRoutes(mode: $mode, limit: $limit) { from to mode avgFare bookingsCount }
  }
`;

// ========== USER ==========
export const MY_PROFILE = gql`
  query MyProfile { myProfile { id fullName dateOfBirth gender loyaltyPoints loyaltyTier } }
`;

export const UPDATE_PROFILE = gql`
  mutation UpdateProfile($fullName: String, $dateOfBirth: String, $gender: String) {
    updateProfile(fullName: $fullName, dateOfBirth: $dateOfBirth, gender: $gender) { id fullName }
  }
`;

export const WALLET_BALANCE = gql`
  query WalletBalance { walletBalance }
`;

export const ADD_MONEY = gql`
  mutation AddMoney($amount: Float!) {
    addMoneyToWallet(amount: $amount) { id balance }
  }
`;

export const MY_TRAVELLERS = gql`
  query MyTravellers {
    myTravellers { id name age gender idType idNumber isPrimary }
  }
`;

export const ADD_TRAVELLER = gql`
  mutation AddTraveller($name: String!, $age: Int!, $gender: String!, $idType: String!, $idNumber: String!) {
    addTraveller(name: $name, age: $age, gender: $gender, idType: $idType, idNumber: $idNumber) { id name }
  }
`;

export const DELETE_TRAVELLER = gql`
  mutation DeleteTraveller($id: ID!) { deleteTraveller(id: $id) }
`;

// ========== TRIPS / BOOKING AGGREGATOR ==========
export const MY_TRIPS = gql`
  query MyBookings {
    myBookings { id combinedRef segments { mode bookingRef status fare } totalFare status createdAt }
  }
`;

// ========== NOTIFICATIONS ==========
export const MY_NOTIFICATIONS = gql`
  query MyNotifications($page: Int, $size: Int) {
    myNotifications(page: $page, size: $size) { id type title message isRead createdAt }
  }
`;

export const UNREAD_COUNT = gql`
  query UnreadCount { unreadCount }
`;

export const MARK_READ = gql`
  mutation MarkRead($notificationId: ID!) {
    markRead(notificationId: $notificationId) { id isRead }
  }
`;

export const MARK_ALL_READ = gql`
  mutation MarkAllRead { markAllRead }
`;

// ========== SUBSCRIPTIONS ==========
export const FLIGHT_STATUS_SUB = gql`
  subscription FlightStatus($flightNumber: String!) {
    flightStatusUpdate(flightNumber: $flightNumber) { flightNumber status delayMinutes gate updatedAt }
  }
`;

export const PAYMENT_STATUS_SUB = gql`
  subscription PaymentStatus($transactionId: String!) {
    paymentStatusUpdate(transactionId: $transactionId) { id status }
  }
`;

export const NEW_NOTIFICATION_SUB = gql`
  subscription NewNotification {
    newNotification { id type title message createdAt }
  }
`;

// ========== ADMIN ==========
export const FRAUD_ALERTS = gql`
  query FraudAlerts { fraudAlerts { id userId alertType description severity isResolved createdAt } }
`;

export const ALL_USERS = gql`
  query AllUsers($page: Int, $size: Int) {
    allUsers(page: $page, size: $size) { id email phone fullName isActive role createdAt }
  }
`;
