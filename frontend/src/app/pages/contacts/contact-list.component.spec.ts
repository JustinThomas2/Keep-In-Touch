import type { ComponentFixture } from '@angular/core/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { CrmService } from '../../core/crm.service';
import type { Contact } from '../../core/crm.types';
import { ContactListComponent } from './contact-list.component';

describe('ContactListComponent', () => {
  let fixture: ComponentFixture<ContactListComponent>;

  beforeEach(async () => {
    const crm: Pick<CrmService, 'contacts'> = {
      contacts: () =>
        of([
          {
            __typename: 'Contact',
            id: 'contact-1',
            firstName: 'Ada',
            lastName: 'Lovelace',
            preferredName: null,
            roleTitle: 'Mathematician',
            location: null,
            linkedinUrl: null,
            email: 'ada@example.test',
            phone: null,
            relationshipType: 'PROFESSIONAL',
            status: 'ACTIVE',
            source: null,
            notes: null,
            birthdayMonth: null,
            birthdayDay: null,
            birthdayYear: null,
            lastInteractionAt: null,
            nextFollowUpAt: null,
            createdAt: '2026-01-01T00:00:00Z',
            updatedAt: '2026-01-01T00:00:00Z',
            company: {
              __typename: 'Company',
              id: 'company-1',
              name: 'Analytical Engines',
              website: null,
              industry: null,
              location: null,
            },
          } satisfies Contact,
        ]),
    };

    await TestBed.configureTestingModule({
      imports: [ContactListComponent],
      providers: [provideRouter([]), { provide: CrmService, useValue: crm }],
    }).compileComponents();

    fixture = TestBed.createComponent(ContactListComponent);
  });

  it('renders contacts returned by the CRM service', () => {
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Ada Lovelace');
    expect(element.textContent).toContain('Analytical Engines');
    expect(element.textContent).toContain('Mathematician');
    expect(element.textContent).toContain('ada@example.test');
  });
});
