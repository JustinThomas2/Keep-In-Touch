import type { ComponentFixture } from '@angular/core/testing';
import { TestBed } from '@angular/core/testing';
import {
  ActivatedRoute,
  convertToParamMap,
  provideRouter,
} from '@angular/router';
import { of } from 'rxjs';

import { CrmService } from '../../core/crm.service';
import { ContactCreateComponent } from './contact-create.component';

describe('ContactCreateComponent', () => {
  let fixture: ComponentFixture<ContactCreateComponent>;

  beforeEach(async () => {
    const crm: Pick<
      CrmService,
      'companies' | 'contact' | 'createContact' | 'updateContact'
    > = {
      companies: () => of([]),
      contact: () => of(null),
      createContact: () => {
        throw new Error(
          'createContact should not be called for an invalid form.',
        );
      },
      updateContact: () => {
        throw new Error(
          'updateContact should not be called for an invalid form.',
        );
      },
    };

    await TestBed.configureTestingModule({
      imports: [ContactCreateComponent],
      providers: [
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
              queryParamMap: convertToParamMap({}),
            },
          },
        },
        { provide: CrmService, useValue: crm },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ContactCreateComponent);
  });

  it('requires a first name before submitting', () => {
    fixture.detectChanges();

    const component = fixture.componentInstance;
    component.form.controls.firstName.setValue('');
    component.form.controls.relationshipType.setValue('PROFESSIONAL');
    fixture.detectChanges();

    const element = fixture.nativeElement as HTMLElement;
    const submitButton = element.querySelector<HTMLButtonElement>(
      'button[type="submit"]',
    );

    expect(component.form.invalid).toBeTrue();
    expect(submitButton).not.toBeNull();
    if (submitButton === null) {
      return;
    }
    expect(submitButton.disabled).toBeTrue();
  });
});
