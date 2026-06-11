import { Component, signal, OnInit, effect } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-trips', standalone: true, imports: [NgFor, NgIf],
  template: `
    <div class="container trips-page">
      <h1>My Trips</h1>
      <div *ngIf="loading()" class="loading">Loading trips...</div>
      <div *ngIf="error()" class="error-msg">{{error()}}</div>
      <div class="trip-tabs">
        <button *ngFor="let tab of tabs" (click)="activeTab.set(tab)"
                [class.active]="activeTab() === tab" class="tab-btn">{{tab}}</button>
      </div>
      <div class="trip-list">
        <div *ngFor="let trip of filteredTrips()" class="trip-card">
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
        </div>
        <div *ngIf="!loading() && filteredTrips().length === 0" class="no-trips">No trips found.</div>
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
    .loading, .no-trips { text-align: center; padding: 40px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; text-align: center; margin-bottom: 16px; }
  `]
})
export class TripsComponent implements OnInit {
  tabs = ['All', 'Flights', 'Buses', 'Trains'];
  activeTab = signal('All');
  trips = signal<any[]>([]);
  filteredTrips = signal<any[]>([]);
  loading = signal(true);
  error = signal('');
  private graphql = inject(GraphqlService);

  constructor() {
    effect(() => {
      const tab = this.activeTab();
      const all = this.trips();
      if (tab === 'All') {
        this.filteredTrips.set(all);
      } else {
        const mode = tab.replace('s', '');
        this.filteredTrips.set(all.filter(t => t.mode === mode));
      }
    });
  }

  ngOnInit() {
    this.graphql.myTrips().subscribe({
      next: (data: any) => {
        const raw = data?.myBookings || [];
        const mapped = raw.map((b: any) => ({
          id: b.id, bookingRef: b.combinedRef || b.segments?.[0]?.bookingRef || '',
          mode: b.segments?.[0]?.mode || 'FLIGHT',
          from: b.segments?.[0]?.mode || '',
          to: '',
          status: b.status || 'CONFIRMED',
          totalFare: b.totalFare || 0,
          date: b.createdAt,
        }));
        this.trips.set(mapped);
        this.loading.set(false);
      },
      error: (err: any) => {
        this.error.set(err?.message || 'Failed to load trips');
        this.loading.set(false);
      }
    });
  }
}
