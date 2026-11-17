import { HttpInterceptorFn } from '@angular/common/http';
import { catchError, defer, switchMap, throwError } from 'rxjs';
import { keycloak } from './keycloak';

const apiOrigins = [
  'http://localhost:8081',
  'http://localhost:8082',
  'http://localhost:8083',
  'http://localhost:8084',
  'http://localhost:8085',
  'http://localhost:8086'
];

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const url = new URL(request.url, window.location.origin);

  if (!apiOrigins.includes(url.origin)) {
    return next(request);
  }

  return defer(() => keycloak.updateToken(30)).pipe(
    catchError(error => {
      void keycloak.login();
      return throwError(() => error);
    }),
    switchMap(() => {
      const authenticatedRequest = request.clone({
        setHeaders: {
          Authorization: `Bearer ${keycloak.token}`
        }
      });

      return next(authenticatedRequest);
    })
  );
};
