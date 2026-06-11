import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule, NgFor],
  template: `
    <section class="hero">
      <div class="container">
        <h1>Book Bus, Train & Flight Tickets</h1>
        <p class="subtitle">India's most trusted travel platform. One app, all transport.</p>
        <div class="search-tabs">
          <button *ngFor="let tab of tabs" (click)="activeTab.set(tab)" 
                  [class.active]="activeTab() === tab" class="tab-btn">{{tab}}</button>
        </div>
        <div class="search-form">
          <input [(ngModel)]="from" placeholder="From" class="search-input">
          <input [(ngModel)]="to" placeholder="To" class="search-input">
          <input [(ngModel)]="date" type="date" class="search-input">
          <button (click)="search()" class="search-btn">Search</button>
        </div>
      </div>
    </section>
    <section class="features container">
      <h2>Why TransitHub?</h2>
      <div class="feature-grid">
        <div class="feature-card">
          <div class="icon">✈️</div>
          <h3>Flights</h3>
          <p>500+ domestic & international routes</p>
        </div>
        <div class="feature-card">
          <div class="icon">🚌</div>
          <h3>Buses</h3>
          <p>3000+ routes across 200+ operators</p>
        </div>
        <div class="feature-card">
          <div class="icon">🚄</div>
          <h3>Trains</h3>
          <p>IRCTC integration, real-time PNR</p>
        </div>
        <div class="feature-card">
          <div class="icon">🔄</div>
          <h3>Multi-Modal</h3>
          <p>Plan combined bus+train+flight trips</p>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .hero { background: linear-gradient(135deg, #1E40AF, #2563EB); color: white; padding: 80px 0 60px; text-align: center; }
    .hero h1 { font-size: 48px; font-weight: 700; margin-bottom: 12px; }
    .subtitle { font-size: 18px; opacity: 0.9; margin-bottom: 40px; }
    .search-tabs { display: flex; justify-content: center; gap: 8px; margin-bottom: 24px; }
    .tab-btn { padding: 10px 28px; border-radius: 8px; border: 2px solid rgba(255,255,255,0.3); background: transparent; color: white; font-weight: 600; cursor: pointer; }
    .tab-btn.active { background: white; color: var(--primary); border-color: white; }
    .search-form { display: flex; gap: 12px; max-width: 700px; margin: 0 auto; }
    .search-input { flex: 1; padding: 14px 18px; border-radius: 12px; border: none; font-size: 16px; }
    .search-btn { background: var(--accent); color: white; border: none; padding: 14px 36px; border-radius: 12px; font-weight: 600; font-size: 16px; cursor: pointer; }
    .features { padding: 80px 0; }
    .features h2 { text-align: center; font-size: 32px; margin-bottom: 48px; }
    .feature-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
    .feature-card { background: white; padding: 32px; border-radius: 16px; text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .feature-card .icon { font-size: 48px; margin-bottom: 16px; }
    .feature-card h3 { font-size: 20px; margin-bottom: 8px; }
    .feature-card p { color: var(--text-secondary); }
  `]
})
export class HomeComponent {
  tabs = ['Flights', 'Buses', 'Trains', 'Multi-Modal'];
  activeTab = signal('Flights');
  from = ''; to = ''; date = new Date().toISOString().split('T')[0];
  constructor(private router: Router) {}
  search() {
    const mode = this.activeTab().toLowerCase();
    this.router.navigate(['/search', mode === 'multi-modal' ? 'multi-modal' : mode + 's'], 
      { queryParams: { from: this.from, to: this.to, date: this.date } });
  }
}
