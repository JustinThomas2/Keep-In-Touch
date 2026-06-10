import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CrmService } from '../../core/crm.service';
import type { FollowUp } from '../../core/crm.types';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="page-shell">
      <div class="page-header">
        <div>
          <h1>Dashboard</h1>
          <p>Due and overdue next actions.</p>
        </div>
      </div>

      <ng-container *ngIf="dashboard$ | async as dashboard; else loading">
        <div class="dashboard-grid">
          <section>
            <div class="page-header related-header">
              <div>
                <h2 class="title is-4">Overdue</h2>
                <p>Open follow-ups past their due time.</p>
              </div>
            </div>

            <div class="empty-state" *ngIf="dashboard.overdueFollowUps.length === 0">
              <p class="has-text-weight-semibold">No overdue follow-ups.</p>
            </div>

            <div class="list-grid" *ngIf="dashboard.overdueFollowUps.length > 0">
              <article class="list-row" *ngFor="let followUp of dashboard.overdueFollowUps">
                <div>
                  <a class="row-title" [routerLink]="['/contacts', followUp.contact.id]">{{ contactName(followUp) }}</a>
                  <div class="row-meta">{{ formatDateTime(followUp.dueAt) }}</div>
                  <p class="timeline-summary">{{ followUp.reason || 'No reason recorded' }}</p>
                </div>
                <button class="button is-small is-link" type="button" [disabled]="savingId === followUp.id" (click)="complete(followUp)">
                  {{ savingId === followUp.id ? 'Completing...' : 'Complete' }}
                </button>
              </article>
            </div>
          </section>

          <section>
            <div class="page-header related-header">
              <div>
                <h2 class="title is-4">Due soon</h2>
                <p>Open follow-ups due in the next day.</p>
              </div>
            </div>

            <div class="empty-state" *ngIf="dashboard.dueFollowUps.length === 0">
              <p class="has-text-weight-semibold">No follow-ups due soon.</p>
            </div>

            <div class="list-grid" *ngIf="dashboard.dueFollowUps.length > 0">
              <article class="list-row" *ngFor="let followUp of dashboard.dueFollowUps">
                <div>
                  <a class="row-title" [routerLink]="['/contacts', followUp.contact.id]">{{ contactName(followUp) }}</a>
                  <div class="row-meta">{{ formatDateTime(followUp.dueAt) }}</div>
                  <p class="timeline-summary">{{ followUp.reason || 'No reason recorded' }}</p>
                </div>
                <button class="button is-small is-link" type="button" [disabled]="savingId === followUp.id" (click)="complete(followUp)">
                  {{ savingId === followUp.id ? 'Completing...' : 'Complete' }}
                </button>
              </article>
            </div>
          </section>
        </div>

        <p class="notification is-danger is-light" *ngIf="error">{{ error }}</p>
      </ng-container>

      <ng-template #loading>
        <div class="empty-state">Loading dashboard...</div>
      </ng-template>
    </section>
  `
})
export class DashboardComponent {
  private readonly crm = inject(CrmService);

  error = '';
  savingId: string | null = null;

  readonly dashboard$ = this.crm.dashboard();

  contactName(followUp: FollowUp): string {
    return `${followUp.contact.firstName} ${followUp.contact.lastName ?? ''}`.trim();
  }

  formatDateTime(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return new Intl.DateTimeFormat(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  }

  complete(followUp: FollowUp): void {
    if (this.savingId !== null) {
      return;
    }

    this.error = '';
    this.savingId = followUp.id;
    this.crm.completeFollowUp(followUp.id, followUp.contactId).subscribe({
      next: () => {
        this.savingId = null;
      },
      error: () => {
        this.error = 'Follow-up could not be completed.';
        this.savingId = null;
      }
    });
  }
}
