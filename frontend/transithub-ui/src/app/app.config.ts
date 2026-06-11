import { ApplicationConfig, inject } from '@angular/core';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { InMemoryCache, split } from '@apollo/client/core';
import { GraphQLWsLink } from '@apollo/client/link/subscriptions';
import { HttpLink } from 'apollo-angular/http';
import { createClient } from 'graphql-ws';
import { Apollo, APOLLO_OPTIONS } from 'apollo-angular';
import { setContext } from '@apollo/client/link/context';
import { routes } from './app.routes';
import { environment } from '../environments/environment';

const authLink = setContext((operation, prevCtx) => {
  const token = localStorage.getItem('accessToken');
  const serviceName = (prevCtx as any)?.serviceName || '';
  return {
    headers: {
      ...(prevCtx as any).headers,
      Authorization: token ? `Bearer ${token}` : '',
      ...(serviceName ? { 'X-Service': serviceName } : {}),
    }
  };
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withComponentInputBinding()),
    provideHttpClient(),
    provideAnimationsAsync(),
    {
      provide: APOLLO_OPTIONS,
      useFactory: () => {
        const httpLink = inject(HttpLink);
        const http = httpLink.create({ uri: environment.graphqlEndpoint });
        const wsLink = new GraphQLWsLink(createClient({ url: environment.graphqlWsEndpoint }));
        const link = split(
          ({ query }) => {
            const def = query.definitions[0];
            return def.kind === 'OperationDefinition' && def.operation === 'subscription';
          },
          wsLink,
          authLink.concat(http)
        );
        return {
          link,
          cache: new InMemoryCache(),
          defaultOptions: {
            watchQuery: { fetchPolicy: 'network-only' },
            query: { fetchPolicy: 'network-only' },
          }
        };
      },
      deps: [],
    },
    Apollo,
  ],
};
