import { TypedDocumentNode as DocumentNode } from '@graphql-typed-document-node/core';
export type Maybe<T> = T | null;
export type InputMaybe<T> = T | null;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type MakeEmpty<T extends { [key: string]: unknown }, K extends keyof T> = { [_ in K]?: never };
export type Incremental<T> = T | { [P in keyof T]?: P extends ' $fragmentName' | '__typename' ? T[P] : never };
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: { input: string; output: string; }
  String: { input: string; output: string; }
  Boolean: { input: boolean; output: boolean; }
  Int: { input: number; output: number; }
  Float: { input: number; output: number; }
};

export type Company = {
  __typename?: 'Company';
  contacts: Array<Contact>;
  createdAt: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  industry: Maybe<Scalars['String']['output']>;
  location: Maybe<Scalars['String']['output']>;
  name: Scalars['String']['output'];
  notes: Maybe<Scalars['String']['output']>;
  updatedAt: Maybe<Scalars['String']['output']>;
  website: Maybe<Scalars['String']['output']>;
};

export type Contact = {
  __typename?: 'Contact';
  birthdayDay: Maybe<Scalars['Int']['output']>;
  birthdayMonth: Maybe<Scalars['Int']['output']>;
  birthdayYear: Maybe<Scalars['Int']['output']>;
  company: Maybe<Company>;
  createdAt: Maybe<Scalars['String']['output']>;
  email: Maybe<Scalars['String']['output']>;
  firstName: Scalars['String']['output'];
  id: Scalars['ID']['output'];
  lastInteractionAt: Maybe<Scalars['String']['output']>;
  lastName: Maybe<Scalars['String']['output']>;
  linkedinUrl: Maybe<Scalars['String']['output']>;
  location: Maybe<Scalars['String']['output']>;
  nextFollowUpAt: Maybe<Scalars['String']['output']>;
  notes: Maybe<Scalars['String']['output']>;
  phone: Maybe<Scalars['String']['output']>;
  preferredName: Maybe<Scalars['String']['output']>;
  relationshipType: RelationshipType;
  roleTitle: Maybe<Scalars['String']['output']>;
  source: Maybe<Scalars['String']['output']>;
  status: ContactStatus;
  updatedAt: Maybe<Scalars['String']['output']>;
};

export type ContactStatus =
  | 'ACTIVE'
  | 'CONVERSATION_SCHEDULED'
  | 'DORMANT'
  | 'DO_NOT_CONTACT'
  | 'NEW'
  | 'PAUSED'
  | 'REACHED_OUT'
  | 'WAITING_FOR_RESPONSE';

export type CreateCompanyInput = {
  industry: InputMaybe<Scalars['String']['input']>;
  location: InputMaybe<Scalars['String']['input']>;
  name: Scalars['String']['input'];
  notes: InputMaybe<Scalars['String']['input']>;
  website: InputMaybe<Scalars['String']['input']>;
};

export type CreateContactInput = {
  birthdayDay: InputMaybe<Scalars['Int']['input']>;
  birthdayMonth: InputMaybe<Scalars['Int']['input']>;
  birthdayYear: InputMaybe<Scalars['Int']['input']>;
  companyId: InputMaybe<Scalars['ID']['input']>;
  email: InputMaybe<Scalars['String']['input']>;
  firstName: Scalars['String']['input'];
  lastName: InputMaybe<Scalars['String']['input']>;
  linkedinUrl: InputMaybe<Scalars['String']['input']>;
  location: InputMaybe<Scalars['String']['input']>;
  notes: InputMaybe<Scalars['String']['input']>;
  phone: InputMaybe<Scalars['String']['input']>;
  preferredName: InputMaybe<Scalars['String']['input']>;
  relationshipType: RelationshipType;
  roleTitle: InputMaybe<Scalars['String']['input']>;
  source: InputMaybe<Scalars['String']['input']>;
  status: InputMaybe<ContactStatus>;
};

export type CreateFollowUpInput = {
  contactId: Scalars['ID']['input'];
  dueAt: Scalars['String']['input'];
  interactionId: InputMaybe<Scalars['ID']['input']>;
  reason: InputMaybe<Scalars['String']['input']>;
};

