import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';
import { appConfig } from './app/app.config';
import { keycloak } from './app/keycloak';

async function start(): Promise<void> {
  const authenticated = await keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256'
  });

  if (authenticated) {
    await bootstrapApplication(App, appConfig);
  }
}

start().catch(error => {
  console.error('Application startup failed:', error);
});
