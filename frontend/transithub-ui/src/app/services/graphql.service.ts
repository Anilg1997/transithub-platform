import { Injectable, inject } from '@angular/core';
import { Apollo } from 'apollo-angular';
import { Observable, map } from 'rxjs';
import {
  LOGIN, REGISTER, ME,
  SEARCH_FLIGHTS, FLIGHT_DETAIL, SEAT_MAP, BOOK_FLIGHT, MY_FLIGHT_BOOKINGS, CANCEL_FLIGHT,
  SEARCH_BUSES, BUS_SEAT_MAP, BOOK_BUS, MY_BUS_BOOKINGS,
  SEARCH_TRAINS, COACH_MAP, BOOK_TRAIN, MY_TRAIN_BOOKINGS, CHECK_PNR,
  INITIATE_PAYMENT, CONFIRM_PAYMENT, PAYMENT_STATUS, MY_PAYMENTS,
  AUTOCOMPLETE, POPULAR_ROUTES,
  MY_PROFILE, UPDATE_PROFILE, WALLET_BALANCE, ADD_MONEY,
  MY_TRAVELLERS, ADD_TRAVELLER, DELETE_TRAVELLER,
  MY_TRIPS,
  MY_NOTIFICATIONS, UNREAD_COUNT, MARK_READ, MARK_ALL_READ,
  FRAUD_ALERTS, ALL_USERS,
} from '../graphql/operations';

@Injectable({ providedIn: 'root' })
export class GraphqlService {
  private apollo = inject(Apollo);

  // Auth
  login(emailOrPhone: string, password: string): Observable<any> {
    return this.apollo.mutate({ mutation: LOGIN, variables: { emailOrPhone, password } }).pipe(map(r => r.data));
  }
  register(email: string, phone: string, fullName: string, password: string): Observable<any> {
    return this.apollo.mutate({ mutation: REGISTER, variables: { email, phone, fullName, password } }).pipe(map(r => r.data));
  }
  me(): Observable<any> {
    return this.apollo.watchQuery({ query: ME }).valueChanges.pipe(map(r => r.data));
  }

