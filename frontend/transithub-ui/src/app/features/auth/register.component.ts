import { Component } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register', standalone: true, imports: [FormsModule, RouterLink],
  template: `
    <div class="auth-container">
      <div class="auth-card">
        <h2>Create Account</h2>
        <p class="subtitle">Join TransitHub today</p>
        <form (ngSubmit)="register()" class="auth-form">
          <input [(ngModel)]="fullName" name="name" placeholder="Full Name" required class="input">
          <input [(ngModel)]="email" name="email" placeholder="Email" required class="input">
          <input [(ngModel)]="phone" name="phone" placeholder="Phone" required class="input">
          <input [(ngModel)]="password" name="password" type="password" placeholder="Password" required class="input">
          <button type="submit" class="btn-primary">Create Account</button>
        </form>
        <p class="auth-link">Already have an account? <a routerLink="/auth/login">Sign In</a></p>
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
    .auth-link { text-align: center; margin-top: 24px; color: var(--text-secondar
