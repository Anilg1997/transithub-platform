import { Component, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgFor, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-flight-booking', standalone: true, imports: [NgFor, FormsModule, CurrencyPipe],
  template: `
    <div class="container booking-page">
      <div class="booking-card">
        <h2>Flight Booking</h2>
        <div class="flight-summary">
          <div class="route"><span class="city">DEL</span> → <span class="city">BOM</span></div>
          <div class="details">AI101 · Air India · Non-stop · 2h 15m</div>
          <div class="fare">₹{{baseFare()}}</div>
        </div>
        <div class="passengers-section">
          <h3>Passengers</h3>
          <div *ngFor="let p of passengers(); let i = index" class="passenger-form">
            <h4>Passenger {{i + 1}}</h4>
            <div class="form-row">
              <input [(ngModel)]="p.name" placeholder="Full Name" class="input">
              <input [(ngModel)]="p.age" type="number" placeholder="Age" class="input small">
              <select [(ngModel)]="p.gender" class="input small">
                <option value="">Gender</option>
                <option value="M">Male</option>
                <option value="F">Female</option>
                <option value="O">Other</option>
              </select>
            </div>
            <div class="form-row">
              <select [(ngModel)]="p.idType" class="input">
                <option value="AADHAAR">Aadhaar</option>
                <option value="PAN">PAN Card</option>
                <option value="PASSPORT">Passport</option>
              </select>
              <input [(ngModel)]="p.idNumber" placeholder="ID Number" class="input">
            </div>
          </div>
          <button (click)="addPassenger()" class="add-btn">+ Add Passenger</button>
        </div>
        <div class="seat-selection">
          <h3>Select Seats</h3>
          <div class="seat-grid">
            <button *ngFor="let seat of seatMap" class="seat" [class.selected]="selectedSeats().includes(seat)"
                    [class.unavailable]="seat.startsWith('X')" (click)="toggleSeat(seat)"
                    [disabled]="seat.startsWith('X')">{{seat}}</button>
          </div>
        </div>
        <div class="summary">
          <div class="total">Total: ₹{{totalFare()}}</div>
          <button (click)="book()" class="book-btn">Confirm Booking</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .booking-page { padding: 32px 0; max-width: 700px; margin: 0 auto; }
    .booking-card { background: white; border-radius: 16px; padding: 32px; box-shadow: 0 2px 8px rgba(0,0,0,0.08); }
    .flight-summary { background: #EFF6FF; border-radius: 12px; padding: 20px; margin-bottom: 24px; display: flex; justify-content: space-between; align-items: center; }
    .route { font-size: 20px; font-weight: 700; }
    .city { color: var(--primary); }
    .details { color: var(--text-secondary); font-size: 14px; }
    .fare { font-size: 22px; font-weight: 700; color: var(--primary); }
    .passengers-section { margin-bottom: 24px; }
    .passenger-form { border: 1px solid #E2E8F0; border-radius: 10px; padding: 16px; margin-bottom: 16px; }
    .passenger-form h4 { margin-bottom: 12px; color: var(--primary); }
    .form-row { display: flex; gap: 12px; margin-bottom: 12px; }
    .input { flex: 1; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px; font-size: 14px; }
    .input.small { flex: 0 0 100px; }
    select.input { background: white; }
    .add-btn { background: none; border: 2px dashed #CBD5E1; padding: 12px; width: 100%; border-radius: 10px; cursor: pointer; color: var(--text-secondary); font-weight: 500; }
    .add-btn:hover { border-color: var(--primary); color: var(--primary); }
    .seat-selection { margin-bottom: 24px; }
    .seat-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 8px; margin-top: 12px; }
    .seat { padding: 10px; border: 1px solid #E2E8F0; border-radius: 6px; cursor: pointer; font-size: 12px; text-align: center; background: white; }
    .seat:hover:not(.unavailable) { border-color: var(--primary); background: #EFF6FF; }
    .seat.selected { background: var(--primary); color: white; border-color: var(--primary); }
    .seat.unavailable { background: #F1F5F9; color: #94A3B8; cursor: not-allowed; }
    .summary { display: flex; justify-content: space-between; align-items: center; padding-top: 20px; border-top: 1px solid #E2E8F0; }
    .total { font-size: 24px; font-weight: 700; color: var(--primary); }
    .book-btn { background: var(--accent); color: white; padding: 16px 48px; border: none; border-radius: 12px; font-size: 16px; font-weight: 700; cursor: pointer; }
    .book-btn:hover { background: #059669; }
  `]
})
export class FlightBookingComponent {
  baseFare = signal(5500);
  passengers = signal([{ name: '', age: 0, gender: '', idType: 'AADHAAR', idNumber: '' }]);
  selectedSeats = signal<string[]>([]);
  seatMap = ['1A','1B','1C','1D','1E','1F','2A','2B','X','2D','2E','2F','3A','3B','3C','3D','3E','3F'];
  totalFare = signal(5500);

  constructor(private router: Router) {}
  addPassenger() { this.passengers.update(p => [...p, { name: '', age: 0, gender: '', idType: 'AADHAAR', idNumber: '' }]); }
  toggleSeat(seat: string) {
    this.selectedSeats.update(s => s.includes(seat) ? s.filter(x => x !== seat) : [...s, seat]);
  }
  book() { this.router.navigate(['/payment', 'FL-' + Date.now()]); }
}
