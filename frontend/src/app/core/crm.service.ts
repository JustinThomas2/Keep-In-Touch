import { inject, Injectable } from '@angular/core';
import { Apollo, onlyCompleteData } from 'apollo-angular';
import type { Observable } from 'rxjs';
import { map } from 'rxjs';

import type {
  Company,
  CompanyDetail,
  Contact,
  CreateCompanyInput,
  CreateContactInput,
  CreateFollowUpInput,
  CreateInteractionInput,
  Dashboard,
  FollowUp,
  Interaction,
  UpdateContactInput,
  UpdateInteractionInput,
} from './crm.types';
import type {
  CancelFollowUpMutation,
  CancelFollowUpMutationVariables,
  CompaniesQuery,
  CompleteFollowUpMutation,
  CompleteFollowUpMutationVariables,
  CompanyQuery,
  CompanyQueryVariables,
  ContactInteractionsQuery,
  ContactInteractionsQueryVariables,
  ContactQuery,
  ContactQueryVariables,
  ContactsQuery,
  CreateCompanyMutation,
  CreateCompanyMutationVariables,
  CreateContactMutation,
  CreateContactMutationVariables,
  CreateFollowUpMutation,
  CreateFollowUpMutationVariables,
  CreateInteractionMutation,
  CreateInteractionMutationVariables,
  DashboardQuery,
  OpenFollowUpQuery,
  OpenFollowUpQueryVariables,
  UpdateContactMutation,
  UpdateContactMutationVariables,
  UpdateInteractionMutation,
  UpdateInteractionMutationVariables,
} from './graphql/generated';
import {
  CancelFollowUpDocument,
  CompaniesDocument,
  CompleteFollowUpDocument,
  CompanyDocument,
  ContactDocument,
  ContactInteractionsDocument,
  ContactsDocument,
  CreateCompanyDocument,
  CreateContactDocument,
  CreateFollowUpDocument,
  CreateInteractionDocument,
  DashboardDocument,
  OpenFollowUpDocument,
  UpdateContactDocument,
  UpdateInteractionDocument,
} from './graphql/generated';

@Injectable({ providedIn: 'root' })
export class CrmService {
  private readonly apollo = inject(Apollo);

  companies(): Observable<Company[]> {
    return this.apollo
      .watchQuery<CompaniesQuery>({
        query: CompaniesDocument,
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.companies),
      );
  }

  company(id: string): Observable<CompanyDetail | null> {
    return this.apollo
      .watchQuery<CompanyQuery, CompanyQueryVariables>({
        query: CompanyDocument,
        variables: { id },
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.company),
      );
  }

  contacts(): Observable<Contact[]> {
    return this.apollo
      .watchQuery<ContactsQuery>({
        query: ContactsDocument,
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.contacts),
      );
  }

  contact(id: string): Observable<Contact | null> {
    return this.apollo
      .watchQuery<ContactQuery, ContactQueryVariables>({
        query: ContactDocument,
        variables: { id },
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.contact),
      );
  }

  contactInteractions(contactId: string): Observable<Interaction[]> {
    return this.apollo
      .watchQuery<ContactInteractionsQuery, ContactInteractionsQueryVariables>({
        query: ContactInteractionsDocument,
        variables: { contactId },
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.contactInteractions),
      );
  }

  openFollowUp(contactId: string): Observable<FollowUp | null> {
    return this.apollo
      .watchQuery<OpenFollowUpQuery, OpenFollowUpQueryVariables>({
        query: OpenFollowUpDocument,
        variables: { contactId },
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.openFollowUp),
      );
  }

  dashboard(): Observable<Dashboard> {
    return this.apollo
      .watchQuery<DashboardQuery>({
        query: DashboardDocument,
        fetchPolicy: 'cache-and-network',
      })
      .valueChanges.pipe(
        onlyCompleteData(),
        map((result) => result.data.dashboard),
      );
  }

