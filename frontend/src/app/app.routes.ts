import { Routes } from '@angular/router';
import { CompanyCreateComponent } from './pages/companies/company-create.component';
import { CompanyDetailComponent } from './pages/companies/company-detail.component';
import { CompanyListComponent } from './pages/companies/company-list.component';
import { ContactCreateComponent } from './pages/contacts/contact-create.component';
import { ContactDetailComponent } from './pages/contacts/contact-detail.component';
import { ContactListComponent } from './pages/contacts/contact-list.component';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'contacts' },
  { path: 'contacts', component: ContactListComponent },
  { path: 'contacts/new', component: ContactCreateComponent },
  { path: 'contacts/:id', component: ContactDetailComponent },
  { path: 'companies', component: CompanyListComponent },
  { path: 'companies/new', component: CompanyCreateComponent },
  { path: 'companies/:id', component: CompanyDetailComponent },
  { path: '**', redirectTo: 'contacts' }
];