export type CreateInteractionInput = {
  contactId: Scalars['ID']['input'];
  interactionType: InteractionType;
  occurredAt: Scalars['String']['input'];
  outcome: InputMaybe<Scalars['String']['input']>;
  summary: Scalars['String']['input'];
};

export type Dashboard = {
  __typename?: 'Dashboard';
  dueFollowUps: Array<FollowUp>;
  overdueFollowUps: Array<FollowUp>;
};

export type FollowUp = {
  __typename?: 'FollowUp';
  completedAt: Maybe<Scalars['String']['output']>;
  contact: Contact;
  contactId: Scalars['ID']['output'];
  createdAt: Maybe<Scalars['String']['output']>;
  dueAt: Scalars['String']['output'];
  id: Scalars['ID']['output'];
  interactionId: Maybe<Scalars['ID']['output']>;
  reason: Maybe<Scalars['String']['output']>;
  status: FollowUpStatus;
  updatedAt: Maybe<Scalars['String']['output']>;
};

export type FollowUpStatus =
  | 'CANCELLED'
  | 'COMPLETED'
  | 'OPEN';

export type Interaction = {
  __typename?: 'Interaction';
  contactId: Scalars['ID']['output'];
  createdAt: Maybe<Scalars['String']['output']>;
  id: Scalars['ID']['output'];
  interactionType: InteractionType;
  occurredAt: Scalars['String']['output'];
  outcome: Maybe<Scalars['String']['output']>;
  summary: Scalars['String']['output'];
  updatedAt: Maybe<Scalars['String']['output']>;
};

export type InteractionType =
  | 'APPLICATION_REFERRAL'
  | 'COFFEE_CHAT'
  | 'EMAIL'
  | 'IN_PERSON'
  | 'LINKEDIN_MESSAGE'
  | 'OTHER'
  | 'PHONE_CALL'
  | 'SLACK';

export type Mutation = {
  __typename?: 'Mutation';
  cancelFollowUp: FollowUp;
  completeFollowUp: FollowUp;
  createCompany: Company;
  createContact: Contact;
  createFollowUp: FollowUp;
  createInteraction: Interaction;
  updateCompany: Company;
  updateContact: Contact;
  updateInteraction: Interaction;
};


export type MutationCancelFollowUpArgs = {
  id: Scalars['ID']['input'];
};


export type MutationCompleteFollowUpArgs = {
  id: Scalars['ID']['input'];
};


export type MutationCreateCompanyArgs = {
  input: CreateCompanyInput;
};


export type MutationCreateContactArgs = {
  input: CreateContactInput;
};


export type MutationCreateFollowUpArgs = {
  input: CreateFollowUpInput;
};


export type MutationCreateInteractionArgs = {
  input: CreateInteractionInput;
};


export type MutationUpdateCompanyArgs = {
  input: UpdateCompanyInput;
};


export type MutationUpdateContactArgs = {
  input: UpdateContactInput;
};


export type MutationUpdateInteractionArgs = {
  input: UpdateInteractionInput;
};

export type Query = {
  __typename?: 'Query';
  companies: Array<Company>;
  company: Maybe<Company>;
  contact: Maybe<Contact>;
  contactInteractions: Array<Interaction>;
  contacts: Array<Contact>;
  dashboard: Dashboard;
  health: Scalars['String']['output'];
  openFollowUp: Maybe<FollowUp>;
};


export type QueryCompanyArgs = {
  id: Scalars['ID']['input'];
};


export type QueryContactArgs = {
  id: Scalars['ID']['input'];
};


export type QueryContactInteractionsArgs = {
  contactId: Scalars['ID']['input'];
};


export type QueryOpenFollowUpArgs = {
  contactId: Scalars['ID']['input'];
};

export type RelationshipType =
  | 'ALUMNI'
  | 'COMMUNITY'
  | 'FORMER_COWORKER'
  | 'FRIEND'
  | 'MENTOR'
  | 'OTHER'
  | 'PROFESSIONAL'
  | 'RECRUITER';

