import { Component, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgFor, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-bus-booking', standalone: true, imports: [NgFor, FormsModule, CurrencyPipe],
  template: `
    <div class="container booking-page">
      <div class="booking-card">
        <h2>Bus Booking</h2>
        <div class="bus-summary">
          <div class="route"><span class="city">Delhi</span> → <span class="city">Jaipur</span></div>
          <div class="details">RSRTC Volvo AC · Departs 22:00 · Arrives 05:30 · 7h 30m</div>
          <div class="fare">₹{{baseFare()}}</div>
        </div>
        <div class="boarding-section">
          <div class="form-group">
            <label>Boarding Point</label>
            <select [(ngModel)]="boardingPoint" class="input">
              <option value="Kashmere Gate">Kashmere Gate ISBT</option>
              <option value="Majnu Ka Tilla">Majnu Ka Tilla</option>
            </select>
          </div>
          <div class="form-group">
            <label>Dropping Point</label>
            <select [(ngModel)]="droppingPoint" class="input">
              <option value="Jaipur Station">Jaipur Bus Stand</option>
              <option value="Vaishali Nagar">Vaishali Nagar</option>
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
          <div class="legend"><span class="legend-item available">Available</span><span class="legend-item selected">Selected</span><span class="legend-item booked">Booked</span></div>
          <div class="seat-grid">
            <button *ngFor="let seat of seatMap" class="seat" [class.selected]="selectedSeats().includes(seat)"
                    [class.booked]="seat.startsWith('X')" (click)="toggleSeat(seat)"
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
    .legend { display: flex; gap: 16px; margin-bottom: 12px; font-size: 13px; }
    .legend-item::before { content: ''; display: inline-block; width: 12px; height: 12px; border-radius: 3px; margin-right: 4px; vertical-align: middle; }
    .available::before { background: white; border: 1px solid #E2E8F0; }
    .selected::before { background: #059669; }
    .booked::before { background: #94A3B8; }
    .seat-grid { display: grid; grid-template-columns: repeat(6, 1fr); gap: 8px; }
    .seat { padding: 12px; border: 1px solid #E2E8F0; border-radius: 6px; cursor: pointer; font-size: 13px; text-align: center; background: white; }
    .seat:hover:not(.booked) { border-color: #059669; }
    .seat.selected { background: #059669; color: white; border-color: #059669; }
    .seat.booked { background: #F1F5F9; color: #94A3B8; cursor: not-allowed; }
    .summary { display: flex; justify-content: space-between; align-items: center; padding-top: 20px; border-top: 1px solid #E2E8F0; margin-top: 20px; }
    .total { font-size: 24px; font-weight: 700; color: #059669; }
    .book-btn { background: #059669; color: white; padding: 16px 48px; border: none; border-radius: 12px; font-size: 16px; font-weight: 700; cursor: pointer; }
  `]
})
export class BusBookingComponent {
  baseFare = signal(1500);
  boardingPoint = 'Kashmere Gate';
  droppingPoint = 'Jaipur Station';
  passengers = signal([{ name: '', age: 0, gender: '' }]);
  selectedSeats = signal<string[]>([]);
  seatMap = ['L1','L2','L3','L4','L5','L6','U1','U2','X','U4','U5','U6'];
  totalFare = signal(1500);

  constructor(private router: Router) {}
  addPassenger() { this.passengers.update(p => [...p, { name: '', age: 0, gender: '' }]); }
  toggleSeat(seat: string) {
    this.selectedSeats.update(s => s.includes(seat) ? s.filter(x => x !== seat) : [...s, seat]);
  }
  book() { this.router.navigate(['/payment', 'BS-' + Date.now()]); }
}
