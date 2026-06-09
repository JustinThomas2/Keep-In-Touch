import type {
  CompanyDetailFieldsFragment,
  CompanyFieldsFragment,
  ContactFieldsFragment,
  ContactStatus,
  CreateCompanyInput,
  CreateContactInput,
  RelationshipType
} from './graphql/generated';

export type Company = CompanyFieldsFragment;
export type CompanyDetail = CompanyDetailFieldsFragment;
export type Contact = ContactFieldsFragment;

export type { ContactStatus, CreateCompanyInput, CreateContactInput, RelationshipType };
