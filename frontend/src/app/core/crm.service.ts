import { Injectable } from '@angular/core';
import { Apollo, gql } from 'apollo-angular';
import { map, Observable } from 'rxjs';

import { Company, Contact, CreateCompanyInput, CreateContactInput } from './crm.types';

const COMPANY_FIELDS = gql`
  fragment CompanyFields on Company {
    id
    name
    website
    industry
    location
    notes
    createdAt
    updatedAt
  }
`;

const CONTACT_FIELDS = gql`
  fragment ContactFields on Contact {
    id
    firstName
    lastName
    preferredName
    roleTitle
    location
    linkedinUrl
    email
    phone
    relationshipType
    status
    source
    notes
    birthdayMonth
    birthdayDay
    birthdayYear
    lastInteractionAt
    nextFollowUpAt
    createdAt
    updatedAt
    company {
      id
      name
      website
      industry
      location
    }
  }
`;

const COMPANIES_QUERY = gql`
  ${COMPANY_FIELDS}
  query Companies {
    companies {
      ...CompanyFields
    }
  }
`;

const COMPANY_QUERY = gql`
  ${COMPANY_FIELDS}
  query Company($id: ID!) {
    company(id: $id) {
      ...CompanyFields
      contacts {
        id
        firstName
        lastName
        email
        roleTitle
        status
        relationshipType
        company {
          id
          name
          website
          industry
          location
        }
      }
    }
  }
`;

const CONTACTS_QUERY = gql`
  ${CONTACT_FIELDS}
  query Contacts {
    contacts {
      ...ContactFields
    }
  }
`;

const CONTACT_QUERY = gql`
  ${CONTACT_FIELDS}
  query Contact($id: ID!) {
    contact(id: $id) {
      ...ContactFields
    }
  }
`;

const CREATE_COMPANY_MUTATION = gql`
  ${COMPANY_FIELDS}
  mutation CreateCompany($input: CreateCompanyInput!) {
    createCompany(input: $input) {
      ...CompanyFields
    }
  }
`;

const CREATE_CONTACT_MUTATION = gql`
  ${CONTACT_FIELDS}
  mutation CreateContact($input: CreateContactInput!) {
    createContact(input: $input) {
      ...ContactFields
    }
  }
`;

@Injectable({ providedIn: 'root' })
export class CrmService {
  constructor(private readonly apollo: Apollo) {}

  companies(): Observable<Company[]> {
    return this.apollo
      .watchQuery<{ companies: Company[] }>({
        query: COMPANIES_QUERY,
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(map((result) => (result.data as { companies: Company[] } | undefined)?.companies ?? []));
  }

  company(id: string): Observable<Company | null> {
    return this.apollo
      .watchQuery<{ company: Company | null }>({
        query: COMPANY_QUERY,
        variables: { id },
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(map((result) => (result.data as { company: Company | null } | undefined)?.company ?? null));
  }

  contacts(): Observable<Contact[]> {
    return this.apollo
      .watchQuery<{ contacts: Contact[] }>({
        query: CONTACTS_QUERY,
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(map((result) => (result.data as { contacts: Contact[] } | undefined)?.contacts ?? []));
  }

  contact(id: string): Observable<Contact | null> {
    return this.apollo
      .watchQuery<{ contact: Contact | null }>({
        query: CONTACT_QUERY,
        variables: { id },
        fetchPolicy: 'cache-and-network'
      })
      .valueChanges.pipe(map((result) => (result.data as { contact: Contact | null } | undefined)?.contact ?? null));
  }

  createCompany(input: CreateCompanyInput): Observable<Company> {
    return this.apollo
      .mutate<{ createCompany: Company }>({
        mutation: CREATE_COMPANY_MUTATION,
        variables: { input },
        refetchQueries: [{ query: COMPANIES_QUERY }]
      })
      .pipe(map((result) => result.data?.createCompany as Company));
  }

  createContact(input: CreateContactInput): Observable<Contact> {
    return this.apollo
      .mutate<{ createContact: Contact }>({
        mutation: CREATE_CONTACT_MUTATION,
        variables: { input },
        refetchQueries: [{ query: CONTACTS_QUERY }, { query: COMPANIES_QUERY }]
      })
      .pipe(map((result) => result.data?.createContact as Contact));
  }
}
