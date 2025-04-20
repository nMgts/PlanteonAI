import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';
import { catchError, Observable, switchMap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TokenInterceptorService implements HttpInterceptor {

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const token = localStorage.getItem('accessToken');

    let clonedRequest = req;

    if (token) {
      clonedRequest = req.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(clonedRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401) {
          return this.authService.refreshToken().pipe(
            switchMap((response) => {
              if (response.statusCode === 200) {
                localStorage.setItem('accessToken', response.accessToken);

                const retryRequest = req.clone({
                  setHeaders: {
                    Authorization: `Bearer ${response.accessToken}`
                  }
                });
                return next.handle(retryRequest);
              } else {
                this.authService.logout();
                this.router.navigate(['/login']);
                return throwError(() => new Error('Unauthorized'));
              }
            }),
            catchError(() => {
              this.authService.logout();
              this.router.navigate(['/login']);
              return throwError(() => new Error('Unauthorized'));
            })
          );
        }

        return throwError(() => error);
      })
    )
  }
}
