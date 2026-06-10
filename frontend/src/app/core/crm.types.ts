import type {
  CompanyDetailFieldsFragment,
  CompanyFieldsFragment,
  ContactFieldsFragment,
  ContactStatus,
  CreateCompanyInput,
  CreateContactInput,
  CreateFollowUpInput,
  CreateInteractionInput,
  DashboardQuery,
  FollowUpFieldsFragment,
  FollowUpStatus,
  InteractionFieldsFragment,
  InteractionType,
  RelationshipType,
  UpdateInteractionInput,
} from './graphql/generated';

export type Company = CompanyFieldsFragment;
export type CompanyDetail = CompanyDetailFieldsFragment;
export type Contact = ContactFieldsFragment;
export type Interaction = InteractionFieldsFragment;
export type FollowUp = FollowUpFieldsFragment;
export type Dashboard = DashboardQuery['dashboard'];

export type {
  ContactStatus,
  CreateCompanyInput,
  CreateContactInput,
  CreateFollowUpInput,
  CreateInteractionInput,
  FollowUpStatus,
  InteractionType,
  RelationshipType,
  UpdateInteractionInput,
};
