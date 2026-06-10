import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import type { AbstractControl, ValidationErrors } from '@angular/forms';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { CrmService } from '../../core/crm.service';
import { blankToNull, requiredText } from '../../core/form-utils';

function required(control: AbstractControl): ValidationErrors | null {
  return Validators.required(control);
}

@Component({
  selector: 'app-company-create',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="page-shell">
      <div class="page-header">
        <div>
          <h1>New company</h1>
          <p>Add an organization you want to track contacts against.</p>
        </div>
        <a class="button is-light" routerLink="/companies">Cancel</a>
      </div>

      <form class="form-panel" [formGroup]="form" (ngSubmit)="submit()">
        <div class="form-grid">
          <div class="field is-full">
            <label class="label" for="name">Company name</label>
            <div class="control">
              <input
                class="input"
                id="name"
                type="text"
                formControlName="name"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="website">Website</label>
            <div class="control">
              <input
                class="input"
                id="website"
                type="url"
                formControlName="website"
              />
            </div>
          </div>

          <div class="field">
            <label class="label" for="industry">Industry</label>
            <div class="control">
              <input
                class="input"
                id="industry"
                type="text"
                formControlName="industry"
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
            {{ saving ? 'Saving...' : 'Create company' }}
          </button>
          <a class="button is-light" routerLink="/companies">Cancel</a>
        </div>
      </form>
    </section>
  `,
})
export class CompanyCreateComponent {
  private readonly fb = inject(FormBuilder);
  private readonly crm = inject(CrmService);
  private readonly router = inject(Router);

  error = '';
  saving = false;

  readonly form = this.fb.nonNullable.group({
    name: ['', required],
    website: [''],
    industry: [''],
    location: [''],
    notes: [''],
  });

  submit(): void {
    if (this.form.invalid || this.saving) {
      return;
    }

    this.error = '';
    this.saving = true;
    const value = this.form.getRawValue();

    this.crm
      .createCompany({
        name: requiredText(value.name),
        website: blankToNull(value.website),
        industry: blankToNull(value.industry),
        location: blankToNull(value.location),
        notes: blankToNull(value.notes),
      })
      .subscribe({
        next: (company) =>
          void this.router.navigate(['/companies', company.id]),
        error: () => {
          this.error = 'Company could not be created.';
          this.saving = false;
        },
      });
  }
}
