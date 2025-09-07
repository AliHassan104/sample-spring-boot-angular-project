// src/app/services/auth.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { BehaviorSubject, Observable, catchError, map, throwError } from 'rxjs';

export interface User {
  username: string;
  role: string;
  token: string;
  roles?: string[];
  permissions?: string[];
}

export interface LoginRequest {
  name: string;
  password: string;
}

export interface AuthenticationResponse {
  jwt: string;
  tokenType: string;
  expiresIn: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = 'http://localhost:8080/api'; // Adjust to your backend URL
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    // Check if user is already logged in
    const savedUser = localStorage.getItem('currentUser');
    if (savedUser) {
      try {
        const user = JSON.parse(savedUser);
        this.currentUserSubject.next(user);
      } catch (error) {
        console.error('Error parsing saved user:', error);
        localStorage.removeItem('currentUser');
      }
    }
  }

  login(username: string, password: string): Observable<boolean> {
    const loginRequest: LoginRequest = {
      name: username,
      password: password
    };

    const headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    return this.http.post<AuthenticationResponse>(`${this.API_URL}/login`, loginRequest, { headers })
      .pipe(
        map(response => {
          if (response && response.jwt) {
            // Decode JWT to get user info (basic decoding - in production use a proper JWT library)
            const decodedToken = this.decodeJWT(response.jwt);
            
            const user: User = {
              username: username,
              role: this.extractPrimaryRole(decodedToken.ROLES || []),
              token: response.jwt,
              roles: decodedToken.ROLES || [],
              permissions: decodedToken.PERMISSIONS || []
            };

            // Store user in localStorage
            localStorage.setItem('currentUser', JSON.stringify(user));
            this.currentUserSubject.next(user);
            
            return true;
          }
          return false;
        }),
        catchError(error => {
          console.error('Login error:', error);
          return throwError(() => error);
        })
      );
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }

  isLoggedIn(): boolean {
    const user = this.getCurrentUser();
    if (!user || !user.token) {
      return false;
    }

    // Check if token is expired
    try {
      const decodedToken = this.decodeJWT(user.token);
      const currentTime = Date.now() / 1000;
      
      if (decodedToken.exp && decodedToken.exp < currentTime) {
        this.logout();
        return false;
      }
      
      return true;
    } catch (error) {
      console.error('Error checking token validity:', error);
      this.logout();
      return false;
    }
  }

  getToken(): string | null {
    const user = this.getCurrentUser();
    return user ? user.token : null;
  }

  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    return user?.roles?.includes(role) || false;
  }

  hasPermission(permission: string): boolean {
    const user = this.getCurrentUser();
    return user?.permissions?.includes(permission) || false;
  }

  // Basic JWT decoder (for demo purposes - use a proper library in production)
  private decodeJWT(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
      }).join(''));

      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('Error decoding JWT:', error);
      return {};
    }
  }

  // Extract primary role for display purposes
  private extractPrimaryRole(roles: string[]): string {
    if (roles.includes('ADMIN')) return 'admin';
    if (roles.includes('TEACHER')) return 'teacher';
    if (roles.includes('STUDENT')) return 'student';
    return roles[0]?.toLowerCase() || 'user';
  }
}