  // Flights
  searchFlights(origin: string, destination: string, date: string, cabinClass?: string, passengers?: number): Observable<any> {
    return this.apollo.watchQuery({ query: SEARCH_FLIGHTS, variables: { origin, destination, date, cabinClass, passengers } }).valueChanges.pipe(map(r => r.data));
  }
  flightDetail(flightId: string): Observable<any> {
    return this.apollo.watchQuery({ query: FLIGHT_DETAIL, variables: { flightId } }).valueChanges.pipe(map(r => r.data));
  }
  seatMap(flightId: string, date: string): Observable<any> {
    return this.apollo.watchQuery({ query: SEAT_MAP, variables: { flightId, date } }).valueChanges.pipe(map(r => r.data));
  }
  bookFlight(input: any): Observable<any> {
    return this.apollo.mutate({ mutation: BOOK_FLIGHT, variables: input }).pipe(map(r => r.data));
  }
  myFlightBookings(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_FLIGHT_BOOKINGS }).valueChanges.pipe(map(r => r.data));
  }
  cancelFlight(bookingRef: string): Observable<any> {
    return this.apollo.mutate({ mutation: CANCEL_FLIGHT, variables: { bookingRef } }).pipe(map(r => r.data));
  }

  // Buses
  searchBuses(origin: string, destination: string, date: string): Observable<any> {
    return this.apollo.watchQuery({ query: SEARCH_BUSES, variables: { origin, destination, date } }).valueChanges.pipe(map(r => r.data));
  }
  busSeatMap(busId: string, date: string): Observable<any> {
    return this.apollo.watchQuery({ query: BUS_SEAT_MAP, variables: { busId, date } }).valueChanges.pipe(map(r => r.data));
  }
  bookBus(input: any): Observable<any> {
    return this.apollo.mutate({ mutation: BOOK_BUS, variables: input }).pipe(map(r => r.data));
  }
  myBusBookings(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_BUS_BOOKINGS }).valueChanges.pipe(map(r => r.data));
  }

  // Trains
  searchTrains(from: string, to: string, date: string): Observable<any> {
    return this.apollo.watchQuery({ query: SEARCH_TRAINS, variables: { from, to, date } }).valueChanges.pipe(map(r => r.data));
  }
  coachMap(trainId: string, date: string, coachType: string): Observable<any> {
    return this.apollo.watchQuery({ query: COACH_MAP, variables: { trainId, date, coachType } }).valueChanges.pipe(map(r => r.data));
  }
  bookTrain(input: any): Observable<any> {
    return this.apollo.mutate({ mutation: BOOK_TRAIN, variables: input }).pipe(map(r => r.data));
  }
  myTrainBookings(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_TRAIN_BOOKINGS }).valueChanges.pipe(map(r => r.data));
  }
  checkPnr(pnr: string): Observable<any> {
    return this.apollo.watchQuery({ query: CHECK_PNR, variables: { pnr } }).valueChanges.pipe(map(r => r.data));
  }

  // Payments
  initiatePayment(bookingRef: string, amount: number, method: string): Observable<any> {
    return this.apollo.mutate({ mutation: INITIATE_PAYMENT, variables: { bookingRef, amount, method } }).pipe(map(r => r.data));
  }
  confirmPayment(transactionId: string, mockSuccess?: boolean): Observable<any> {
    return this.apollo.mutate({ mutation: CONFIRM_PAYMENT, variables: { transactionId, mockSuccess } }).pipe(map(r => r.data));
  }
  paymentStatus(transactionId: string): Observable<any> {
    return this.apollo.watchQuery({ query: PAYMENT_STATUS, variables: { transactionId } }).valueChanges.pipe(map(r => r.data));
  }
  myPayments(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_PAYMENTS }).valueChanges.pipe(map(r => r.data));
  }

  // Search
  autocomplete(query: string, type?: string): Observable<any> {
    return this.apollo.watchQuery({ query: AUTOCOMPLETE, variables: { query, type } }).valueChanges.pipe(map(r => r.data));
  }
  popularRoutes(mode?: string, limit?: number): Observable<any> {
    return this.apollo.watchQuery({ query: POPULAR_ROUTES, variables: { mode, limit } }).valueChanges.pipe(map(r => r.data));
  }

  // User
  myProfile(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_PROFILE }).valueChanges.pipe(map(r => r.data));
  }
  updateProfile(fullName?: string, dateOfBirth?: string, gender?: string): Observable<any> {
    return this.apollo.mutate({ mutation: UPDATE_PROFILE, variables: { fullName, dateOfBirth, gender } }).pipe(map(r => r.data));
  }
  walletBalance(): Observable<any> {
    return this.apollo.watchQuery({ query: WALLET_BALANCE }).valueChanges.pipe(map(r => r.data));
  }
  addMoneyToWallet(amount: number): Observable<any> {
    return this.apollo.mutate({ mutation: ADD_MONEY, variables: { amount } }).pipe(map(r => r.data));
  }
  myTravellers(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_TRAVELLERS }).valueChanges.pipe(map(r => r.data));
  }
  addTraveller(name: string, age: number, gender: string, idType: string, idNumber: string): Observable<any> {
    return this.apollo.mutate({ mutation: ADD_TRAVELLER, variables: { name, age, gender, idType, idNumber } }).pipe(map(r => r.data));
  }
  deleteTraveller(id: string): Observable<any> {
    return this.apollo.mutate({ mutation: DELETE_TRAVELLER, variables: { id } }).pipe(map(r => r.data));
  }

  // Trips
  myTrips(): Observable<any> {
    return this.apollo.watchQuery({ query: MY_TRIPS }).valueChanges.pipe(map(r => r.data));
  }

  // Notifications
  myNotifications(page?: number, size?: number): Observable<any> {
    return this.apollo.watchQuery({ query: MY_NOTIFICATIONS, variables: { page, size } }).valueChanges.pipe(map(r => r.data));
  }
  unreadCount(): Observable<any> {
    return this.apollo.watchQuery({ query: UNREAD_COUNT }).valueChanges.pipe(map(r => r.data));
  }
  markRead(notificationId: string): Observable<any> {
    return this.apollo.mutate({ mutation: MARK_READ, variables: { notificationId } }).pipe(map(r => r.data));
  }
  markAllRead(): Observable<any> {
    return this.apollo.mutate({ mutation: MARK_ALL_READ }).pipe(map(r => r.data));
  }

  // Admin
  fraudAlerts(): Observable<any> {
    return this.apollo.watchQuery({ query: FRAUD_ALERTS }).valueChanges.pipe(map(r => r.data));
  }
  allUsers(page?: number, size?: number): Observable<any> {
    return this.apollo.watchQuery({ query: ALL_USERS, variables: { page, size } }).valueChanges.pipe(map(r => r.data));
  }
}
