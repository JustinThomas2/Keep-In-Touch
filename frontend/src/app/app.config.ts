import type { ApplicationConfig } from '@angular/core';
import { inject, provideZoneChangeDetection } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { InMemoryCache } from '@apollo/client/cache';
import { provideApollo } from 'apollo-angular';
import { HttpLink } from 'apollo-angular/http';

import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideHttpClient(),
    provideRouter(routes),
    provideApollo(() => {
      const httpLink = inject(HttpLink);

      return {
        cache: new InMemoryCache(),
        link: httpLink.create({ uri: '/graphql' })
      };
    })
  ]
};
