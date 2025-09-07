// src/app/dashboard/dashboard.component.ts
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService, User } from '../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-container">
      <div class="header">
        <h1>Dashboard</h1>
        <button (click)="logout()" class="logout-btn">Logout</button>
      </div>
      
      <div class="welcome-card" *ngIf="currentUser">
        <h2>Hello {{ currentUser.role | titlecase }}!</h2>
        <p>Welcome back, <strong>{{ currentUser.username }}</strong></p>
        <div class="user-info">
          <div class="info-item">
            <span class="label">Role:</span>
            <span class="value" [ngClass]="'role-' + currentUser.role">
              {{ currentUser.role | titlecase }}
            </span>
          </div>
          <div class="info-item">
            <span class="label">Username:</span>
            <span class="value">{{ currentUser.username }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .dashboard-container {
      min-height: 100vh;
      background-color: #f8f9fa;
      padding: 2rem;
    }
    
    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 2rem;
      background: white;
      padding: 1rem 2rem;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }
    
    .header h1 {
      margin: 0;
      color: #333;
    }
    
    .logout-btn {
      padding: 0.5rem 1rem;
      background-color: #dc3545;
      color: white;
      border: none;
      border-radius: 4px;
      cursor: pointer;
      font-size: 0.9rem;
    }
    
    .logout-btn:hover {
      background-color: #c82333;
    }
    
    .welcome-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
      max-width: 600px;
      margin: 0 auto;
      text-align: center;
    }
    
    .welcome-card h2 {
      color: #333;
      margin-bottom: 1rem;
      font-size: 2rem;
    }
    
    .welcome-card p {
      color: #666;
      font-size: 1.1rem;
      margin-bottom: 2rem;
    }
    
    .user-info {
      display: flex;
      justify-content: center;
      gap: 2rem;
      flex-wrap: wrap;
    }
    
    .info-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.5rem;
    }
    
    .label {
      font-weight: bold;
      color: #666;
      font-size: 0.9rem;
      text-transform: uppercase;
    }
    
    .value {
      font-size: 1.1rem;
      padding: 0.5rem 1rem;
      border-radius: 4px;
      font-weight: 500;
    }
    
    .role-admin {
      background-color: #dc3545;
      color: white;
    }
    
    .role-teacher {
      background-color: #28a745;
      color: white;
    }
    
    .role-student {
      background-color: #007bff;
      color: white;
    }
    
    @media (max-width: 768px) {
      .dashboard-container {
        padding: 1rem;
      }
      
      .header {
        flex-direction: column;
        gap: 1rem;
        text-align: center;
      }
      
      .user-info {
        flex-direction: column;
        gap: 1rem;
      }
    }
  `]
})
export class DashboardComponent implements OnInit {
  currentUser: User | null = null;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
    });
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}