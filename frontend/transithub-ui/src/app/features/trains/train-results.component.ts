import { Component, signal } from '@angular/core';
import { Router } from '@angular/router';
import { NgFor, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-train-results', standalone: true, imports: [NgFor, CurrencyPipe],
  template: `
    <div class="container results-page">
      <div class="results">
        <div *ngFor="let train of trains()" class="train-card" (click)="selectTrain(train)">
          <div class="train-header">
            <span class="train-name">{{train.trainName}}</span>
            <span class="train-number">{{train.trainNumber}}</span>
          </div>
          <div class="train-details">
            <div class="time-block">
              <span class="time">{{train.departureTime}}</span>
              <span class="station">{{train.origin}}</span>
            </div>
            <div class="duration-block">
              <span>{{train.duration}} min</span>
              <div class="line"></div>
            </div>
            <div class="time-block">
              <span class="time">{{train.arrivalTime}}</span>
              <span class="station">{{train.destination}}</span>
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
  `]
})
export class TrainResultsComponent {
  trains = signal([
    { id: '1', trainNumber: '12301', trainName: 'Rajdhani Express', origin: 'NDLS', destination: 'HWH', departureTime: '16:55', arrivalTime: '09:55', duration: 1020, coaches: [{coachType:'3AC', fare: 1800, availableBerths: 45},{coachType:'2AC', fare: 2500, availableBerths: 12},{coachType:'1AC', fare: 4200, availableBerths: 5}] },
    { id: '2', trainNumber: '12951', trainName: 'Mumbai Rajdhani', origin: 'NDLS', destination: 'BCT', departureTime: '16:35', arrivalTime: '08:35', duration: 960, coaches: [{coachType:'3AC', fare: 2200, availableBerths: 8},{coachType:'2AC', fare: 3000, availableBerths: 3}] },
  ]);
  constructor(private router: Router) {}
  selectTrain(train: any) { this.router.navigate(['/booking/train', train.id]); }
  selectCoach(train: any, coach: any) { this.router.navigate(['/booking/train', train.id], { queryParams: { coach: coach.coachType } }); }
}
