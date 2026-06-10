import type {
  CompanyDetailFieldsFragment,
  CompanyFieldsFragment,
  ContactFieldsFragment,
  ContactStatus,
  CreateCompanyInput,
  CreateContactInput,
  CreateInteractionInput,
  InteractionFieldsFragment,
  InteractionType,
  RelationshipType,
  UpdateInteractionInput
} from './graphql/generated';

export type Company = CompanyFieldsFragment;
export type CompanyDetail = CompanyDetailFieldsFragment;
export type Contact = ContactFieldsFragment;
export type Interaction = InteractionFieldsFragment;

export type {
  ContactStatus,
  CreateCompanyInput,
  CreateContactInput,
  CreateInteractionInput,
  InteractionType,
  RelationshipType,
  UpdateInteractionInput
};
