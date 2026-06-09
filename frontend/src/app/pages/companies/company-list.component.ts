import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CrmService } from '../../core/crm.service';

@Component({
  selector: 'app-company-list',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="page-shell">
      <div class="page-header">
        <div>
          <h1>Companies</h1>
          <p>Organizations connected to your relationship pipeline.</p>
        </div>
        <a class="button is-link" routerLink="/companies/new">New company</a>
      </div>

      <ng-container *ngIf="companies$ | async as companies; else loading">
        <div class="empty-state" *ngIf="companies.length === 0">
          <p class="has-text-weight-semibold">No companies yet.</p>
          <p class="row-meta">Create a company before associating contacts with it.</p>
        </div>

        <div class="list-grid" *ngIf="companies.length > 0">
          <a class="list-row" *ngFor="let company of companies" [routerLink]="['/companies', company.id]">
            <div>
              <div class="row-title">{{ company.name }}</div>
              <div class="row-meta">
                {{ company.industry || 'No industry' }}
                <span *ngIf="company.location"> · {{ company.location }}</span>
              </div>
            </div>
            <span class="button is-small is-light">Open</span>
          </a>
        </div>
      </ng-container>

      <ng-template #loading>
        <div class="empty-state">Loading companies...</div>
      </ng-template>
    </section>
  `
})
export class CompanyListComponent {
  private readonly crm = inject(CrmService);

  readonly companies$ = this.crm.companies();
}
