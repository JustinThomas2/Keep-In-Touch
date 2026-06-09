import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { CrmService } from '../../core/crm.service';

@Component({
  selector: 'app-contact-detail',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="page-shell">
      <ng-container *ngIf="contact$ | async as contact; else loading">
        <div class="page-header" *ngIf="contact; else missing">
          <div>
            <h1>{{ contact.firstName }} {{ contact.lastName || '' }}</h1>
            <p>
              {{ contact.roleTitle || contact.relationshipType }}
              <span *ngIf="contact.company"> at {{ contact.company.name }}</span>
            </p>
          </div>
          <a class="button is-light" routerLink="/contacts">Back to contacts</a>
        </div>

        <div class="detail-panel" *ngIf="contact">
          <div class="detail-grid">
            <div class="detail-item">
              <span class="detail-label">Status</span>
              <span class="status-pill">{{ contact.status }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">Company</span>
              <a class="detail-value" *ngIf="contact.company; else empty" [routerLink]="['/companies', contact.company.id]">
                {{ contact.company.name }}
              </a>
            </div>
            <div class="detail-item">
              <span class="detail-label">Email</span>
              <a class="detail-value" *ngIf="contact.email; else empty" [href]="'mailto:' + contact.email">
                {{ contact.email }}
              </a>
            </div>
            <div class="detail-item">
              <span class="detail-label">Phone</span>
              <div class="detail-value">{{ contact.phone || 'Not set' }}</div>
            </div>
            <div class="detail-item">
              <span class="detail-label">LinkedIn</span>
              <a class="detail-value" *ngIf="contact.linkedinUrl; else empty" [href]="contact.linkedinUrl" target="_blank">
                {{ contact.linkedinUrl }}
              </a>
            </div>
            <div class="detail-item">
              <span class="detail-label">Location</span>
              <div class="detail-value">{{ contact.location || 'Not set' }}</div>
            </div>
            <div class="detail-item">
              <span class="detail-label">Birthday</span>
              <div class="detail-value">{{ birthday(contact.birthdayMonth, contact.birthdayDay, contact.birthdayYear) }}</div>
            </div>
            <div class="detail-item">
              <span class="detail-label">Source</span>
              <div class="detail-value">{{ contact.source || 'Not set' }}</div>
            </div>
            <div class="detail-item is-full">
              <span class="detail-label">Notes</span>
              <div class="detail-value">{{ contact.notes || 'No notes yet' }}</div>
            </div>
          </div>
        </div>
      </ng-container>

      <ng-template #empty>Not set</ng-template>
      <ng-template #missing>
        <div class="empty-state">Contact not found.</div>
      </ng-template>
      <ng-template #loading>
        <div class="empty-state">Loading contact...</div>
      </ng-template>
    </section>
  `
})
export class ContactDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly crm = inject(CrmService);

  readonly contact$ = this.route.paramMap.pipe(
    map((params) => params.get('id') ?? ''),
    switchMap((id) => this.crm.contact(id))
  );

  birthday(month: number | null, day: number | null, year: number | null): string {
    if (!month || !day) {
      return 'Not set';
    }

    return year ? `${month}/${day}/${year}` : `${month}/${day}`;
  }
}
