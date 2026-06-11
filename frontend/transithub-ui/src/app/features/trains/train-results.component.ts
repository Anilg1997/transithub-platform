import { Component, signal, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { NgFor, NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-train-results', standalone: true, imports: [NgFor, NgIf],
  template: `
    <div class="container results-page">
      <div class="results">
        <div *ngIf="loading()" class="loading">Loading trains...</div>
        <div *ngIf="error()" class="error-msg">{{error()}}</div>
        <div *ngFor="let train of trains()" class="train-card" (click)="selectTrain(train)">
          <div class="train-header">
            <span class="train-name">{{train.trainName}}</span>
            <span class="train-number">{{train.trainNumber}}</span>
          </div>
          <div class="train-details">
            <div class="time-block">
              <span class="time">{{train.departureTime}}</span>
              <span class="station">{{train.origin?.code || train.origin}}</span>
            </div>
            <div class="duration-block">
              <span>{{train.duration}} min</span>
              <div class="line"></div>
            </div>
            <div class="time-block">
              <span class="time">{{train.arrivalTime}}</span>
              <span class="station">{{train.destination?.code || train.destination}}</span>
            </div>
            <div class="coaches-block">
              <div *ngFor="let c of train.coaches" class="coach-tag" (click)="$event.stopPropagation(); selectCoach(train, c)">
                <span class="coach-type">{{c.coachType}}</span>
                <span class="coach-fare">₹{{c.fare}}</span>
                <span class="coach-seats" [class.low]="c.availableBerths < 10">{{c.availableBerths}} left</span>
              </div>
            </div>
          </div>
        </div>
        <div *ngIf="!loading() && trains().length === 0" class="no-results">No trains found for this route.</div>
      </div>
    </div>
  `,
  styles: [`
    .results-page { padding: 32px 0; }
    .results { display: flex; flex-direction: column; gap: 16px; }
    .train-card { background: white; border-radius: 12px; padding: 20px; cursor: pointer; border: 1px solid #E2E8F0; transition: all 0.2s; }
    .train-card:hover { border-color: var(--primary); box-shadow: 0 4px 12px rgba(37,99,235,0.15); }
    .train-header { display: flex; justify-content: space-between; margin-bottom: 16px; }
    .train-name { font-weight: 600; font-size: 18px; }
    .train-number { color: var(--text-secondary); }
    .train-details { display: flex; align-items: center; gap: 24px; }
    .time-block { text-align: center; }
    .time { display: block; font-size: 20px; font-weight: 700; }
    .station { font-size: 13px; color: var(--text-secondary); }
    .duration-block { flex: 1; text-align: center; font-size: 14px; color: var(--text-secondary); }
    .line { border-top: 2px solid #E2E8F0; margin: 8px 0; }
    .coaches-block { display: flex; gap: 8px; flex-wrap: wrap; }
    .coach-tag { background: #F8FAFC; padding: 8px 12px; border-radius: 8px; text-align: center; cursor: pointer; border: 1px solid #E2E8F0; }
    .coach-type { display: block; font-weight: 600; font-size: 13px; }
    .coach-fare { display: block; color: var(--primary); font-weight: 700; }
    .coach-seats { font-size: 11px; }
    .low { color: var(--danger); }
    .loading, .no-results { text-align: center; padding: 40px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; text-align: center; }
  `]
})
export class TrainResultsComponent implements OnInit {
  trains = signal<any[]>([]);
  loading = signal(true);
  error = signal('');
  private graphql = inject(GraphqlService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const from = params['from'] || 'NDLS';
      const to = params['to'] || 'HWH';
      const date = params['date'] || new Date().toISOString().split('T')[0];
      this.loading.set(true);
      this.graphql.searchTrains(from, to, date).subscribe({
        next: (data: any) => {
          this.trains.set(data?.searchTrains || []);
          this.loading.set(false);
        },
        error: (err: any) => {
          this.error.set(err?.message || 'Failed to load trains');
          this.loading.set(false);
        }
      });
    });
  }

  selectTrain(train: any) { this.router.navigate(['/booking/train', train.id]); }
  selectCoach(train: any, coach: any) { this.router.navigate(['/booking/train', train.id], { queryParams: { coach: coach.coachType } }); }
}
