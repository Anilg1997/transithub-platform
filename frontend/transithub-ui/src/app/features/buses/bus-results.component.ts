import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { NgFor, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-bus-results', standalone: true, imports: [NgFor, CurrencyPipe],
  template: `
    <div class="container results-page">
      <div class="results">
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
  `]
})
export class BusResultsComponent {
  buses = signal([
    { id: '1', operator: 'VRL Travels', busType: 'AC Sleeper', origin: 'Mumbai', destination: 'Pune', departureTime: '22:00', arrivalTime: '05:00', duration: 420, fare: 800, availableSeats: 12 },
    { id: '2', operator: 'SRS Travels', busType: 'Volvo', origin: 'Mumbai', destination: 'Pune', departureTime: '06:30', arrivalTime: '11:00', duration: 270, fare: 1200, availableSeats: 28 },
  ]);
  constructor(private router: Router) {}
  selectBus(bus: any) { this.router.navigate(['/booking/bus', bus.id]); }
}
