import { Component, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgFor, CurrencyPipe, NgIf } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';

@Component({
  selector: 'app-payment', standalone: true, imports: [NgFor, FormsModule, CurrencyPipe, NgIf],
  template: `
    <div class="container payment-page">
      <div class="payment-card">
        <h2>Complete Payment</h2>
        <div class="amount-display">
          <span class="label">Total Amount</span>
          <span class="amount">₹{{totalAmount}}</span>
        </div>
        <div class="payment-methods">
          <h3>Select Payment Method</h3>
          <div *ngFor="let method of methods" class="method-card" [class.selected]="selectedMethod() === method.id" (click)="selectedMethod.set(method.id)">
            <span class="method-icon">{{method.icon}}</span>
            <span class="method-name">{{method.name}}</span>
          </div>
        </div>
        <button (click)="pay()" class="pay-btn" [disabled]="processing()">{{processing() ? 'Processing...' : 'Pay ₹' + totalAmount}}</button>
        <div *ngIf="paymentResult()" class="payment-result" [class.success]="paymentResult() === 'SUCCESS'" [class.failed]="paymentResult() === 'FAILED'">
          {{paymentResult() === 'SUCCESS' ? 'Payment Successful! 🎉 Redirecting...' : 'Payment Failed. Please try again.'}}
        </div>
      </div>
    </div>
  `,
  styles: [`
    .payment-page { padding: 40px 0; max-width: 500px; margin: 0 auto; }
    .payment-card { background: white; border-radius: 16px; padding: 40px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }
    .payment-card h2 { margin-bottom: 24px; }
    .amount-display { display: flex; justify-content: space-between; align-items: center; padding: 16px; background: #EFF6FF; border-radius: 12px; margin-bottom: 24px; }
    .amount-display .label { color: var(--text-secondary); }
    .amount-display .amount { font-size: 28px; font-weight: 700; color: var(--primary); }
    .payment-methods h3 { margin-bottom: 16px; font-size: 16px; }
    .method-card { display: flex; align-items: center; gap: 12px; padding: 16px; border: 2px solid #E2E8F0; border-radius: 10px; margin-bottom: 12px; cursor: pointer; transition: all 0.2s; }
    .method-card:hover, .method-card.selected { border-color: var(--primary); background: #EFF6FF; }
    .method-icon { font-size: 24px; }
    .method-name { font-weight: 500; }
    .pay-btn { width: 100%; padding: 16px; background: var(--accent); color: white; border: none; border-radius: 12px; font-size: 18px; font-weight: 700; cursor: pointer; margin-top: 16px; }
    .pay-btn:disabled { opacity: 0.6; cursor: not-allowed; }
    .pay-btn:hover { background: #059669; }
    .payment-result { padding: 16px; border-radius: 12px; text-align: center; margin-top: 16px; font-weight: 600; }
    .success { background: #D1FAE5; color: #065F46; }
    .failed { background: #FEF2F2; color: #DC2626; }
  `]
})
export class PaymentComponent implements OnInit {
  totalAmount = 5500;
  selectedMethod = signal('UPI');
  paymentResult = signal<string | null>(null);
  processing = signal(false);
  bookingRef = '';
  transactionId = '';
  methods = [
    { id: 'UPI', name: 'UPI (Google Pay / PhonePe)', icon: '📱' },
    { id: 'CARD', name: 'Credit / Debit Card', icon: '💳' },
    { id: 'NETBANKING', name: 'Net Banking', icon: '🏦' },
    { id: 'WALLET', name: 'Wallet', icon: '💰' },
  ];
  private graphql = inject(GraphqlService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit() {
    this.route.params.subscribe(params => {
      this.bookingRef = params['ref'];
    });
  }

  pay() {
    this.processing.set(true);
    this.graphql.initiatePayment(this.bookingRef, this.totalAmount, this.selectedMethod()).subscribe({
      next: (data: any) => {
        this.transactionId = data?.initiatePayment?.transactionId;
        this.graphql.confirmPayment(this.transactionId, true).subscribe({
          next: (result: any) => {
            const status = result?.confirmPayment?.status;
            this.paymentResult.set(status === 'SUCCESS' ? 'SUCCESS' : 'FAILED');
            this.processing.set(false);
            if (status === 'SUCCESS') {
              setTimeout(() => this.router.navigate(['/trips']), 2000);
            }
          },
          error: () => {
            this.paymentResult.set('FAILED');
            this.processing.set(false);
          }
        });
      },
      error: () => {
        this.paymentResult.set('FAILED');
        this.processing.set(false);
      }
    });
  }
}
