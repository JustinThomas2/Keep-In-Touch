import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';

import { CrmService } from '../../core/crm.service';

@Component({
  selector: 'app-contact-list',
  imports: [CommonModule, RouterLink],
  template: `
    <section class="page-shell">
      <div class="page-header">
        <div>
          <h1>Contacts</h1>
          <p>People you want to stay connected with.</p>
        </div>
        <a class="button is-link" routerLink="/contacts/new">New contact</a>
      </div>

      <ng-container *ngIf="contacts$ | async as contacts; else loading">
        <div class="empty-state" *ngIf="contacts.length === 0">
          <p class="has-text-weight-semibold">No contacts yet.</p>
          <p class="row-meta">Create your first contact to start the relationship workflow.</p>
        </div>

        <div class="list-grid" *ngIf="contacts.length > 0">
          <a class="list-row" *ngFor="let contact of contacts" [routerLink]="['/contacts', contact.id]">
            <div>
              <div class="row-title">{{ contact.firstName }} {{ contact.lastName || '' }}</div>
              <div class="row-meta">
                {{ contact.company?.name || 'No company' }}
                <span *ngIf="contact.roleTitle"> · {{ contact.roleTitle }}</span>
                <span *ngIf="contact.email"> · {{ contact.email }}</span>
              </div>
            </div>
            <span class="status-pill">{{ contact.status }}</span>
          </a>
        </div>
      </ng-container>

      <ng-template #loading>
        <div class="empty-state">Loading contacts...</div>
      </ng-template>
    </section>
  `
})
export class ContactListComponent {
  private readonly crm = inject(CrmService);

  readonly contacts$ = this.crm.contacts();
}
