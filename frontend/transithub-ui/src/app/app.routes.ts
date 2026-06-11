import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', loadComponent: () => import('./features/home/home.component').then(m => m.HomeComponent) },
  { path: 'auth/login', loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent) },
  { path: 'auth/register', loadComponent: () => import('./features/auth/register.component').then(m => m.RegisterComponent) },
  { path: 'search/flights', loadComponent: () => import('./features/flights/flight-results.component').then(m => m.FlightResultsComponent) },
  { path: 'search/buses', loadComponent: () => import('./features/buses/bus-results.component').then(m => m.BusResultsComponent) },
  { path: 'search/trains', loadComponent: () => import('./features/trains/train-results.component').then(m => m.TrainResultsComponent) },
  { path: 'booking/flight/:id', loadComponent: () => import('./features/flights/flight-booking.component').then(m => m.FlightBookingComponent) },
  { path: 'booking/bus/:id', loadComponent: () => import('./features/buses/bus-booking.component').then(m => m.BusBookingComponent) },
  { path: 'booking/train/:id', loadComponent: () => import('./features/trains/train-booking.component').then(m => m.TrainBookingComponent) },
  { path: 'payment/:ref', loadComponent: () => import('./features/payment/payment.component').then(m => m.PaymentComponent) },
  { path: 'trips', loadComponent: () => import('./features/trips/trips.component').then(m => m.TripsComponent) },
  { path: 'admin', loadComponent: () => import('./features/admin/admin.component').then(m => m.AdminComponent) },
  { path: 'notifications', loadComponent: () => import('./features/notifications/notifications.component').then(m => m.NotificationsComponent) },
  { path: 'profile', loadComponent: () => import('./features/profile/profile.component').then(m => m.ProfileComponent) },
  { path: '**', redirectTo: '' },
];
