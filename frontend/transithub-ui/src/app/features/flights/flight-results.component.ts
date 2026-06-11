import { Component, signal, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgFor, CurrencyPipe, DatePipe, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-flight-results', standalone: true, imports: [NgFor, CurrencyPipe, DatePipe, NgIf],
  template: `
    <div class="container results-page">
      <div class="filters">
        <h2>Filters</h2>
        <div class="filter-group">
          <label>Price Range</label>
          <input type="range" min="1000" max="50000" class="slider">
        </div>
        <div class="filter-group">
          <label>Stops</label>
          <div class="checkbox-group">
            <label><input type="checkbox" checked> Non-stop</label>
            <label><input type="checkbox"> 1 Stop</label>
          </div>
        </div>
      </div>
      <div class="results">
        <div *ngIf="loading()" class="loading">Loading flights...</div>
        <div *ngIf="error()" class="error-msg">{{error()}}</div>
        <div *ngFor="let flight of flights()" class="flight-card" (click)="selectFlight(flight)">
          <div class="flight-header">
            <span class="airline">{{flight.airline}}</span>
            <span class="flight-number">{{flight.flightNumber}}</span>
          </div>
          <div class="flight-details">
            <div class="time-block">
              <span class="time">{{flight.departureTime}}</span>
              <span class="city">{{flight.origin?.code || flight.origin}}</span>
            </div>
            <div class="duration-block">
              <span class="duration">{{flight.duration}} min</span>
              <div class="line"></div>
              <span *ngIf="flight.stops > 0" class="stops">{{flight.stops}} stop(s)</span>
              <span *ngIf="flight.stops === 0" class="stops non-stop">Non-stop</span>
            </div>
            <div class="time-block">
              <span class="time">{{flight.arrivalTime}}</span>
              <span class="city">{{flight.destination?.code || flight.destination}}</span>
            </div>
            <div class="price-block">
              <span class="price">₹{{flight.totalFare}}</span>
              <span class="seats" [class.low]="flight.availableSeats < 10">{{flight.availableSeats}} seats left</span>
            </div>
          </div>
        </div>
        <div *ngIf="!loading() && flights().length === 0" class="no-results">No flights found. Try different dates or routes.</div>
      </div>
    </div>
  `,
  styles: [`
    .results-page { display: flex; gap: 32px; padding: 32px 0; }
    .filters { width: 260px; background: white; padding: 24px; border-radius: 12px; height: fit-content; }
    .filters h2 { font-size: 18px; margin-bottom: 20px; }
    .filter-group { margin-bottom: 20px; }
    .filter-group label { display: block; font-weight: 500; margin-bottom: 8px; color: var(--text-secondary); }
    .slider { width: 100%; }
    .checkbox-group label { display: block; margin-bottom: 8px; cursor: pointer; }
    .results { flex: 1; display: flex; flex-direction: column; gap: 16px; }
    .flight-card { background: white; border-radius: 12px; padding: 20px; cursor: pointer; transition: all 0.2s; border: 1px solid #E2E8F0; }
    .flight-card:hover { border-color: var(--primary); box-shadow: 0 4px 12px rgba(37,99,235,0.15); transform: translateY(-2px); }
    .flight-header { display: flex; justify-content: space-between; margin-bottom: 16px; }
    .airline { font-weight: 600; }
    .flight-number { color: var(--text-secondary); }
    .flight-details { display: flex; align-items: center; gap: 24px; }
    .time-block { text-align: center; }
    .time { display: block; font-size: 22px; font-weight: 700; }
    .city { font-size: 13px; color: var(--text-secondary); }
    .duration-block { flex: 1; text-align: center; }
    .duration { font-size: 14px; color: var(--text-secondary); }
    .line { border-top: 2px solid #E2E8F0; margin: 8px 0; }
    .stops { font-size: 12px; color: var(--text-secondary); }
    .non-stop { color: var(--accent); }
    .price-block { text-align: right; }
    .price { display: block; font-size: 22px; font-weight: 700; color: var(--primary); }
    .seats { font-size: 12px; }
    .low { color: var(--danger); font-weight: 600; }
    .loading, .no-results { text-align: center; padding: 40px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; margin-bottom: 16px; text-align: center; }
  `]
})
export class FlightResultsComponent implements OnInit {
  flights = signal<any[]>([]);
  loading = signal(true);
  error = signal('');
  private graphql = inject(GraphqlService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const origin = params['from'] || 'DEL';
      const dest = params['to'] || 'BOM';
      const date = params['date'] || new Date().toISOString().split('T')[0];
      this.loading.set(true);
      this.graphql.searchFlights(origin, dest, date).subscribe({
        next: (data: any) => {
          this.flights.set(data?.searchFlights || []);
          this.loading.set(false);
        },
        error: (err: any) => {
          this.error.set(err?.message || 'Failed to load flights');
          this.loading.set(false);
        }
      });
    });
  }

  selectFlight(flight: any) { this.router.navigate(['/booking/flight', flight.id]); }
}
