// src/app/login/login.component.ts
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <h2>Login</h2>
        <form (ngSubmit)="onSubmit()" #loginForm="ngForm">
          <div class="form-group">
            <label for="username">Username:</label>
            <input 
              type="text" 
              id="username" 
              name="username" 
              [(ngModel)]="username" 
              required
              #usernameInput="ngModel"
            >
            <div *ngIf="usernameInput.invalid && usernameInput.touched" class="error">
              Username is required
            </div>
          </div>
          
          <div class="form-group">
            <label for="password">Password:</label>
            <input 
              type="password" 
              id="password" 
              name="password" 
              [(ngModel)]="password" 
              required
              #passwordInput="ngModel"
            >
            <div *ngIf="passwordInput.invalid && passwordInput.touched" class="error">
              Password is required
            </div>
          </div>
          
          <button 
            type="submit" 
            [disabled]="loginForm.invalid || loading"
            class="login-btn"
          >
            {{ loading ? 'Logging in...' : 'Login' }}
          </button>
          
          <div *ngIf="errorMessage" class="error">
            {{ errorMessage }}
          </div>
        </form>
        
        <div class="demo-accounts">
          <h4>Demo Accounts:</h4>
          <div class="account-item">
            <strong>Admin:</strong> admin / password123
            <button (click)="fillCredentials('admin', 'password123')" class="fill-btn">Fill</button>
          </div>
          <div class="account-item">
            <strong>Teacher:</strong> teacher1 / password123
            <button (click)="fillCredentials('teacher1', 'password123')" class="fill-btn">Fill</button>
          </div>
          <div class="account-item">
            <strong>Student:</strong> student1 / password123
            <button (click)="fillCredentials('student1', 'password123')" class="fill-btn">Fill</button>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      display: flex;
      justify-content: center;
      align-items: center;
      min-height: 100vh;
      background-color: #f5f5f5;
    }
    
    .login-card {
      background: white;
      padding: 2rem;
      border-radius: 8px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      width: 100%;
      max-width: 450px;
    }
    
    h2 {
      text-align: center;
      margin-bottom: 2rem;
      color: #333;
    }
    
    .form-group {
      margin-bottom: 1rem;
    }
    
    label {
      display: block;
      margin-bottom: 0.5rem;
      font-weight: bold;
      color: #555;
    }
    
    input {
      width: 100%;
      padding: 0.75rem;
      border: 1px solid #ddd;
      border-radius: 4px;
      font-size: 1rem;
      box-sizing: border-box;
    }
    
    input:focus {
      outline: none;
      border-color: #007bff;
    }
    
    .login-btn {
      width: 100%;
      padding: 0.75rem;
      background-color: #007bff;
      color: white;
      border: none;
      border-radius: 4px;
      font-size: 1rem;
      cursor: pointer;
      margin-top: 1rem;
    }
    
    .login-btn:hover:not(:disabled) {
      background-color: #0056b3;
    }
    
    .login-btn:disabled {
      background-color: #ccc;
      cursor: not-allowed;
    }
    
    .error {
      color: #dc3545;
      font-size: 0.875rem;
      margin-top: 0.25rem;
    }
    
    .demo-accounts {
      margin-top: 2rem;
      padding-top: 1rem;
      border-top: 1px solid #eee;
    }
    
    .demo-accounts h4 {
      margin-bottom: 1rem;
      color: #666;
      text-align: center;
    }
    
    .account-item {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin: 0.75rem 0;
      padding: 0.5rem;
      background-color: #f8f9fa;
      border-radius: 4px;
      font-size: 0.9rem;
    }
    
    .fill-btn {
      padding: 0.25rem 0.5rem;
      background-color: #28a745;
      color: white;
      border: none;
      border-radius: 3px;
      cursor: pointer;
      font-size: 0.8rem;
    }
    
    .fill-btn:hover {
      background-color: #218838;
    }
  `]
})
export class LoginComponent {
  username = '';
  password = '';
  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (!this.username || !this.password) {
      return;
    }

    this.loading = true;
    this.errorMessage = '';

    this.authService.login(this.username, this.password).subscribe({
      next: (success) => {
        this.loading = false;
        if (success) {
          this.router.navigate(['/dashboard']);
        } else {
          this.errorMessage = 'Login failed. Please check your credentials.';
        }
      },
      error: (error) => {
        this.loading = false;
        console.error('Login error:', error);
        
        if (error.status === 401) {
          this.errorMessage = 'Invalid username or password';
        } else if (error.status === 0) {
          this.errorMessage = 'Unable to connect to server. Please check if the backend is running.';
        } else {
          this.errorMessage = 'Login failed. Please try again.';
        }
      }
    });
  }

  fillCredentials(username: string, password: string): void {
    this.username = username;
    this.password = password;
  }
}