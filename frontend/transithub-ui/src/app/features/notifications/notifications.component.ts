import { Component, signal, OnInit } from '@angular/core';
import { NgFor, DatePipe, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-notifications', standalone: true, imports: [NgFor, DatePipe, NgIf],
  template: `
    <div class="container notifications-page">
      <div class="notif-header">
        <h1>Notifications</h1>
        <button class="mark-read" (click)="markAllRead()">Mark All Read</button>
      </div>
      <div *ngIf="loading()" class="loading">Loading notifications...</div>
      <div *ngIf="error()" class="error-msg">{{error()}}</div>
      <div class="notif-list">
        <div *ngFor="let n of notifications()" class="notif-card" [class.unread]="!n.isRead" (click)="markRead(n)">
          <div class="notif-icon">
            {{n.type === 'BOOKING' ? '🎫' : n.type === 'PAYMENT' ? '💳' : n.type === 'REMINDER' ? '⏰' : '🔔'}}
          </div>
          <div class="notif-content">
            <strong>{{n.title}}</strong>
            <p>{{n.message}}</p>
            <small>{{n.createdAt | date:'medium'}}</small>
          </div>
          <div *ngIf="!n.isRead" class="unread-dot"></div>
        </div>
      </div>
      <div *ngIf="!loading() && notifications().length === 0" class="no-notifs">No notifications yet.</div>
    </div>
  `,
  styles: [`
    .notifications-page { padding: 40px 0; max-width: 700px; margin: 0 auto; }
    .notif-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .notif-header h1 { font-size: 28px; }
    .mark-read { color: var(--primary); background: none; border: none; cursor: pointer; font-weight: 600; }
    .notif-list { display: flex; flex-direction: column; gap: 12px; }
    .notif-card { display: flex; align-items: flex-start; gap: 16px; padding: 16px; background: white; border-radius: 12px; border: 1px solid #E2E8F0; cursor: pointer; transition: all 0.2s; position: relative; }
    .notif-card:hover { border-color: var(--primary); }
    .notif-card.unread { background: #EFF6FF; border-color: #BFDBFE; }
    .notif-icon { font-size: 24px; min-width: 40px; text-align: center; }
    .notif-content { flex: 1; }
    .notif-content strong { display: block; margin-bottom: 4px; }
    .notif-content p { color: var(--text-secondary); font-size: 14px; margin: 0 0 4px; }
    .notif-content small { color: #94A3B8; }
    .unread-dot { width: 10px; height: 10px; background: var(--primary); border-radius: 50%; margin-top: 6px; }
    .loading, .no-notifs { text-align: center; padding: 40px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; text-align: center; margin-bottom: 16px; }
  `]
})
export class NotificationsComponent implements OnInit {
  notifications = signal<any[]>([]);
  loading = signal(true);
  error = signal('');
  private graphql = inject(GraphqlService);

  ngOnInit() {
    this.graphql.myNotifications(0, 50).subscribe({
      next: (data: any) => {
        this.notifications.set(data?.myNotifications || []);
        this.loading.set(false);
      },
      error: (err: any) => {
        this.error.set(err?.message || 'Failed to load notifications');
        this.loading.set(false);
      }
    });
  }

  markRead(n: any) {
    if (!n.isRead) {
      this.graphql.markRead(n.id).subscribe({
        next: () => {
          this.notifications.update(list => list.map(x => x.id === n.id ? { ...x, isRead: true } : x));
        },
        error: () => {}
      });
    }
  }

  markAllRead() {
    this.graphql.markAllRead().subscribe({
      next: () => {
        this.notifications.update(list => list.map(x => ({ ...x, isRead: true })));
      },
      error: () => {}
    });
  }
}