export type UpdateCompanyInput = {
  id: Scalars['ID']['input'];
  industry: InputMaybe<Scalars['String']['input']>;
  location: InputMaybe<Scalars['String']['input']>;
  name: Scalars['String']['input'];
  notes: InputMaybe<Scalars['String']['input']>;
  website: InputMaybe<Scalars['String']['input']>;
};

export type UpdateContactInput = {
  birthdayDay: InputMaybe<Scalars['Int']['input']>;
  birthdayMonth: InputMaybe<Scalars['Int']['input']>;
  birthdayYear: InputMaybe<Scalars['Int']['input']>;
  companyId: InputMaybe<Scalars['ID']['input']>;
  email: InputMaybe<Scalars['String']['input']>;
  firstName: Scalars['String']['input'];
  id: Scalars['ID']['input'];
  lastName: InputMaybe<Scalars['String']['input']>;
  linkedinUrl: InputMaybe<Scalars['String']['input']>;
  location: InputMaybe<Scalars['String']['input']>;
  notes: InputMaybe<Scalars['String']['input']>;
  phone: InputMaybe<Scalars['String']['input']>;
  preferredName: InputMaybe<Scalars['String']['input']>;
  relationshipType: RelationshipType;
  roleTitle: InputMaybe<Scalars['String']['input']>;
  source: InputMaybe<Scalars['String']['input']>;
  status: InputMaybe<ContactStatus>;
};

export type UpdateInteractionInput = {
  id: Scalars['ID']['input'];
  interactionType: InteractionType;
  occurredAt: Scalars['String']['input'];
  outcome: InputMaybe<Scalars['String']['input']>;
  summary: Scalars['String']['input'];
};

export type CompanyFieldsFragment = (
  { __typename?: 'Company' }
  & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location' | 'notes' | 'createdAt' | 'updatedAt'>
);

export type CompanyDetailFieldsFragment = (
  { __typename?: 'Company' }
  & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location' | 'notes' | 'createdAt' | 'updatedAt'>
  & { contacts: Array<(
    { __typename?: 'Contact' }
    & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
  )> }
);

export type ContactFieldsFragment = (
  { __typename?: 'Contact' }
  & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'preferredName' | 'roleTitle' | 'location' | 'linkedinUrl' | 'email' | 'phone' | 'relationshipType' | 'status' | 'source' | 'notes' | 'birthdayMonth' | 'birthdayDay' | 'birthdayYear' | 'lastInteractionAt' | 'nextFollowUpAt' | 'createdAt' | 'updatedAt'>
  & { company: Maybe<(
    { __typename?: 'Company' }
    & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
  )> }
);

export type InteractionFieldsFragment = (
  { __typename?: 'Interaction' }
  & Pick<Interaction, 'id' | 'contactId' | 'interactionType' | 'occurredAt' | 'summary' | 'outcome' | 'createdAt' | 'updatedAt'>
);

export type FollowUpFieldsFragment = (
  { __typename?: 'FollowUp' }
  & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
  & { contact: (
    { __typename?: 'Contact' }
    & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
    & { company: Maybe<(
      { __typename?: 'Company' }
      & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
    )> }
  ) }
);

export type CompaniesQueryVariables = Exact<{ [key: string]: never; }>;


export type CompaniesQuery = (
  { __typename?: 'Query' }
  & { companies: Array<(
    { __typename?: 'Company' }
    & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location' | 'notes' | 'createdAt' | 'updatedAt'>
  )> }
);

export type CompanyQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type CompanyQuery = (
  { __typename?: 'Query' }
  & { company: Maybe<(
    { __typename?: 'Company' }
    & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location' | 'notes' | 'createdAt' | 'updatedAt'>
    & { contacts: Array<(
      { __typename?: 'Contact' }
      & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
    )> }
  )> }
);

export type ContactsQueryVariables = Exact<{ [key: string]: never; }>;


