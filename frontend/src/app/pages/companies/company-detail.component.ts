import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { CrmService } from '../../core/crm.service';

@Component({
  selector: 'app-company-detail',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="page-shell">
      <ng-container *ngIf="company$ | async as company; else loading">
        <div class="page-header" *ngIf="company; else missing">
          <div>
            <h1>{{ company.name }}</h1>
            <p>{{ company.industry || 'No industry recorded' }}</p>
          </div>
          <a
            class="button is-link"
            [routerLink]="['/contacts/new']"
            [queryParams]="{ companyId: company.id }"
          >
            New contact
          </a>
        </div>

        <div class="detail-panel" *ngIf="company">
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Website</span>
              <a
                class="detail-value"
                *ngIf="company.website; else empty"
                [href]="company.website"
                target="_blank"
              >
                {{ company.website }}
              </a>
            </div>
            <div class="detail-item">
              <span class="detail-label">Location</span>
              <div class="detail-value">
                {{ company.location || 'Not set' }}
              </div>
            </div>
            <div class="detail-item is-full">
              <span class="detail-label">Notes</span>
              <div class="detail-value">
                {{ company.notes || 'No notes yet' }}
              </div>
            </div>
          </div>
        </div>

        <div class="page-header related-header" *ngIf="company">
          <div>
            <h2 class="title is-4">Contacts</h2>
            <p>People currently associated with this company.</p>
          </div>
        </div>

        <div
          class="empty-state"
          *ngIf="company && company.contacts.length === 0"
        >
          <p class="has-text-weight-semibold">No contacts associated yet.</p>
        </div>

        <div class="list-grid" *ngIf="company && company.contacts.length > 0">
          <a
            class="list-row"
            *ngFor="let contact of company.contacts"
            [routerLink]="['/contacts', contact.id]"
          >
            <div>
              <div class="row-title">
                {{ contact.firstName }} {{ contact.lastName || '' }}
              </div>
              <div class="row-meta">
                {{ contact.roleTitle || contact.email || 'No role or email' }}
              </div>
            </div>
            <span class="status-pill">{{ contact.status }}</span>
          </a>
        </div>
      </ng-container>

      <ng-template #empty>Not set</ng-template>
      <ng-template #missing>
        <div class="empty-state">Company not found.</div>
      </ng-template>
      <ng-template #loading>
        <div class="empty-state">Loading company...</div>
      </ng-template>
    </section>
  `,
})
export class CompanyDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly crm = inject(CrmService);

  readonly company$ = this.route.paramMap.pipe(
    map((params) => params.get('id') ?? ''),
    switchMap((id) => this.crm.company(id)),
  );
}
