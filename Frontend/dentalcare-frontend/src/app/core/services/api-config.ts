import { InjectionToken } from '@angular/core';

/** API Gateway base URL — pending environment-specific overrides. */
export const API_GATEWAY_URL = new InjectionToken<string>('API_GATEWAY_URL', {
  providedIn: 'root',
  factory: () => 'http://localhost:8080',
});
