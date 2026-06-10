import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import type { AbstractControl, ValidationErrors } from '@angular/forms';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { CrmService } from '../../core/crm.service';
import type { ContactStatus, RelationshipType } from '../../core/crm.types';
import { blankToNull, numberOrNull, requiredText } from '../../core/form-utils';

function required(control: AbstractControl): ValidationErrors | null {
  return Validators.required(control);
}

@Component({
  selector: 'app-contact-create',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="page-shell">
      <div class="page-header">
        <div>
          <h1>New contact</h1>
          <p>Add the person and optionally associate them with a company.</p>
        </div>
        <a class="button is-light" routerLink="/contacts">Cancel</a>
      </div>

      <form class="form-panel" [formGroup]="form" (ngSubmit)="submit()">
        <div class="form-grid">
          <div class="field">
            <label class="label" for="firstName">First name</label>
            <div class="control">
              <input
                class="input"
                id="firstName"
                type="text"
                formControlName="firstName"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="lastName">Last name</label>
            <div class="control">
              <input
                class="input"
                id="lastName"
                type="text"
                formControlName="lastName"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="preferredName">Preferred name</label>
            <div class="control">
              <input
                class="input"
                id="preferredName"
                type="text"
                formControlName="preferredName"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="companyId">Company</label>
            <div class="control">
              <div class="select is-fullwidth">
                <select id="companyId" formControlName="companyId">
                  <option value="">No company</option>
                  <option
                    *ngFor="let company of companies$ | async"
                    [value]="company.id"
                  >
                    {{ company.name }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <div class="field">
            <label class="label" for="relationshipType">Relationship</label>
            <div class="control">
              <div class="select is-fullwidth">
                <select
                  id="relationshipType"
                  formControlName="relationshipType"
                >
                  <option *ngFor="let type of relationshipTypes" [value]="type">
                    {{ type }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <div class="field">
            <label class="label" for="status">Status</label>
            <div class="control">
              <div class="select is-fullwidth">
                <select id="status" formControlName="status">
                  <option *ngFor="let status of statuses" [value]="status">
                    {{ status }}
                  </option>
                </select>
              </div>
            </div>
          </div>

          <div class="field">
            <label class="label" for="roleTitle">Role/title</label>
            <div class="control">
              <input
                class="input"
                id="roleTitle"
                type="text"
                formControlName="roleTitle"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="location">Location</label>
            <div class="control">
              <input
                class="input"
                id="location"
                type="text"
                formControlName="location"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="email">Email</label>
            <div class="control">
              <input
                class="input"
                id="email"
                type="email"
                formControlName="email"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="phone">Phone</label>
            <div class="control">
              <input
                class="input"
                id="phone"
                type="tel"
                formControlName="phone"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="linkedinUrl">LinkedIn</label>
            <div class="control">
              <input
                class="input"
                id="linkedinUrl"
                type="url"
                formControlName="linkedinUrl"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="source">Source</label>
            <div class="control">
              <input
                class="input"
                id="source"
                type="text"
                formControlName="source"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="birthdayMonth">Birthday month</label>
            <div class="control">
              <input
                class="input"
                id="birthdayMonth"
                type="number"
                min="1"
                max="12"
                formControlName="birthdayMonth"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="birthdayDay">Birthday day</label>
            <div class="control">
              <input
                class="input"
                id="birthdayDay"
                type="number"
                min="1"
                max="31"
                formControlName="birthdayDay"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="birthdayYear">Birthday year</label>
            <div class="control">
              <input
                class="input"
                id="birthdayYear"
                type="number"
                min="1900"
                formControlName="birthdayYear"
              />
            </div>
            <p class="field-hint">
              Only include a year when month and day are set.
            </p>
          </div>

          <div class="field is-full">
            <label class="label" for="notes">Notes</label>
            <div class="control">
              <textarea
                class="textarea"
                id="notes"
                rows="4"
                formControlName="notes"
              ></textarea>
            </div>
          </div>
        </div>

        <p class="notification is-danger is-light" *ngIf="error">{{ error }}</p>

        <div class="toolbar">
          <button
            class="button is-link"
            type="submit"
            [disabled]="form.invalid || saving"
          >
            {{ saving ? 'Saving...' : 'Create contact' }}
          </button>
          <a class="button is-light" routerLink="/contacts">Cancel</a>
        </div>
      </form>
    </section>
  `,
})
export class ContactCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly crm = inject(CrmService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);

  error = '';
  saving = false;

  readonly companies$ = this.crm.companies();
  readonly relationshipTypes: RelationshipType[] = [
    'PROFESSIONAL',
    'FRIEND',
    'FORMER_COWORKER',
    'ALUMNI',
    'RECRUITER',
    'MENTOR',
    'COMMUNITY',
    'OTHER',
  ];
  readonly statuses: ContactStatus[] = [
    'NEW',
    'REACHED_OUT',
    'WAITING_FOR_RESPONSE',
    'CONVERSATION_SCHEDULED',
    'ACTIVE',
    'DORMANT',
    'PAUSED',
    'DO_NOT_CONTACT',
  ];

  readonly form = this.fb.nonNullable.group({
    companyId: [''],
    firstName: ['', required],
    lastName: [''],
    preferredName: [''],
    roleTitle: [''],
    location: [''],
    linkedinUrl: [''],
    email: [''],
    phone: [''],
    relationshipType: this.fb.nonNullable.control<RelationshipType>(
      'PROFESSIONAL',
      { validators: [required] },
    ),
    status: this.fb.nonNullable.control<ContactStatus>('NEW'),
    source: [''],
    notes: [''],
    birthdayMonth: [''],
    birthdayDay: [''],
    birthdayYear: [''],
  });

  constructor() {
    const companyId = this.route.snapshot.queryParamMap.get('companyId');
    if (companyId !== null && companyId.length > 0) {
      this.form.controls.companyId.setValue(companyId);
    }
  }

  submit(): void {
    if (this.form.invalid || this.saving) {
      return;
    }

    this.error = '';
    this.saving = true;
    const value = this.form.getRawValue();

    this.crm
      .createContact({
        companyId: blankToNull(value.companyId),
        firstName: requiredText(value.firstName),
        lastName: blankToNull(value.lastName),
        preferredName: blankToNull(value.preferredName),
        roleTitle: blankToNull(value.roleTitle),
        location: blankToNull(value.location),
        linkedinUrl: blankToNull(value.linkedinUrl),
        email: blankToNull(value.email),
        phone: blankToNull(value.phone),
        relationshipType: value.relationshipType,
        status: value.status,
        source: blankToNull(value.source),
        notes: blankToNull(value.notes),
        birthdayMonth: numberOrNull(value.birthdayMonth),
        birthdayDay: numberOrNull(value.birthdayDay),
        birthdayYear: numberOrNull(value.birthdayYear),
      })
      .subscribe({
        next: (contact) => void this.router.navigate(['/contacts', contact.id]),
        error: () => {
          this.error =
            'Contact could not be created. Check required fields, email uniqueness, and birthday values.';
          this.saving = false;
        },
      });
  }
}
