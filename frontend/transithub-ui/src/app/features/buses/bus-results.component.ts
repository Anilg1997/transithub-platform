import { Component, signal, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgFor, CurrencyPipe, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-bus-results', standalone: true, imports: [NgFor, CurrencyPipe, NgIf],
  template: `
    <div class="container results-page">
      <div class="results">
        <div *ngIf="loading()" class="loading">Loading buses...</div>
        <div *ngIf="error()" class="error-msg">{{error()}}</div>
        <div *ngFor="let bus of buses()" class="bus-card" (click)="selectBus(bus)">
          <div class="bus-header">
            <span class="operator">{{bus.operator}}</span>
            <span class="bus-type">{{bus.busType}}</span>
          </div>
          <div class="bus-details">
            <div><span class="label">Departure</span><span class="value">{{bus.departureTime}}</span></div>
            <div><span class="label">Arrival</span><span class="value">{{bus.arrivalTime}}</span></div>
            <div><span class="label">Duration</span><span class="value">{{bus.duration}} min</span></div>
            <div class="price-block">
              <span class="price">₹{{bus.fare}}</span>
              <span class="seats">{{bus.availableSeats}} seats</span>
              <button class="book-btn">View Seats</button>
            </div>
          </div>
        </div>
        <div *ngIf="!loading() && buses().length === 0" class="no-results">No buses found for this route.</div>
      </div>
    </div>
  `,
  styles: [`
    .results-page { padding: 32px 0; }
    .results { display: flex; flex-direction: column; gap: 16px; }
    .bus-card { background: white; border-radius: 12px; padding: 20px; cursor: pointer; border: 1px solid #E2E8F0; transition: all 0.2s; }
    .bus-card:hover { border-color: var(--primary); box-shadow: 0 4px 12px rgba(37,99,235,0.15); }
    .bus-header { display: flex; justify-content: space-between; margin-bottom: 16px; }
    .operator { font-weight: 600; font-size: 18px; }
    .bus-type { background: #EFF6FF; color: var(--primary); padding: 4px 12px; border-radius: 20px; font-size: 13px; }
    .bus-details { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; align-items: center; }
    .label { display: block; font-size: 12px; color: var(--text-secondary); }
    .value { font-size: 16px; font-weight: 600; }
    .price-block { text-align: right; }
    .price { display: block; font-size: 24px; font-weight: 700; color: var(--primary); }
    .seats { font-size: 13px; color: var(--text-secondary); }
    .book-btn { margin-top: 8px; background: var(--primary); color: white; border: none; padding: 8px 20px; border-radius: 8px; cursor: pointer; font-weight: 600; }
    .loading, .no-results { text-align: center; padding: 40px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; text-align: center; }
  `]
})
export class BusResultsComponent implements OnInit {
  buses = signal<any[]>([]);
  loading = signal(true);
  error = signal('');
  private graphql = inject(GraphqlService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const origin = params['from'] || 'Mumbai';
      const dest = params['to'] || 'Pune';
      const date = params['date'] || new Date().toISOString().split('T')[0];
      this.loading.set(true);
      this.graphql.searchBuses(origin, dest, date).subscribe({
        next: (data: any) => {
          this.buses.set(data?.searchBuses || []);
          this.loading.set(false);
        },
        error: (err: any) => {
          this.error.set(err?.message || 'Failed to load buses');
          this.loading.set(false);
        }
      });
    });
  }

  selectBus(bus: any) { this.router.navigate(['/booking/bus', bus.id]); }
}
