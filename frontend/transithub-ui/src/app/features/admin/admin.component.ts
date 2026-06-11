import { Component, signal, OnInit } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-admin', standalone: true, imports: [NgFor, NgIf],
  template: `
    <div class="container admin-page">
      <h1>Admin Dashboard</h1>
      <div *ngIf="loading()" class="loading">Loading dashboard...</div>
      <div *ngIf="error()" class="error-msg">{{error()}}</div>
      <div class="stats-grid">
        <div class="stat-card"><h3>Active Users</h3><p class="value">{{users().length}}</p></div>
        <div class="stat-card"><h3>Fraud Alerts</h3><p class="value">{{fraudAlerts().length}}</p></div>
        <div class="stat-card"><h3>Unresolved Alerts</h3><p class="value">{{unresolvedCount()}}</p></div>
      </div>
      <div class="admin-section">
        <h2>Recent Users</h2>
        <table class="admin-table">
          <thead><tr><th>Email</th><th>Name</th><th>Role</th><th>Status</th></tr></thead>
          <tbody>
            <tr *ngFor="let u of users()">
              <td>{{u.email}}</td><td>{{u.fullName}}</td><td>{{u.role}}</td>
              <td><span class="status-badge" [class.active]="u.isActive">{{u.isActive ? 'Active' : 'Banned'}}</span></td>
            </tr>
          </tbody>
        </table>
      </div>
      <div class="admin-section" style="margin-top:24px">
        <h2>Fraud Alerts</h2>
        <div *ngFor="let a of fraudAlerts()" class="alert-card">
          <strong>{{a.alertType}}</strong> — {{a.description}}
          <span class="severity" [class.high]="a.severity==='HIGH'">{{a.severity}}</span>
        </div>
        <div *ngIf="fraudAlerts().length === 0" class="no-data">No fraud alerts.</div>
      </div>
    </div>
  `,
  styles: [`
    .admin-page { padding: 40px 0; }
    .admin-page h1 { font-size: 32px; margin-bottom: 24px; }
    .stats-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; margin-bottom: 40px; }
    .stat-card { background: white; padding: 24px; border-radius: 12px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .stat-card h3 { color: var(--text-secondary); font-size: 14px; margin-bottom: 8px; }
    .stat-card .value { font-size: 28px; font-weight: 700; color: var(--primary); }
    .admin-section h2 { margin-bottom: 16px; }
    .admin-table { width: 100%; background: white; border-radius: 12px; overflow: hidden; border-collapse: collapse; }
    .admin-table th, .admin-table td { padding: 12px 16px; text-align: left; border-bottom: 1px solid #E2E8F0; }
    .admin-table th { background: #F8FAFC; font-weight: 600; color: var(--text-secondary); }
    .status-badge { padding: 4px 12px; border-radius: 20px; font-size: 13px; background: #FEE2E2; }
    .status-badge.active { background: #D1FAE5; }
    .alert-card { background: white; padding: 16px; border-radius: 8px; border: 1px solid #E2E8F0; margin-bottom: 8px; display: flex; justify-content: space-between; align-items: center; }
    .severity { padding: 4px 12px; border-radius: 20px; font-size: 12px; background: #FEF3C7; }
    .severity.high { background: #FEE2E2; color: #DC2626; }
    .loading, .no-data { text-align: center; padding: 40px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; text-align: center; margin-bottom: 16px; }
  `]
})
export class AdminComponent implements OnInit {
  users = signal<any[]>([]);
  fraudAlerts = signal<any[]>([]);
  loading = signal(true);
  error = signal('');
  unresolvedCount = signal(0);
  private graphql = inject(GraphqlService);

  ngOnInit() {
    this.graphql.allUsers(0, 50).subscribe({
      next: (data: any) => { this.users.set(data?.allUsers || []); this.loading.set(false); },
      error: (err: any) => { this.error.set(err?.message || 'Failed to load'); this.loading.set(false); }
    });
    this.graphql.fraudAlerts().subscribe({
      next: (data: any) => {
        const alerts = data?.fraudAlerts || [];
        this.fraudAlerts.set(alerts);
        this.unresolvedCount.set(alerts.filter((a: any) => !a.isResolved).length);
      },
      error: () => {}
    });
  }
}
