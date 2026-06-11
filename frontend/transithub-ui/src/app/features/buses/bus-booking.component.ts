import { Component, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgFor, CurrencyPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-bus-booking', standalone: true, imports: [NgFor, FormsModule, CurrencyPipe, NgIf],
  template: `
    <div class="container booking-page">
      <div *ngIf="loading()" class="loading">Loading bus details...</div>
      <div *ngIf="error()" class="error-msg">{{error()}}</div>
      <div *ngIf="!loading() && !error()" class="booking-card">
        <h2>Bus Booking</h2>
        <div class="bus-summary">
          <div class="route"><span class="city">{{busData()?.origin || 'Delhi'}}</span> → <span class="city">{{busData()?.destination || 'Jaipur'}}</span></div>
          <div class="details">{{busData()?.operator || ''}} · {{busData()?.busType || ''}} · Departs {{busData()?.departureTime || ''}}</div>
          <div class="fare">₹{{busData()?.fare || baseFare()}}</div>
        </div>
        <div class="boarding-section">
          <div class="form-group">
            <label>Boarding Point</label>
            <select [(ngModel)]="boardingPoint" class="input">
              <option *ngFor="let p of busData()?.boardingPoints || ['Kashmere Gate ISBT']" [value]="p">{{p}}</option>
            </select>
          </div>
          <div class="form-group">
            <label>Dropping Point</label>
            <select [(ngModel)]="droppingPoint" class="input">
              <option *ngFor="let p of busData()?.droppingPoints || ['Jaipur Bus Stand']" [value]="p">{{p}}</option>
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
        <div class="seat-selection">
          <h3>Select Seats</h3>
          <div class="seat-grid">
            <button *ngFor="let seat of seatMap" class="seat" [class.selected]="selectedSeats().includes(seat)"
                    [class.booked]="seat.startsWith('X')" (click)="toggleSeat(seat)"
                    [disabled]="seat.startsWith('X')">{{seat}}</button>
          </div>
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
    .bus-summary { background: #F0FDF4; border-radius: 12px; padding: 20px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; }
    .route { font-size: 20px; font-weight: 700; }
    .city { color: #059669; }
    .details { color: var(--text-secondary); font-size: 14px; }
    .fare { font-size: 22px; font-weight: 700; color: #059669; }
    .boarding-section { display: flex; gap: 16px; margin-bottom: 24px; }
    .form-group { flex: 1; }
    .form-group label { display: block; font-weight: 500; margin-bottom: 6px; color: var(--text-secondary); }
    .input { width: 100%; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px; font-size: 14px; box-sizing: border-box; }
    .input.small { flex: 0 0 100px; width: 100px; }
    .passenger-form { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; margin-bottom: 12px; }
    .form-row { display: flex; gap: 12px; }
    .add-btn { background: none; border: 2px dashed #CBD5E1; padding: 12px; width: 100%; border-radius: 10px; cursor: pointer; color: var(--text-secondary); }
    .seat-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 8px; margin-top: 12px; }
    .seat { padding: 12px; border: 1px solid #E2E8F0; border-radius: 6px; cursor: pointer; font-size: 13px; text-align: center; background: white; }
    .seat:hover:not(.booked) { border-color: #059669; }
    .seat.selected { background: #059669; color: white; border-color: #059669; }
    .seat.booked { background: #F1F5F9; color: #94A3B8; cursor: not-allowed; }
    .summary { display: flex; justify-content: space-between; align-items: center; padding-top: 20px; border-top: 1px solid #E2E8F0; margin-top: 20px; }
    .total { font-size: 24px; font-weight: 700; color: #059669; }
    .book-btn { background: #059669; color: white; padding: 16px 48px; border: none; border-radius: 12px; font-size: 16px; font-weight: 700; cursor: pointer; }
    .book-btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .loading, .error-msg { text-align: center; padding: 40px; }
    .error-msg { background: #FEF2F2; color: #DC2626; border-radius: 8px; }
  `]
})
export class BusBookingComponent implements OnInit {
  baseFare = signal(1500);
  boardingPoint = 'Kashmere Gate ISBT';
  droppingPoint = 'Jaipur Bus Stand';
  passengers = signal([{ name: '', age: 0, gender: '' }]);
  selectedSeats = signal<string[]>([]);
  seatMap = ['L1','L2','L3','L4','L5','L6','U1','U2','X','U4','U5','U6'];
  totalFare = signal(1500);
  loading = signal(true);
  error = signal('');
  booking = signal(false);
  busData = signal<any>(null);
  private graphql = inject(GraphqlService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private busId = '';
  private date = '';

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.busId = params['id'];
      this.date = new Date().toISOString().split('T')[0];
      this.graphql.busSeatMap(this.busId, this.date).subscribe({
        next: (data: any) => {
          const seats = data?.busSeatMap;
          if (seats) this.seatMap = seats.map((s: any) => s.isAvailable ? s.seatNumber : 'X');
          this.loading.set(false);
        },
        error: () => { this.loading.set(false); }
      });
    });
  }

  addPassenger() { this.passengers.update(p => [...p, { name: '', age: 0, gender: '' }]); }
  toggleSeat(seat: string) {
    this.selectedSeats.update(s => s.includes(seat) ? s.filter(x => x !== seat) : [...s, seat]);
  }

  book() {
    this.booking.set(true);
    this.graphql.bookBus({
      busId: this.busId, date: this.date,
      seats: this.selectedSeats(),
      boardingPoint: this.boardingPoint,
      droppingPoint: this.droppingPoint,
      passengers: this.passengers()
    }).subscribe({
      next: (data: any) => {
        const ref = data?.bookBus?.bookingRef || 'BS-' + Date.now();
        this.router.navigate(['/payment', ref]);
      },
      error: () => { this.booking.set(false); }
    });
  }
}
