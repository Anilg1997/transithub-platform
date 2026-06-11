import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { NgIf } from '@angular/common';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, NgIf],
  template: `
    <header class="header">
      <div class="container header-content">
        <a routerLink="/" class="logo">TransitHub</a>
        <nav class="nav">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact:true}">Home</a>
          <a routerLink="/trips" routerLinkActive="active">My Trips</a>
          <a routerLink="/notifications" routerLinkActive="active">Alerts</a>
          <a routerLink="/profile" routerLinkActive="active">Profile</a>
          @if (!isAuthenticated()) {
            <a routerLink="/auth/login" class="btn-primary">Login</a>
          } @else {
            <button (click)="logout()" class="btn-outline">Logout</button>
          }
        </nav>
      </div>
    </header>
    <main class="main">
      <router-outlet/>
    </main>
    <footer class="footer">
      <div class="container">
        <p>&copy; 2024 TransitHub. All rights reserved.</p>
      </div>
    </footer>
  `,
  styles: [`
    .header { background: white; border-bottom: 1px solid #E2E8F0; position: sticky; top: 0; z-index: 100; }
    .header-content { display: flex; align-items: center; justify-content: space-between; height: 64px; }
    .logo { font-size: 24px; font-weight: 700; color: var(--primary); text-decoration: none; }
    .nav { display: flex; align-items: center; gap: 24px; }
    .nav a { color: var(--text-secondary); font-weight: 500; text-decoration: none; }
    .nav a:hover, .nav a.active { color: var(--primary); }
    .btn-primary { background: var(--primary); color: white; padding: 8px 20px; border-radius: 8px; font-weight: 600; }
    .btn-outline { border: 2px solid var(--primary); color: var(--primary); padding: 6px 18px; border-radius: 8px; font-weight: 600; background: transparent; cursor: pointer; }
    .main { min-height: calc(100vh - 128px); }
    .footer { background: var(--text); color: white; text-align: center; padding: 24px 0; }
  `]
})
export class AppComponent {
  isAuthenticated = () => !!localStorage.getItem('accessToken');
  logout() { localStorage.removeItem('accessToken'); localStorage.removeItem('refreshToken'); window.location.reload(); }
}
