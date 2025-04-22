import { Injectable } from '@angular/core';
import { environment } from '../environments/environment';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, of, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginResponse {
  email: string;
  firstName: string;
  lastName: string;
  accessToken: string;
  statusCode: number;
}

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
  statusCode: number;
}

@Injectable({
  providedIn: 'root'
})

export class AuthService {
  private baseUrl = `${environment.apiUrl}/auth`;
  private loginUrl = `${this.baseUrl}/login`;
  private isAuthenticatedUrl = `${this.baseUrl}/is-authenticated`;
  private refreshTokenUrl = `${this.baseUrl}/refresh`
  private logoutUrl = `${this.baseUrl}/logout`;

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  login(credentials: { username: string, password: string }): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(this.loginUrl, credentials, { withCredentials: true }).pipe(
      tap(response => {
        if (response.statusCode === 200) {
          localStorage.setItem('accessToken', response.accessToken);
          localStorage.setItem('firstName', response.firstName);
          localStorage.setItem('lastName', response.lastName);
          localStorage.setItem('email', response.email);
        }
      })
    )
  }

  logout(): Observable<any> {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('firstName');
    localStorage.removeItem('lastName');
    localStorage.removeItem('email');
    this.router.navigate(['/login']);
    return this.http.post(this.logoutUrl, {}, { withCredentials: true });
  }

  refreshToken(): Observable<RefreshTokenResponse> {
    return this.http.post<RefreshTokenResponse>(this.refreshTokenUrl, {}, { withCredentials: true });
  }

  checkAuth(): Observable<boolean> {
    const headers = this.createHeaders();
    return this.http.get(this.isAuthenticatedUrl, {
      headers,
      observe: 'response'
    }).pipe(
      map(response => {
        return response.status === 200;
      }),
      catchError(err => {
        return of(false);
      })
    );
  }


  private createHeaders() {
    const token = localStorage.getItem('accessToken') || '';
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
}
