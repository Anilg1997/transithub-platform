import { Component, signal, OnInit } from '@angular/core';
import { NgFor, CurrencyPipe, DecimalPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-profile', standalone: true, imports: [NgFor, FormsModule, CurrencyPipe, DecimalPipe, NgIf],
  template: `
    <div class="container profile-page">
      <div *ngIf="loading()" class="loading">Loading profile...</div>
      <div *ngIf="error()" class="error-msg">{{error()}}</div>
      <div *ngIf="!loading()">
        <div class="profile-header">
          <div class="avatar">{{user().fullName?.charAt(0) || 'U'}}</div>
          <div class="user-info">
            <h2>{{user().fullName}}</h2>
            <p>{{user().email}}</p>
            <span class="tier-badge">{{user().loyaltyTier || 'Silver'}}</span>
          </div>
        </div>
        <div class="stats-grid">
          <div class="stat-card">
            <span class="stat-value">{{user().loyaltyPoints || 0}}</span>
            <span class="stat-label">Loyalty Points</span>
          </div>
          <div class="stat-card">
            <span class="stat-value">₹{{walletBalance() | number}}</span>
            <span class="stat-label">Wallet Balance</span>
          </div>
        </div>
        <div class="section">
          <h3>My Travellers</h3>
          <div *ngFor="let t of travellers()" class="traveller-card">
            <div class="traveller-info">
              <span class="name">{{t.name}}</span>
              <span class="details">Age: {{t.age}} · {{t.gender}} · {{t.idType}}</span>
            </div>
            <button class="remove-btn" (click)="removeTraveller(t)">✕</button>
          </div>
          <div *ngIf="showAddForm()" class="add-form">
            <input [(ngModel)]="newTraveller.name" placeholder="Name" class="input">
            <input [(ngModel)]="newTraveller.age" type="number" placeholder="Age" class="input small">
            <select [(ngModel)]="newTraveller.gender" class="input small">
              <option value="M">Male</option><option value="F">Female</option>
            </select>
            <button (click)="saveTraveller()" class="save-btn">Save</button>
          </div>
          <button (click)="showAddForm.set(true)" *ngIf="!showAddForm()" class="add-btn">+ Add Traveller</button>
        </div>
        <div class="section">
          <h3>Add Money to Wallet</h3>
          <div class="wallet-topup">
            <input [(ngModel)]="topupAmount" type="number" placeholder="Amount" class="input">
            <button (click)="topup()" class="topup-btn">Add ₹{{topupAmount || 0}}</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .profile-page { padding: 32px 0; max-width: 700px; margin: 0 auto; }
    .profile-header { display: flex; align-items: center; gap: 20px; margin-bottom: 32px; }
    .avatar { width: 80px; height: 80px; background: var(--primary); color: white; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 36px; font-weight: 700; }
    .user-info h2 { margin: 0; }
    .user-info p { color: var(--text-secondary); margin: 4px 0; }
    .tier-badge { background: #FEF3C7; color: #92400E; padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
    .stats-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; margin-bottom: 32px; }
    .stat-card { background: white; padding: 24px; border-radius: 12px; text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
    .stat-value { display: block; font-size: 24px; font-weight: 700; color: var(--primary); }
    .stat-label { color: var(--text-secondary); font-size: 13px; }
    .section { background: white; border-radius: 12px; padding: 24px; margin-bottom: 20px; box-shadow: 0 1px 3px rgba(0,0,0,0.08); }
    .section h3 { margin-bottom: 16px; }
    .traveller-card { display: flex; justify-content: space-between; align-items: center; padding: 12px; border: 1px solid #E2E8F0; border-radius: 8px; margin-bottom: 8px; }
    .traveller-info .name { font-weight: 600; display: block; }
    .traveller-info .details { font-size: 13px; color: var(--text-secondary); }
    .remove-btn { background: none; border: none; color: #EF4444; cursor: pointer; font-size: 18px; }
    .add-form { display: flex; gap: 8px; margin-top: 12px; }
    .input { padding: 10px 12px; border: 1px solid #E2E8F0; border-radius: 8px; font-size: 14px; flex: 1; }
    .input.small { flex: 0 0 80px; }
    .save-btn { background: var(--primary); color: white; border: none; padding: 10px 20px; border-radius: 8px; cursor: pointer; font-weight: 600; }
    .add-btn { background: none; border: 2px dashed #CBD5E1; padding: 12px; width: 100%; border-radius: 10px; cursor: pointer; color: var(--text-secondary); }
    .wallet-topup { display: flex; gap: 12px; }
    .topup-btn { background: var(--accent); color: white; border: none; padding: 10px 24px; border-radius: 8px; cursor: pointer; font-weight: 600; white-space: nowrap; }
    .loading, .error-msg { text-align: center; padding: 40px; }
    .error-msg { background: #FEF2F2; color: #DC2626; border-radius: 8px; }
  `]
})
export class ProfileComponent implements OnInit {
  user = signal<any>({ fullName: 'User', email: '' });
  walletBalance = signal(0);
  travellers = signal<any[]>([]);
  showAddForm = signal(false);
  newTraveller = { name: '', age: 0, gender: 'M' };
  topupAmount = 0;
  loading = signal(true);
  error = signal('');
  private graphql = inject(GraphqlService);

  ngOnInit() {
    this.graphql.myProfile().subscribe({
      next: (data: any) => { this.user.set(data?.myProfile || this.user()); this.loading.set(false); },
      error: () => { this.loading.set(false); }
    });
    this.graphql.walletBalance().subscribe({
      next: (data: any) => { this.walletBalance.set(data?.walletBalance || 0); },
      error: () => {}
    });
    this.graphql.myTravellers().subscribe({
      next: (data: any) => { this.travellers.set(data?.myTravellers || []); },
      error: () => {}
    });
  }

  saveTraveller() {
    if (this.newTraveller.name) {
      this.graphql.addTraveller(this.newTraveller.name, this.newTraveller.age, this.newTraveller.gender, 'AADHAAR', '').subscribe({
        next: (data: any) => {
          if (data?.addTraveller) this.travellers.update(t => [...t, data.addTraveller]);
          this.newTraveller = { name: '', age: 0, gender: 'M' };
          this.showAddForm.set(false);
        },
        error: () => {}
      });
    }
  }

  removeTraveller(t: any) {
    if (t.id) {
      this.graphql.deleteTraveller(t.id).subscribe({
        next: () => { this.travellers.update(list => list.filter(x => x !== t)); },
        error: () => {}
      });
    }
  }

  topup() {
    if (this.topupAmount > 0) {
      this.graphql.addMoneyToWallet(this.topupAmount).subscribe({
        next: (data: any) => {
          this.walletBalance.set(data?.addMoneyToWallet?.balance || this.walletBalance() + this.topupAmount);
          this.topupAmount = 0;
        },
        error: () => {}
      });
    }
  }
}