export type ContactsQuery = (
  { __typename?: 'Query' }
  & { contacts: Array<(
    { __typename?: 'Contact' }
    & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'preferredName' | 'roleTitle' | 'location' | 'linkedinUrl' | 'email' | 'phone' | 'relationshipType' | 'status' | 'source' | 'notes' | 'birthdayMonth' | 'birthdayDay' | 'birthdayYear' | 'lastInteractionAt' | 'nextFollowUpAt' | 'createdAt' | 'updatedAt'>
    & { company: Maybe<(
      { __typename?: 'Company' }
      & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
    )> }
  )> }
);

export type ContactQueryVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type ContactQuery = (
  { __typename?: 'Query' }
  & { contact: Maybe<(
    { __typename?: 'Contact' }
    & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'preferredName' | 'roleTitle' | 'location' | 'linkedinUrl' | 'email' | 'phone' | 'relationshipType' | 'status' | 'source' | 'notes' | 'birthdayMonth' | 'birthdayDay' | 'birthdayYear' | 'lastInteractionAt' | 'nextFollowUpAt' | 'createdAt' | 'updatedAt'>
    & { company: Maybe<(
      { __typename?: 'Company' }
      & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
    )> }
  )> }
);

export type ContactInteractionsQueryVariables = Exact<{
  contactId: Scalars['ID']['input'];
}>;


export type ContactInteractionsQuery = (
  { __typename?: 'Query' }
  & { contactInteractions: Array<(
    { __typename?: 'Interaction' }
    & Pick<Interaction, 'id' | 'contactId' | 'interactionType' | 'occurredAt' | 'summary' | 'outcome' | 'createdAt' | 'updatedAt'>
  )> }
);

export type OpenFollowUpQueryVariables = Exact<{
  contactId: Scalars['ID']['input'];
}>;


export type OpenFollowUpQuery = (
  { __typename?: 'Query' }
  & { openFollowUp: Maybe<(
    { __typename?: 'FollowUp' }
    & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
    & { contact: (
      { __typename?: 'Contact' }
      & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
      & { company: Maybe<(
        { __typename?: 'Company' }
        & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
      )> }
    ) }
  )> }
);

export type DashboardQueryVariables = Exact<{ [key: string]: never; }>;


export type DashboardQuery = (
  { __typename?: 'Query' }
  & { dashboard: (
    { __typename?: 'Dashboard' }
    & { dueFollowUps: Array<(
      { __typename?: 'FollowUp' }
      & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
      & { contact: (
        { __typename?: 'Contact' }
        & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
        & { company: Maybe<(
          { __typename?: 'Company' }
          & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
        )> }
      ) }
    )>, overdueFollowUps: Array<(
      { __typename?: 'FollowUp' }
      & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
      & { contact: (
        { __typename?: 'Contact' }
        & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
        & { company: Maybe<(
          { __typename?: 'Company' }
          & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
        )> }
      ) }
    )> }
  ) }
);

export type CreateCompanyMutationVariables = Exact<{
  input: CreateCompanyInput;
}>;


export type CreateCompanyMutation = (
  { __typename?: 'Mutation' }
  & { createCompany: (
    { __typename?: 'Company' }
    & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location' | 'notes' | 'createdAt' | 'updatedAt'>
  ) }
);

export type CreateContactMutationVariables = Exact<{
  input: CreateContactInput;
}>;


export type CreateContactMutation = (
  { __typename?: 'Mutation' }
  & { createContact: (
    { __typename?: 'Contact' }
    & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'preferredName' | 'roleTitle' | 'location' | 'linkedinUrl' | 'email' | 'phone' | 'relationshipType' | 'status' | 'source' | 'notes' | 'birthdayMonth' | 'birthdayDay' | 'birthdayYear' | 'lastInteractionAt' | 'nextFollowUpAt' | 'createdAt' | 'updatedAt'>
    & { company: Maybe<(
      { __typename?: 'Company' }
      & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
    )> }
  ) }
);

export type CreateInteractionMutationVariables = Exact<{
  input: CreateInteractionInput;
}>;


export type CreateInteractionMutation = (
  { __typename?: 'Mutation' }
  & { createInteraction: (
    { __typename?: 'Interaction' }
    & Pick<Interaction, 'id' | 'contactId' | 'interactionType' | 'occurredAt' | 'summary' | 'outcome' | 'createdAt' | 'updatedAt'>
  ) }
);

