# MVP Review Checklist

Use this checklist for the final portfolio/project review.

## Fresh Clone Setup

- [ ] Create the root `.env` file.
- [ ] Start Postgres with `docker compose up -d`.
- [ ] Start the backend from `backend/` with the `local` profile.
- [ ] Start the frontend from `frontend/` with `npm start`.
- [ ] Open the app at `http://localhost:4200`.

## Core Workflow

- [ ] Create a company.
- [ ] View companies.
- [ ] Open company details.
- [ ] Create a contact.
- [ ] Associate a contact with a company.
- [ ] View contacts.
- [ ] Open contact details.
- [ ] Log an interaction.
- [ ] Edit an interaction.
- [ ] Schedule a follow-up.
- [ ] Complete a follow-up.
- [ ] View due follow-ups on the dashboard.
- [ ] View overdue follow-ups on the dashboard.
- [ ] Add birthday info.
- [ ] View upcoming birthdays on the dashboard.

## Local Verification

- [ ] Run backend formatting checks.
- [ ] Run backend tests.
- [ ] Run frontend formatting checks.
- [ ] Run frontend code generation.
- [ ] Run frontend lint.
- [ ] Run frontend build.

## MVP Boundary

- [ ] Tags are not partially implemented.
- [ ] CSV import/export is not partially implemented.
- [ ] Custom personal dates are not partially implemented.
- [ ] Job application tracking is not partially implemented.
- [ ] Recurring follow-ups are not partially implemented.
- [ ] Notifications/reminders are not partially implemented.
