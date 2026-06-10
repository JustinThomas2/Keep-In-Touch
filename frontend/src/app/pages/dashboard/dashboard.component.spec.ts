import type { ComponentFixture } from '@angular/core/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { CrmService } from '../../core/crm.service';
import type { Dashboard, FollowUp } from '../../core/crm.types';
import { DashboardComponent } from './dashboard.component';

describe('DashboardComponent', () => {
  let fixture: ComponentFixture<DashboardComponent>;

  beforeEach(async () => {
    const followUp: FollowUp = {
      __typename: 'FollowUp',
      id: 'follow-up-1',
      contactId: 'contact-1',
      interactionId: null,
      dueAt: '2026-01-10T15:00:00Z',
      status: 'OPEN',
      reason: 'Send notes',
      completedAt: null,
      createdAt: '2026-01-01T00:00:00Z',
      updatedAt: '2026-01-01T00:00:00Z',
      contact: {
        __typename: 'Contact',
        id: 'contact-1',
        firstName: 'Ada',
        lastName: 'Lovelace',
        email: 'ada@example.test',
        roleTitle: 'Mathematician',
        status: 'ACTIVE',
        relationshipType: 'PROFESSIONAL',
        company: {
          __typename: 'Company',
          id: 'company-1',
          name: 'Analytical Engines',
          website: null,
          industry: null,
          location: null,
        },
      },
    };
    const dashboard: Dashboard = {
      __typename: 'Dashboard',
      dueFollowUps: [followUp],
      overdueFollowUps: [],
      upcomingBirthdays: [],
    };
    const crm: Pick<CrmService, 'dashboard' | 'completeFollowUp'> = {
      dashboard: () => of(dashboard),
      completeFollowUp: () => of(followUp),
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [provideRouter([]), { provide: CrmService, useValue: crm }],
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
  });

  it('renders follow-up data from the dashboard query', () => {
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;

    expect(element.textContent).toContain('Due soon');
    expect(element.textContent).toContain('Ada Lovelace');
    expect(element.textContent).toContain('Send notes');
  });
});