export type UpdateInteractionMutationVariables = Exact<{
  input: UpdateInteractionInput;
}>;


export type UpdateInteractionMutation = (
  { __typename?: 'Mutation' }
  & { updateInteraction: (
    { __typename?: 'Interaction' }
    & Pick<Interaction, 'id' | 'contactId' | 'interactionType' | 'occurredAt' | 'summary' | 'outcome' | 'createdAt' | 'updatedAt'>
  ) }
);

export type CreateFollowUpMutationVariables = Exact<{
  input: CreateFollowUpInput;
}>;


export type CreateFollowUpMutation = (
  { __typename?: 'Mutation' }
  & { createFollowUp: (
    { __typename?: 'FollowUp' }
    & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
    & { contact: (
      { __typename?: 'Contact' }
      & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
      & { company: Maybe<(
        { __typename?: 'Company' }
        & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
      )> }
    ) }
  ) }
);

export type CompleteFollowUpMutationVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type CompleteFollowUpMutation = (
  { __typename?: 'Mutation' }
  & { completeFollowUp: (
    { __typename?: 'FollowUp' }
    & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
    & { contact: (
      { __typename?: 'Contact' }
      & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
      & { company: Maybe<(
        { __typename?: 'Company' }
        & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
      )> }
    ) }
  ) }
);

export type CancelFollowUpMutationVariables = Exact<{
  id: Scalars['ID']['input'];
}>;


export type CancelFollowUpMutation = (
  { __typename?: 'Mutation' }
  & { cancelFollowUp: (
    { __typename?: 'FollowUp' }
    & Pick<FollowUp, 'id' | 'contactId' | 'interactionId' | 'dueAt' | 'status' | 'reason' | 'completedAt' | 'createdAt' | 'updatedAt'>
    & { contact: (
      { __typename?: 'Contact' }
      & Pick<Contact, 'id' | 'firstName' | 'lastName' | 'email' | 'roleTitle' | 'status' | 'relationshipType'>
      & { company: Maybe<(
        { __typename?: 'Company' }
        & Pick<Company, 'id' | 'name' | 'website' | 'industry' | 'location'>
      )> }
    ) }
  ) }
);

