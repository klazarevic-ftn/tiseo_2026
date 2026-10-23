import Keycloak from 'keycloak-js';

export const keycloak = new Keycloak({
  url: 'https://desktop-fghbtqa.taild59e37.ts.net/auth',
  realm: 'faculty-of-fictional-sciences',
  clientId: 'frontend'
});
