export type ContactStatus =
  | 'NEW'
  | 'REACHED_OUT'
  | 'WAITING_FOR_RESPONSE'
  | 'CONVERSATION_SCHEDULED'
  | 'ACTIVE'
  | 'DORMANT'
  | 'PAUSED'
  | 'DO_NOT_CONTACT';

export type RelationshipType =
  | 'FRIEND'
  | 'FORMER_COWORKER'
  | 'ALUMNI'
  | 'RECRUITER'
  | 'MENTOR'
  | 'COMMUNITY'
  | 'PROFESSIONAL'
  | 'OTHER';

export interface Company {
  id: string;
  name: string;
  website: string | null;
  industry: string | null;
  location: string | null;
  notes: string | null;
  createdAt: string | null;
  updatedAt: string | null;
  contacts: Contact[];
}

export interface Contact {
  id: string;
  firstName: string;
  lastName: string | null;
  preferredName: string | null;
  roleTitle: string | null;
  location: string | null;
  linkedinUrl: string | null;
  email: string | null;
  phone: string | null;
  relationshipType: RelationshipType;
  status: ContactStatus;
  source: string | null;
  notes: string | null;
  birthdayMonth: number | null;
  birthdayDay: number | null;
  birthdayYear: number | null;
  lastInteractionAt: string | null;
  nextFollowUpAt: string | null;
  createdAt: string | null;
  updatedAt: string | null;
  company: CompanySummary | null;
}

export interface CompanySummary {
  id: string;
  name: string;
  website: string | null;
  industry: string | null;
  location: string | null;
}

export interface CreateCompanyInput {
  name: string;
  website?: string | null;
  industry?: string | null;
  location?: string | null;
  notes?: string | null;
}

export interface CreateContactInput {
  companyId?: string | null;
  firstName: string;
  lastName?: string | null;
  preferredName?: string | null;
  roleTitle?: string | null;
  location?: string | null;
  linkedinUrl?: string | null;
  email?: string | null;
  phone?: string | null;
  relationshipType: RelationshipType;
  status?: ContactStatus | null;
  source?: string | null;
  notes?: string | null;
  birthdayMonth?: number | null;
  birthdayDay?: number | null;
  birthdayYear?: number | null;
}
