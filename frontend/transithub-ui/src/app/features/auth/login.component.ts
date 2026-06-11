import { Component, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgIf } from '@angular/common';
import { inject } from '@angular/core';
import { GraphqlService } from '../../services/graphql.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login', standalone: true, imports: [FormsModule, RouterLink, NgIf],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Welcome Back</h2>
        <p class="subtitle">Sign in to your TransitHub account</p>
        <div *ngIf="error()" class="error-msg">{{error()}}</div>
        <form (ngSubmit)="login()" class="auth-form">
          <input [(ngModel)]="email" name="email" placeholder="Email or Phone" required class="input">
          <input [(ngModel)]="password" name="password" type="password" placeholder="Password" required class="input">
          <button type="submit" class="btn-primary" [disabled]="loading()">{{loading() ? 'Signing In...' : 'Sign In'}}</button>
        </form>
        <p class="auth-link">Don't have an account? <a routerLink="/auth/register">Register</a></p>
      </div>
    </div>
  `,
  styles: [`
    .auth-container { display: flex; justify-content: center; align-items: center; min-height: 80vh; }
    .auth-card { background: white; padding: 48px; border-radius: 16px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); width: 100%; max-width: 420px; }
    .auth-card h2 { font-size: 28px; margin-bottom: 8px; }
    .subtitle { color: var(--text-secondary); margin-bottom: 32px; }
    .auth-form { display: flex; flex-direction: column; gap: 16px; }
    .input { padding: 14px 16px; border: 2px solid #E2E8F0; border-radius: 10px; font-size: 15px; }
    .input:focus { border-color: var(--primary); outline: none; }
    .btn-primary { background: var(--primary); color: white; padding: 14px; border: none; border-radius: 10px; font-weight: 600; font-size: 16px; cursor: pointer; }
    .btn-primary:disabled { opacity: 0.6; cursor: not-allowed; }
    .auth-link { text-align: center; margin-top: 24px; color: var(--text-secondary); }
    .error-msg { background: #FEF2F2; color: #DC2626; padding: 12px; border-radius: 8px; margin-bottom: 16px; text-align: center; font-size: 14px; }
  `]
})
export class LoginComponent {
  email = '';
  password = '';
  loading = signal(false);
  error = signal('');
  private graphql = inject(GraphqlService);
  private authService = inject(AuthService);
  private router = inject(Router);

  login() {
    this.loading.set(true);
    this.error.set('');
    this.graphql.login(this.email, this.password).subscribe({
      next: (data: any) => {
        const authPayload = data?.login;
        if (authPayload?.accessToken) {
          this.authService.setAuth(authPayload.accessToken, authPayload.user);
          this.router.navigate(['/']);
        }
        this.loading.set(false);
      },
      error: (err: any) => {
        this.error.set(err?.message || 'Login failed. Please try again.');
        this.loading.set(false);
      }
    });
  }
}
