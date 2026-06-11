import { Component, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgFor, CurrencyPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-train-booking', standalone: true, imports: [NgFor, FormsModule, CurrencyPipe, NgIf],
  template: `
    <div class="container booking-page">
      <div *ngIf="loading()" class="loading">Loading coach details...</div>
      <div *ngIf="error()" class="error-msg">{{error()}}</div>
      <div *ngIf="!loading() && !error()" class="booking-card">
        <h2>Train Booking</h2>
        <div class="train-summary">
          <div class="route"><span class="city">{{trainId}}</span> → <span class="city">Destination</span></div>
          <div class="details">Coach: {{coachType || '3AC'}}</div>
          <div class="fare">₹{{baseFare()}}</div>
        </div>
        <div class="coach-section">
          <div class="form-group">
            <label>Quota</label>
            <select [(ngModel)]="quota" class="input">
              <option value="GENERAL">General</option>
              <option value="TATKAL">Tatkal</option>
              <option value="LADIES">Ladies</option>
              <option value="SENIOR_CITIZEN">Senior Citizen</option>
            </select>
          </div>
          <div class="form-group">
            <label>Berth Preference</label>
            <select [(ngModel)]="berthPref" class="input">
              <option value="LOWER">Lower</option>
              <option value="MIDDLE">Middle</option>
              <option value="UPPER">Upper</option>
              <option value="SIDE_LOWER">Side Lower</option>
              <option value="SIDE_UPPER">Side Upper</option>
            </select>
          </div>
        </div>
        <div class="passengers-section">
          <h3>Passengers</h3>
          <div *ngFor="let p of passengers(); let i = index" class="passenger-form">
            <div class="form-row">
              <input [(ngModel)]="p.name" placeholder="Full Name" class="input">
              <input [(ngModel)]="p.age" type="number" placeholder="Age" class="input small">
              <select [(ngModel)]="p.gender" class="input small">
                <option value="">Gender</option>
                <option value="M">Male</option>
                <option value="F">Female</option>
              </select>
            </div>
          </div>
          <button (click)="addPassenger()" class="add-btn">+ Add Passenger</button>
        </div>
        <div class="summary">
          <div class="total">Total: ₹{{totalFare()}}</div>
          <button (click)="book()" class="book-btn" [disabled]="booking()">{{booking() ? 'Booking...' : 'Confirm Booking'}}</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .booking-page { padding: 32px 0; max-width: 700px; margin: 0 auto; }
    .booking-card { background: white; border-radius: 16px; padding: 32px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .train-summary { background: #FFF7ED; border-radius: 12px; padding: 20px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; }
    .route { font-size: 20px; font-weight: 700; }
    .city { color: #EA580C; }
    .details { color: var(--text-secondary); font-size: 14px; }
    .fare { font-size: 22px; font-weight: 700; color: #EA580C; }
    .coach-section { display: flex; gap: 16px; margin-bottom: 24px; }
    .form-group { flex: 1; }
    .form-group label { display: block; font-weight: 500; margin-bottom: 6px; color: var(--text-secondary); }
    .input { width: 100%; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px; font-size: 14px; box-sizing: border-box; }
    .input.small { flex: 0 0 100px; width: 100px; }
    .passenger-form { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
    .form-row { display: flex; gap: 12px; }
    .add-btn { background: none; border: 2px dashed #CBD5E1; padding: 12px; width: 100%; border-radius: 10px; cursor: pointer; color: var(--text-secondary); }
    .summary { display: flex; justify-content: space-between; align-items: center; padding-top: 20px; border-top: 1px solid #E2E8F0; margin-top: 20px; }
    .total { font-size: 24px; font-weight: 700; color: #EA580C; }
    .book-btn { background: #EA580C; color: white; padding: 16px 48px; border: none; border-radius: 12px; font-size: 16px; font-weight: 700; cursor: pointer; }
    .book-btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .loading, .error-msg { text-align: center; padding: 40px; }
    .error-msg { background: #FEF2F2; color: #DC2626; border-radius: 8px; }
  `]
})
export class TrainBookingComponent implements OnInit {
  baseFare = signal(2400);
  passengers = signal([{ name: '', age: 0, gender: '' }]);
  totalFare = signal(2400);
  quota = 'GENERAL';
  berthPref = 'LOWER';
  trainId = '';
  coachType = '3AC';
  loading = signal(true);
  error = signal('');
  booking = signal(false);
  private graphql = inject(GraphqlService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private date = '';

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.trainId = params['id'];
      this.date = new Date().toISOString().split('T')[0];
    });
    this.route.queryParams.subscribe(params => {
      if (params['coach']) this.coachType = params['coach'];
      this.graphql.coachMap(this.trainId, this.date, this.coachType).subscribe({
        next: (data: any) => {
          const berths = data?.coachMap;
          if (berths?.length) this.baseFare.set(berths[0].price || 2400);
          this.loading.set(false);
        },
        error: () => { this.loading.set(false); }
      });
    });
  }

  addPassenger() { this.passengers.update(p => [...p, { name: '', age: 0, gender: '' }]); }

  book() {
    this.booking.set(true);
    this.graphql.bookTrain({
      trainId: this.trainId, date: this.date,
      coachType: this.coachType, quota: this.quota,
      passengers: this.passengers(),
      berthPreferences: [this.berthPref]
    }).subscribe({
      next: (data: any) => {
        const ref = data?.bookTrain?.bookingRef || 'TR-' + Date.now();
        this.router.navigate(['/payment', ref]);
      },
      error: () => { this.booking.set(false); }
    });
  }
}
