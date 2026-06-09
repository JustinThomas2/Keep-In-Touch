import { Injectable } from '@angular/core';
import { Apollo, onlyCompleteData } from 'apollo-angular';
import { map, Observable } from 'rxjs';

import type { Company, CompanyDetail, Contact, CreateCompanyInput, CreateContactInput } from './crm.types';
import {
  CompaniesDocument,
  CompaniesQuery,
  CompanyDocument,
  CompanyQuery,
  CompanyQueryVariables,
  ContactDocument,
  ContactQuery,
  ContactQueryVariables,
  ContactsDocument,
  ContactsQuery,
  CreateCompanyDocument,
  CreateCompanyMutation,
  CreateCompanyMutationVariables,
  CreateContactDocument,
  CreateContactMutation,
  CreateContactMutationVariables
} from './graphql/generated';

@Injectable({ providedIn: 'root' })
export class CrmService {
  constructor(private readonly apollo: Apollo) {}

  companies(): Observable<Company[]> {
    return this.apollo
      .watchQuery<CompaniesQuery>({
        query: CompaniesDocument,
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.companies)
      );
  }

  company(id: string): Observable<CompanyDetail | null> {
    return this.apollo
      .watchQuery<CompanyQuery, CompanyQueryVariables>({
        query: CompanyDocument,
        variables: { id },
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.company)
      );
  }

  contacts(): Observable<Contact[]> {
    return this.apollo
      .watchQuery<ContactsQuery>({
        query: ContactsDocument,
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.contacts)
      );
  }

  contact(id: string): Observable<Contact | null> {
    return this.apollo
      .watchQuery<ContactQuery, ContactQueryVariables>({
        query: ContactDocument,
        variables: { id },
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.contact)
      );
  }

  createCompany(input: CreateCompanyInput): Observable<Company> {
    return this.apollo
      .mutate<CreateCompanyMutation, CreateCompanyMutationVariables>({
        mutation: CreateCompanyDocument,
        variables: { input },
        refetchQueries: [{ query: CompaniesDocument }]
      })
      .pipe(map((result) => this.requiredData(result.data).createCompany));
  }

  createContact(input: CreateContactInput): Observable<Contact> {
    return this.apollo
      .mutate<CreateContactMutation, CreateContactMutationVariables>({
        mutation: CreateContactDocument,
        variables: { input },
        refetchQueries: [{ query: ContactsDocument }, { query: CompaniesDocument }]
      })
      .pipe(map((result) => this.requiredData(result.data).createContact));
  }

  private requiredData<T>(data: T | null | undefined): T {
    if (data === null || data === undefined) {
      throw new Error('GraphQL mutation completed without response data.');
    }

    return data;
  }
}
