import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import type { AbstractControl, ValidationErrors } from '@angular/forms';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, shareReplay, switchMap } from 'rxjs';

import { CrmService } from '../../core/crm.service';
import type { Interaction, InteractionType } from '../../core/crm.types';
import { blankToNull, requiredText } from '../../core/form-utils';

function required(control: AbstractControl): ValidationErrors | null {
  return Validators.required(control);
}

@Component({
  selector: 'app-contact-detail',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './contact-detail.component.html'
})
export class ContactDetailComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly crm = inject(CrmService);
  private readonly fb = inject(FormBuilder);

  interactionError = '';
  savingInteraction = false;
  editingInteractionId: string | null = null;

  readonly interactionTypes: InteractionType[] = [
    'LINKEDIN_MESSAGE',
    'EMAIL',
    'COFFEE_CHAT',
    'PHONE_CALL',
    'SLACK',
    'IN_PERSON',
    'APPLICATION_REFERRAL',
    'OTHER'
  ];

  readonly interactionForm = this.fb.nonNullable.group({
    interactionType: this.fb.nonNullable.control<InteractionType>('EMAIL', { validators: [required] }),
    occurredAt: [this.toDateTimeLocal(new Date()), required],
    summary: ['', required],
    outcome: ['']
  });

  private readonly contactId$ = this.route.paramMap.pipe(
    map((params) => params.get('id') ?? ''),
    shareReplay({ bufferSize: 1, refCount: true })
  );

  readonly contact$ = this.contactId$.pipe(
    switchMap((id) => this.crm.contact(id))
  );

  readonly interactions$ = this.contactId$.pipe(
    switchMap((contactId) => this.crm.contactInteractions(contactId))
  );

  birthday(month: number | null, day: number | null, year: number | null): string {
    if (month === null || day === null) {
      return 'Not set';
    }

    const monthDay = `${String(month)}/${String(day)}`;
    return year === null ? monthDay : `${monthDay}/${String(year)}`;
  }

  formatDateTime(value: string | null): string {
    if (value === null) {
      return 'Not set';
    }

    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      return value;
    }

    return new Intl.DateTimeFormat(undefined, {
      dateStyle: 'medium',
      timeStyle: 'short'
    }).format(date);
  }

  editInteraction(interaction: Interaction): void {
    this.editingInteractionId = interaction.id;
    this.interactionError = '';
    this.interactionForm.setValue({
      interactionType: interaction.interactionType,
      occurredAt: this.toDateTimeLocal(new Date(interaction.occurredAt)),
      summary: interaction.summary,
      outcome: interaction.outcome ?? ''
    });
  }

  cancelEdit(): void {
    this.editingInteractionId = null;
    this.interactionError = '';
    this.resetInteractionForm();
  }

  submitInteraction(contactId: string): void {
    if (this.interactionForm.invalid || this.savingInteraction) {
      return;
    }

    this.interactionError = '';
    this.savingInteraction = true;
    const value = this.interactionForm.getRawValue();
    let input: {
      interactionType: InteractionType;
      occurredAt: string;
      summary: string;
      outcome: string | null;
    };
    try {
      input = this.toInteractionInput(value);
    }
    catch {
      this.interactionError = 'Interaction could not be saved. Check required fields and date/time.';
      this.savingInteraction = false;
      return;
    }
    const request = this.editingInteractionId === null
      ? this.crm.createInteraction({ contactId, ...input })
      : this.crm.updateInteraction({ id: this.editingInteractionId, ...input }, contactId);

    request.subscribe({
      next: () => {
        this.savingInteraction = false;
        this.editingInteractionId = null;
        this.resetInteractionForm();
      },
      error: () => {
        this.interactionError = 'Interaction could not be saved. Check required fields and date/time.';
        this.savingInteraction = false;
      }
    });
  }

  private resetInteractionForm(): void {
    this.interactionForm.setValue({
      interactionType: 'EMAIL',
      occurredAt: this.toDateTimeLocal(new Date()),
      summary: '',
      outcome: ''
    });
  }

  private toDateTimeLocal(date: Date): string {
    if (Number.isNaN(date.getTime())) {
      return '';
    }

    const localTime = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    return localTime.toISOString().slice(0, 16);
  }

  private toIsoDateTime(value: string): string {
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) {
      throw new Error('Interaction date/time is invalid.');
    }

    return date.toISOString();
  }

  private toInteractionInput(value: ReturnType<typeof this.interactionForm.getRawValue>): {
    interactionType: InteractionType;
    occurredAt: string;
    summary: string;
    outcome: string | null;
  } {
    return {
      interactionType: value.interactionType,
      occurredAt: this.toIsoDateTime(value.occurredAt),
      summary: requiredText(value.summary),
      outcome: blankToNull(value.outcome)
    };
  }
}
