import { Component, signal } from '@angular/core';
import { NgFor, DatePipe } from '@angular/common';

@Component({
  selector: 'app-notifications', standalone: true, imports: [NgFor, DatePipe],
  template: `
    <div class="container notifications-page">
      <div class="notif-header">
        <h1>Notifications</h1>
        <button class="mark-read" (click)="markAllRead()">Mark All Read</button>
      </div>
      <div class="notif-list">
        <div *ngFor="let n of notifications()" class="notif-card" [class.unread]="!n.isRead" (click)="markRead(n)">
          <div class="notif-icon">
            {{n.type === 'BOOKING' ? '🎫' : n.type === 'PAYMENT' ? '💳' : n.type === 'REMINDER' ? '⏰' : '🔔'}}
          </div>
          <div class="notif-content">
            <strong>{{n.title}}</strong>
            <p>{{n.message}}</p>
            <small>{{n.createdAt}}</small>
          </div>
          <div *ngIf="!n.isRead" class="unread-dot"></div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notifications-page { padding: 40px 0; max-width: 700px; margin: 0 auto; }
    .notif-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
    .notif-header h1 { font-size: 28px; }
    .mark-read { color: var(--primary); background: none; border: none; cursor: pointer; font-weight: 600; }
    .notif-list { displa
