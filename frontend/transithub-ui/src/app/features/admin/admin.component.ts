import { Component, signal } from '@angular/core';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-admin', standalone: true, imports: [NgFor],
  template: `
    <div class="container admin-page">
      <h1>Admin Dashboard</h1>
      <div class="stats-grid">
        <div class="stat-card"><h3>Total Revenue</h3><p class="value">₹12,45,890</p></div>
        <div class="stat-card"><h3>Bookings Today</h3><p class="value">234</p></div>
        <div class="stat-card"><h3>Active Users</h3><p class="value">45,678</p></div>
        <div class="stat-card"><h3>Cancellation Rate</h3><p class="value">3.2%</p></div>
      </div>
      <div class="admin-section">
        <h2>Recent Bookings</h2>
        <table class="admin-table">
          <thead><tr><th>Ref</th><th>Mode</th><th>User</th><th>Amount</th><th>Status</th><th>Date</th></tr></thead>
          <tbody>
            <tr *ngFor="let b of recentBookings()">
              <td>{{b.ref}}</td><td>{{b.mode}}</td><td>{{b.user}}</td><td>₹{{b.amount}}</td>
              <td><span class="status-badge" [class.confirmed]="b.status==='CONFIRMED'">{{b.status}}</span></td>
              <td>{{b.date}}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
  styles: [`
    .admin-page { padding: 40px 0; }
    .admin-page h1 { font-size: 32px; margin-bottom: 24px; }
    .stats-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 40px; }
    .stat-card { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .stat-card h3 { color: var(--text-secondary); font-size: 14px; margin-bottom: 8px; }
    .stat-card .value { font-size: 28px; font-weight: 700; color: var(--primary); }
    .admin-section h2 { margin-bottom: 16px; }
    .admin-table { width: 100%; background: white; border-radius: 12px; overflow: hidden; border-collapse: collapse; }
    .admin-table th, .admin-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #E2E8F0; }
    .admin-table th { background: #F8FAFC; font-weight: 600; color: var(--text-secondary); }
    .status-badge { padding: 4px 12px; border-radius: 20px; font-size: 13px; background: #FEE2E2; }
    .confirmed { background: #D1FAE5; }
  `]
})
export class AdminComponent {
  recentBookings = signal([
    { ref: 'BK-A1B2C3', mode: 'FLIGHT', user: 'John Doe', amount: 5500, status: 'CONFIRMED', date: '2024-12-15' },
    { ref: 'BK-D4E5F6', mode: 'BUS', user: 'Jane Smith', amount: 800, status: 'CONFIRMED', date: '2024-12-14' },
    { ref: 'BK-G7H8I9', mode: 'TRAIN', user: 'Bob Wilson', amount: 1800, status: 'CANCELLED', date: '2024-12-13' },
  ]);
}
