import { Component, signal, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgFor, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, FormsModule, NgFor, NgIf],
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
    <section class="popular-routes container" *ngIf="popularRoutes().length > 0">
      <h2>Popular Routes</h2>
      <div class="routes-grid">
        <div *ngFor="let route of popularRoutes()" class="route-card">
          <span class="mode">{{route.mode}}</span>
          <span class="route-text">{{route.from}} → {{route.to}}</span>
          <span class="fare">₹{{route.avgFare}}</span>
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
    .features h2, .popular-routes h2 { text-align: center; font-size: 32px; margin-bottom: 48px; }
    .feature-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 24px; }
    .feature-card { background: white; padding: 32px; border-radius: 16px; text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .feature-card .icon { font-size: 48px; margin-bottom: 16px; }
    .feature-card h3 { font-size: 20px; margin-bottom: 8px; }
    .feature-card p { color: var(--text-secondary); }
    .popular-routes { padding: 40px 0 80px; }
    .routes-grid { display: grid; grid-template-columns: repeat(3, 1fr); gap: 16px; }
    .route-card { background: white; padding: 20px; border-radius: 12px; display: flex; align-items: center; gap: 16px; box-shadow: 0 1px 3px rgba(0,0,0,0.1); }
    .route-card .mode { background: #EFF6FF; color: var(--primary); padding: 4px 12px; border-radius: 20px; font-size: 12px; font-weight: 600; }
    .route-card .route-text { font-weight: 600; flex: 1; }
    .route-card .fare { font-weight: 700; color: var(--primary); }
  `]
})
export class HomeComponent implements OnInit {
  tabs = ['Flights', 'Buses', 'Trains', 'Multi-Modal'];
  activeTab = signal('Flights');
  from = '';
  to = '';
  date = new Date().toISOString().split('T')[0];
  popularRoutes = signal<any[]>([]);
  private graphql = inject(GraphqlService);
  private router = inject(Router);

  ngOnInit() {
    this.graphql.popularRoutes(undefined, 6).subscribe({
      next: (data: any) => { this.popularRoutes.set(data?.popularRoutes || []); },
      error: () => {}
    });
  }

  search() {
    const mode = this.activeTab().toLowerCase();
    this.router.navigate(['/search', mode === 'multi-modal' ? 'multi-modal' : mode + 's'],
      { queryParams: { from: this.from, to: this.to, date: this.date } });
  }
}