export const CompanyFieldsFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<CompanyFieldsFragment, unknown>;
export const CompanyDetailFieldsFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyDetailFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"CompanyFields"}},{"kind":"Field","name":{"kind":"Name","value":"contacts"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<CompanyDetailFieldsFragment, unknown>;
export const ContactFieldsFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"ContactFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Contact"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"preferredName"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"linkedinUrl"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"phone"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"source"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayMonth"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayDay"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayYear"}},{"kind":"Field","name":{"kind":"Name","value":"lastInteractionAt"}},{"kind":"Field","name":{"kind":"Name","value":"nextFollowUpAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]} as unknown as DocumentNode<ContactFieldsFragment, unknown>;
export const InteractionFieldsFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"InteractionFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Interaction"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionType"}},{"kind":"Field","name":{"kind":"Name","value":"occurredAt"}},{"kind":"Field","name":{"kind":"Name","value":"summary"}},{"kind":"Field","name":{"kind":"Name","value":"outcome"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<InteractionFieldsFragment, unknown>;
export const FollowUpFieldsFragmentDoc = {"kind":"Document","definitions":[{"kind":"FragmentDefinition","name":{"kind":"Name","value":"FollowUpFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"FollowUp"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionId"}},{"kind":"Field","name":{"kind":"Name","value":"dueAt"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"reason"}},{"kind":"Field","name":{"kind":"Name","value":"completedAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"contact"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]}}]} as unknown as DocumentNode<FollowUpFieldsFragment, unknown>;
export const CompaniesDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"Companies"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"companies"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"CompanyFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<CompaniesQuery, CompaniesQueryVariables>;
export const CompanyDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"Company"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"id"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"ID"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"company"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"id"},"value":{"kind":"Variable","name":{"kind":"Name","value":"id"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"CompanyDetailFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyDetailFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"CompanyFields"}},{"kind":"Field","name":{"kind":"Name","value":"contacts"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}}]}}]}}]} as unknown as DocumentNode<CompanyQuery, CompanyQueryVariables>;
export const ContactsDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"Contacts"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"contacts"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"ContactFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"ContactFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Contact"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"preferredName"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"linkedinUrl"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"phone"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"source"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayMonth"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayDay"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayYear"}},{"kind":"Field","name":{"kind":"Name","value":"lastInteractionAt"}},{"kind":"Field","name":{"kind":"Name","value":"nextFollowUpAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]} as unknown as DocumentNode<ContactsQuery, ContactsQueryVariables>;
export const ContactDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"Contact"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"id"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"ID"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"contact"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"id"},"value":{"kind":"Variable","name":{"kind":"Name","value":"id"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"ContactFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"ContactFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Contact"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"preferredName"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"linkedinUrl"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"phone"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"source"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayMonth"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayDay"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayYear"}},{"kind":"Field","name":{"kind":"Name","value":"lastInteractionAt"}},{"kind":"Field","name":{"kind":"Name","value":"nextFollowUpAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]} as unknown as DocumentNode<ContactQuery, ContactQueryVariables>;
export const ContactInteractionsDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"ContactInteractions"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"contactId"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"ID"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"contactInteractions"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"contactId"},"value":{"kind":"Variable","name":{"kind":"Name","value":"contactId"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"InteractionFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"InteractionFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Interaction"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionType"}},{"kind":"Field","name":{"kind":"Name","value":"occurredAt"}},{"kind":"Field","name":{"kind":"Name","value":"summary"}},{"kind":"Field","name":{"kind":"Name","value":"outcome"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<ContactInteractionsQuery, ContactInteractionsQueryVariables>;
export const OpenFollowUpDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"OpenFollowUp"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"contactId"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"ID"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"openFollowUp"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"contactId"},"value":{"kind":"Variable","name":{"kind":"Name","value":"contactId"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"FollowUpFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"FollowUpFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"FollowUp"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionId"}},{"kind":"Field","name":{"kind":"Name","value":"dueAt"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"reason"}},{"kind":"Field","name":{"kind":"Name","value":"completedAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"contact"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]}}]} as unknown as DocumentNode<OpenFollowUpQuery, OpenFollowUpQueryVariables>;
export const DashboardDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"query","name":{"kind":"Name","value":"Dashboard"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"dashboard"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"dueFollowUps"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"FollowUpFields"}}]}},{"kind":"Field","name":{"kind":"Name","value":"overdueFollowUps"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"FollowUpFields"}}]}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"FollowUpFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"FollowUp"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionId"}},{"kind":"Field","name":{"kind":"Name","value":"dueAt"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"reason"}},{"kind":"Field","name":{"kind":"Name","value":"completedAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"contact"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]}}]} as unknown as DocumentNode<DashboardQuery, DashboardQueryVariables>;
export const CreateCompanyDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateCompany"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"CreateCompanyInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createCompany"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"CompanyFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"CompanyFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Company"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<CreateCompanyMutation, CreateCompanyMutationVariables>;
export const CreateContactDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateContact"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"CreateContactInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createContact"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"ContactFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"ContactFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Contact"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"preferredName"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"location"}},{"kind":"Field","name":{"kind":"Name","value":"linkedinUrl"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"phone"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"source"}},{"kind":"Field","name":{"kind":"Name","value":"notes"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayMonth"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayDay"}},{"kind":"Field","name":{"kind":"Name","value":"birthdayYear"}},{"kind":"Field","name":{"kind":"Name","value":"lastInteractionAt"}},{"kind":"Field","name":{"kind":"Name","value":"nextFollowUpAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]} as unknown as DocumentNode<CreateContactMutation, CreateContactMutationVariables>;
export const CreateInteractionDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateInteraction"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"CreateInteractionInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createInteraction"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"InteractionFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"InteractionFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Interaction"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionType"}},{"kind":"Field","name":{"kind":"Name","value":"occurredAt"}},{"kind":"Field","name":{"kind":"Name","value":"summary"}},{"kind":"Field","name":{"kind":"Name","value":"outcome"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<CreateInteractionMutation, CreateInteractionMutationVariables>;
export const UpdateInteractionDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"UpdateInteraction"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"UpdateInteractionInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"updateInteraction"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"InteractionFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"InteractionFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"Interaction"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionType"}},{"kind":"Field","name":{"kind":"Name","value":"occurredAt"}},{"kind":"Field","name":{"kind":"Name","value":"summary"}},{"kind":"Field","name":{"kind":"Name","value":"outcome"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}}]}}]} as unknown as DocumentNode<UpdateInteractionMutation, UpdateInteractionMutationVariables>;
export const CreateFollowUpDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CreateFollowUp"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"input"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"CreateFollowUpInput"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"createFollowUp"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"input"},"value":{"kind":"Variable","name":{"kind":"Name","value":"input"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"FollowUpFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"FollowUpFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"FollowUp"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionId"}},{"kind":"Field","name":{"kind":"Name","value":"dueAt"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"reason"}},{"kind":"Field","name":{"kind":"Name","value":"completedAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"contact"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]}}]} as unknown as DocumentNode<CreateFollowUpMutation, CreateFollowUpMutationVariables>;
export const CompleteFollowUpDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CompleteFollowUp"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"id"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"ID"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"completeFollowUp"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"id"},"value":{"kind":"Variable","name":{"kind":"Name","value":"id"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"FollowUpFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"FollowUpFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"FollowUp"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionId"}},{"kind":"Field","name":{"kind":"Name","value":"dueAt"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"reason"}},{"kind":"Field","name":{"kind":"Name","value":"completedAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"contact"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]}}]} as unknown as DocumentNode<CompleteFollowUpMutation, CompleteFollowUpMutationVariables>;
export const CancelFollowUpDocument = {"kind":"Document","definitions":[{"kind":"OperationDefinition","operation":"mutation","name":{"kind":"Name","value":"CancelFollowUp"},"variableDefinitions":[{"kind":"VariableDefinition","variable":{"kind":"Variable","name":{"kind":"Name","value":"id"}},"type":{"kind":"NonNullType","type":{"kind":"NamedType","name":{"kind":"Name","value":"ID"}}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"cancelFollowUp"},"arguments":[{"kind":"Argument","name":{"kind":"Name","value":"id"},"value":{"kind":"Variable","name":{"kind":"Name","value":"id"}}}],"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"FragmentSpread","name":{"kind":"Name","value":"FollowUpFields"}}]}}]}},{"kind":"FragmentDefinition","name":{"kind":"Name","value":"FollowUpFields"},"typeCondition":{"kind":"NamedType","name":{"kind":"Name","value":"FollowUp"}},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"contactId"}},{"kind":"Field","name":{"kind":"Name","value":"interactionId"}},{"kind":"Field","name":{"kind":"Name","value":"dueAt"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"reason"}},{"kind":"Field","name":{"kind":"Name","value":"completedAt"}},{"kind":"Field","name":{"kind":"Name","value":"createdAt"}},{"kind":"Field","name":{"kind":"Name","value":"updatedAt"}},{"kind":"Field","name":{"kind":"Name","value":"contact"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"firstName"}},{"kind":"Field","name":{"kind":"Name","value":"lastName"}},{"kind":"Field","name":{"kind":"Name","value":"email"}},{"kind":"Field","name":{"kind":"Name","value":"roleTitle"}},{"kind":"Field","name":{"kind":"Name","value":"status"}},{"kind":"Field","name":{"kind":"Name","value":"relationshipType"}},{"kind":"Field","name":{"kind":"Name","value":"company"},"selectionSet":{"kind":"SelectionSet","selections":[{"kind":"Field","name":{"kind":"Name","value":"id"}},{"kind":"Field","name":{"kind":"Name","value":"name"}},{"kind":"Field","name":{"kind":"Name","value":"website"}},{"kind":"Field","name":{"kind":"Name","value":"industry"}},{"kind":"Field","name":{"kind":"Name","value":"location"}}]}}]}}]}}]} as unknown as DocumentNode<CancelFollowUpMutation, CancelFollowUpMutationVariables>;