  createCompany(input: CreateCompanyInput): Observable<Company> {
    return this.apollo
      .mutate<CreateCompanyMutation, CreateCompanyMutationVariables>({
        mutation: CreateCompanyDocument,
        variables: { input },
        refetchQueries: [{ query: CompaniesDocument }],
      })
      .pipe(map((result) => this.requiredData(result.data).createCompany));
  }

  createContact(input: CreateContactInput): Observable<Contact> {
    return this.apollo
      .mutate<CreateContactMutation, CreateContactMutationVariables>({
        mutation: CreateContactDocument,
        variables: { input },
        refetchQueries: [
          { query: ContactsDocument },
          { query: CompaniesDocument },
        ],
      })
      .pipe(map((result) => this.requiredData(result.data).createContact));
  }

  updateContact(input: UpdateContactInput): Observable<Contact> {
    return this.apollo
      .mutate<UpdateContactMutation, UpdateContactMutationVariables>({
        mutation: UpdateContactDocument,
        variables: { input },
        refetchQueries: [
          { query: ContactDocument, variables: { id: input.id } },
          { query: ContactsDocument },
          { query: CompaniesDocument },
          { query: DashboardDocument },
        ],
      })
      .pipe(map((result) => this.requiredData(result.data).updateContact));
  }

  createInteraction(input: CreateInteractionInput): Observable<Interaction> {
    return this.apollo
      .mutate<CreateInteractionMutation, CreateInteractionMutationVariables>({
        mutation: CreateInteractionDocument,
        variables: { input },
        refetchQueries: [
          { query: ContactDocument, variables: { id: input.contactId } },
          {
            query: ContactInteractionsDocument,
            variables: { contactId: input.contactId },
          },
        ],
      })
      .pipe(map((result) => this.requiredData(result.data).createInteraction));
  }

  updateInteraction(
    input: UpdateInteractionInput,
    contactId: string,
  ): Observable<Interaction> {
    return this.apollo
      .mutate<UpdateInteractionMutation, UpdateInteractionMutationVariables>({
        mutation: UpdateInteractionDocument,
        variables: { input },
        refetchQueries: [
          { query: ContactDocument, variables: { id: contactId } },
          { query: ContactInteractionsDocument, variables: { contactId } },
        ],
      })
      .pipe(map((result) => this.requiredData(result.data).updateInteraction));
  }

  createFollowUp(input: CreateFollowUpInput): Observable<FollowUp> {
    return this.apollo
      .mutate<CreateFollowUpMutation, CreateFollowUpMutationVariables>({
        mutation: CreateFollowUpDocument,
        variables: { input },
        refetchQueries: this.followUpRefetchQueries(input.contactId),
      })
      .pipe(map((result) => this.requiredData(result.data).createFollowUp));
  }

  completeFollowUp(id: string, contactId: string): Observable<FollowUp> {
    return this.apollo
      .mutate<CompleteFollowUpMutation, CompleteFollowUpMutationVariables>({
        mutation: CompleteFollowUpDocument,
        variables: { id },
        refetchQueries: this.followUpRefetchQueries(contactId),
      })
      .pipe(map((result) => this.requiredData(result.data).completeFollowUp));
  }

  cancelFollowUp(id: string, contactId: string): Observable<FollowUp> {
    return this.apollo
      .mutate<CancelFollowUpMutation, CancelFollowUpMutationVariables>({
        mutation: CancelFollowUpDocument,
        variables: { id },
        refetchQueries: this.followUpRefetchQueries(contactId),
      })
      .pipe(map((result) => this.requiredData(result.data).cancelFollowUp));
  }

  private followUpRefetchQueries(contactId: string) {
    return [
      { query: ContactDocument, variables: { id: contactId } },
      { query: OpenFollowUpDocument, variables: { contactId } },
      { query: DashboardDocument },
    ];
  }

  private requiredData<T>(data: T | null | undefined): T {
    if (data === null || data === undefined) {
      throw new Error('GraphQL mutation completed without response data.');
    }

    return data;
  }
}
