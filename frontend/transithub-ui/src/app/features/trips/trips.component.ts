import { Component, signal } from '@angular/core';
import { NgFor, DatePipe, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-trips', standalone: true, imports: [NgFor, DatePipe, CurrencyPipe],
  template: `
    <div class="container trips-page">
      <h1>My Trips</h1>
      <div class="trip-tabs">
        <button *ngFor="let tab of tabs" (click)="activeTab.set(tab)" 
                [class.active]="activeTab() === tab" class="tab-btn">{{tab}}</button>
      </div>
      <div class="trip-list">
        <div *ngFor="let trip of trips()" class="trip-card">
          <div class="trip-header">
            <span class="mode-badge" [class.flight]="trip.mode==='FLIGHT'" [class.bus]="trip.mode==='BUS'" [class.train]="trip.mode==='TRAIN'">
              {{trip.mode === 'FLIGHT' ? '✈️' : trip.mode === 'BUS' ? '🚌' : '🚄'}} {{trip.mode}}
            </span>
            <span class="status" [class.confirmed]="trip.status==='CONFIRMED'" [class.cancelled]="trip.status==='CANCELLED'">{{trip.status}}</span>
          </div>
          <div class="trip-route">
            <span class="city">{{trip.from}}</span>
            <span class="arrow">→</span>
            <span class="city">{{trip.to}}</span>
          </div>
          <div class="trip-info">
            <span>Ref: {{trip.bookingRef}}</span>
            <span class="fare">₹{{trip.totalFare}}</span>
          </div>
          <div class="trip-actions">
            <button class="btn-outline">View Details</button>
            <button *ngIf="trip.status === 'CONFIRMED'" class="btn-danger">Cancel</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .trips-page { padding: 40px 0; }
    .trips-page h1 { font-size: 32px; margin-bottom: 24px; }
    .trip-tabs { display: flex; gap: 8px; margin-bottom: 24px; }
    .tab-btn { padding: 10px 24px; border: 2px solid #E2E8F0; border-radius: 8px; background: white; cursor: pointer; font-weight: 500; }
    .tab-btn.active { border-color: var(--primary); color: var(--primary); background: #EFF6FF; }
    .trip-list { display: flex; flex-direction: column; gap: 16px; }
    .trip-card { background: white; border-radius: 12px; padding: 20px; border: 1px solid #E2E8F0; }
    .trip-header { display: flex; justify-content: space-between; margin-bottom: 12px; }
    .mode-badge { padding: 4px 12px; border-radius: 20px; font-weight: 600; font-size: 13px; }
    .status { font-weight: 600; font-size: 14px; }
    .confirmed { color: var(--accent); }
    .cancelled { color: var(--danger); }
    .trip-route { font-size: 18px; font-weight: 600; margin-bottom: 8px; }
    .arrow { margin: 0 12px; color: var(--text-secondary); }
    .trip-info { display: flex; justify-content: space-between; color: var(--text-secondary); font-size: 14px; margin-bottom: 16px; }
    .fare { font-weight: 700; color: var(--text); }
    .trip-actions { display: flex; gap: 12px; }
    .btn-outline { border: 2px solid var(--primary); color: var(--primary); padding: 8px 20px; border-radius: 8px; background: transparent; cursor: pointer; font-weight: 600; }
    .btn-danger { border: 2px solid var(--danger); color: var(--danger); padding: 8px 20px; border-radius: 8px; background: transparent; cursor: pointer; font-weight: 600; }
  `]
})
export class TripsComponent {
  tabs = ['All', 'Flights', 'Buses', 'Trains'];
  activeTab = signal('All');
  trips = signal([
    { id: '1', bookingRef: 'BK-A1B2C3', mode: 'FLIGHT', from: 'DEL', to: 'BOM', status: 'CONFIRMED', totalFare: 5500, date: '2024-12-15' },
    { id: '2', bookingRef: 'BK-D4E5F6', mode: 'BUS', from: 'Mumbai', to: 'Pune', status: 'CONFIRMED', totalFare: 800, date: '2024-12-20' },
    { id: '3', bookingRef: 'BK-G7H8I9', mode: 'TRAIN', from: 'NDLS', to: 'HWH', status: 'CANCELLED', totalFare: 1800, date: '2024-11-10' },
  ]);
}
