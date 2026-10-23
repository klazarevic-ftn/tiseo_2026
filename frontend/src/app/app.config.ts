import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    providePrimeNG({
      license: 'eyJpZCI6IjMyOGY1YmY1LTg4YTItNDA1MS1hNWQ0LTg2MDgzY2Q3MmViZSIsInByb2R1Y3QiOiJwcmltZXVpIiwidGllciI6ImNvbW11bml0eSIsInR5cGUiOiJkZXYiLCJpYXQiOjE3OTI3OTUzMjksImV4cCI6MTgyNDMzMTMyOX0.aRyzBUoZ_sxELa-jvNsU4SXkxD8c4k2lTfQUTSKR9WzmTPDGgrODIRo24FHdLvyHdwvtfoVZvDOrxt7agzdlBg',
      theme: {
        preset: Aura
      }
    })
  ]
};
