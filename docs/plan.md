# Project Plan

## Project Summary

Keep in Touch is a lightweight personal CRM for remembering people, conversations, and next steps.

The app helps users track relationships without turning their network into a sales pipeline.

The MVP should answer:

- Who do I know?
- Where do they work?
- What have we talked about?
- When should I follow up?
- Who am I letting go cold?
- Whose birthday is coming up?

## Core Product Direction

This app should feel like a personal relationship memory tool, not a sales CRM.

It should prioritize:

- Simplicity
- Clear next actions
- Relationship context
- Conversation history
- Follow-up reminders
- Practical usefulness for job search and personal networking

It should avoid:

- Sales pipeline language
- Relationship scoring
- Over-automation
- Contact scraping
- Heavy AI features in MVP
- Complex team/collaboration features

## Tech Stack

### Frontend

- Angular
- TypeScript
- Apollo Angular
- Bulma
- CSS
- Angular routing
- Standalone components

### Backend

- Java 25
- Spring Boot 4.x
- Spring for GraphQL
- Spring Data JPA
- Maven
- Postgres
- Flyway
- JUnit

### Tooling

- Docker
- GitHub Actions
- GitHub
- Markdown docs

## Project Structure

```text
keep-in-touch/
  backend/
  frontend/
  docs/
    schema.md
    plan.md
  docker-compose.yml
  README.md
```

## Source of Truth Rules

This file should act as the implementation roadmap and LLM handoff document.

When working in a new context window, an LLM or coding agent should read this file first, then `docs/schema.md`, then the README.

The schema in `docs/schema.md` is the source of truth for:

- Tables
- Relationships
- Column decisions
- MVP data model
- Future schema ideas

This file is the source of truth for:

- Build order
- Technical decisions
- Phase sequencing
- Review checkpoints
- What should and should not be built yet

If implementation choices conflict with this file, pause and update the plan before continuing.

Do not create Flyway migrations or JPA entities without first checking `docs/schema.md`.

## Coding Agent Handoff Rules

This project may be implemented with help from a coding agent.

Before making changes, the agent must read:

1. `docs/plan.md`
2. `docs/schema.md`
3. `README.md`

The agent should complete only the phase or task explicitly requested by the user.

The agent should not continue into the next phase without user approval.

After completing a task, the agent should report:

- What changed
- Which files were created or modified
- Which commands were run
- Whether tests/builds passed
- Any decisions or tradeoffs made
- Any follow-up questions

The agent should not silently introduce:

- New dependencies
- New frameworks
- Schema changes
- API style changes
- Product features outside the requested phase
- Auth
- Tags
- AI features
- CSV import/export
- Notifications
- Recurring follow-ups

If the agent finds a conflict between the code, `docs/schema.md`, and `docs/plan.md`, it should stop and ask for direction before continuing.

## Implementation Defaults

Use these defaults unless the user explicitly changes them.

### Backend Defaults

- Java 25
- Spring Boot 4.x
- Maven
- Package name: `com.keepintouch`
- Spring for GraphQL
- Spring Data JPA
- Flyway
- Postgres
- JUnit

### Frontend Defaults

- Angular
- TypeScript
- Apollo Angular
- Bulma
- CSS
- Angular routing
- Standalone components

### API Defaults

Use GraphQL for normal app data.

Do not create parallel REST endpoints for MVP app features.

REST may be used later for operational endpoints, auth callbacks, health checks, or deployment concerns if needed.

### Styling Defaults

Use Bulma as the primary CSS framework.

Use regular CSS for small custom overrides.

Do not use SCSS unless explicitly added later.

Do not add Bootstrap, Angular Material, Tailwind, DaisyUI, or another UI framework during MVP.

### Local Development Commands

Expected commands should eventually include:

```bash
docker compose up -d
cd backend && ./mvnw test
cd backend && ./mvnw spring-boot:run
cd frontend && npm install
cd frontend && npm start
```

These commands may not all work until their corresponding phase is complete.

## Product Decisions Already Made

- The app is a personal CRM.
- The repo name is `keep-in-touch`.
- The app should be free/open-source friendly.
- The license is MIT.
- The app should support one user locally at first, but the schema should support multiple users later.
- `users` should exist even before real authentication.
- Companies are part of MVP.
- Contacts are part of MVP.
- Interactions are part of MVP.
- Follow-ups are part of MVP.
- Birthdays are part of MVP.
- Tags are not part of MVP.
- CSV import/export is a stretch goal.
- Markdown interaction notes are a stretch goal.
- Job application tracking is a future idea, not MVP.
- Relationship strength/priority should not be included in MVP.
- `last_name` is not required.
- Email should be unique per user when present.
- Contacts do not require email, phone, or LinkedIn URL.
- Companies are not required for contacts.
- Follow-ups should require a specific due date/time.
- A contact should only have one open follow-up in MVP.
- Interactions should be editable after creation.
- Follow-ups can belong to a contact and may optionally be attached to an interaction.
- `source` should be a free text field, not an enum.

## API Strategy

The app will use GraphQL as the primary API layer.

GraphQL was chosen because this app has nested relationship data that different screens will need in different shapes.

Examples:

- Contact detail page needs contact, company, interactions, and follow-up data.
- Company detail page needs company and associated contacts.
- Dashboard needs due follow-ups, overdue follow-ups, upcoming birthdays, and contact context.
- Interaction timeline needs contact-specific historical data.

GraphQL lets the frontend request the exact data shape needed for each screen without creating many small REST endpoints.

### Backend GraphQL Stack

Use:

- Spring for GraphQL
- Spring Boot
- Spring Data JPA
- Postgres
- Flyway

### Frontend GraphQL Stack

Use:

- Apollo Angular
- Angular
- TypeScript

### API Guidelines

- Use GraphQL for normal application data.
- Avoid building both REST and GraphQL versions of the same app features.
- REST may be used later for operational endpoints, auth callbacks, or health checks if needed.
- Keep the first GraphQL schema small.
- Do not overbuild filtering, pagination, or advanced query capabilities in the first pass.
- Prefer clear mutations and query names over clever abstractions.

### Early GraphQL Operations

First slice:

```graphql
query Companies {
  companies {
    id
    name
    website
    industry
    location
  }
}
```

```graphql
mutation CreateCompany($input: CreateCompanyInput!) {
  createCompany(input: $input) {
    id
    name
  }
}
```

```graphql
query Contacts {
  contacts {
    id
    firstName
    lastName
    preferredName
    roleTitle
    email
    linkedinUrl
    company {
      id
      name
    }
    nextFollowUpAt
    lastInteractionAt
  }
}
```

```graphql
mutation CreateContact($input: CreateContactInput!) {
  createContact(input: $input) {
    id
    firstName
    lastName
  }
}
```

```graphql
query Contact($id: ID!) {
  contact(id: $id) {
    id
    firstName
    lastName
    preferredName
    roleTitle
    location
    linkedinUrl
    email
    phone
    relationshipType
    status
    source
    notes
    birthdayMonth
    birthdayDay
    birthdayYear
    company {
      id
      name
      website
    }
    interactions {
      id
      interactionType
      occurredAt
      summary
      outcome
    }
    openFollowUp {
      id
      dueAt
      status
      reason
    }
  }
}
```

Later slices:

```graphql
mutation CreateInteraction($input: CreateInteractionInput!) {
  createInteraction(input: $input) {
    id
    occurredAt
    summary
  }
}
```

```graphql
mutation UpdateInteraction($input: UpdateInteractionInput!) {
  updateInteraction(input: $input) {
    id
    occurredAt
    summary
    outcome
  }
}
```

```graphql
mutation CreateFollowUp($input: CreateFollowUpInput!) {
  createFollowUp(input: $input) {
    id
    dueAt
    status
    reason
  }
}
```

```graphql
mutation CompleteFollowUp($id: ID!) {
  completeFollowUp(id: $id) {
    id
    status
    completedAt
  }
}
```

```graphql
query Dashboard {
  dueFollowUps {
    id
    dueAt
    reason
    contact {
      id
      firstName
      lastName
      company {
        id
        name
      }
    }
  }

  upcomingBirthdays {
    id
    firstName
    lastName
    preferredName
    birthdayMonth
    birthdayDay
    company {
      id
      name
    }
  }
}
```

## Frontend Styling

The frontend will use Bulma as the primary CSS framework.

Bulma was chosen because it is:

- Simple
- Lightweight
- CSS-only
- Responsive
- Easy to use with Angular
- Not dependent on JavaScript
- Good enough for forms, tables, buttons, cards, navigation, and layout
- Less distracting than building a custom design system during MVP

Angular should own application behavior, state, routing, forms, and interactivity.

Bulma should own the base visual structure.

### Styling Guidelines

- Use Bulma for layout, forms, buttons, tables, cards, navbars, sections, containers, and notification styles.
- Keep custom CSS minimal.
- Prefer clear Angular templates over heavy custom styling.
- Do not add Bootstrap, Angular Material, Tailwind, DaisyUI, or other UI frameworks during MVP.
- Use Bulma's built-in light/dark behavior where possible.
- Revisit custom theming after the core MVP workflow is complete.

### MVP UI Vibe

The app should feel:

- Clean
- Calm
- Useful
- Lightweight
- Personal but not overly playful
- Professional enough for a portfolio project

Avoid making it feel:

- Enterprise-heavy
- Salesy
- Overdesigned
- Dashboard-bloated
- Social-media-like

## LLM Execution Guidelines

When an LLM is helping implement this project:

1. Read this file first.
2. Read `docs/schema.md` second.
3. Read the README third.
4. Work in small vertical slices.
5. Do not implement future ideas unless explicitly asked.
6. Do not add new frameworks without updating this file.
7. Do not silently change schema decisions.
8. Prefer simple working code over clever architecture.
9. Pause at review checkpoints.
10. Keep commits small and focused.
11. Favor understandable code over abstraction.

The LLM should not:

- Build the whole app in one pass.
- Add authentication before MVP.
- Add tags before MVP.
- Add AI features before MVP.
- Add CSV import/export before MVP.
- Add notifications before MVP.
- Add recurring follow-ups before MVP.
- Add a second API style unless explicitly approved.
- Add a CSS/component library beyond Bulma.
- Overcomplicate the database schema.

## Commit Guidelines

Use small commits.

Preferred commit style:

```text
Add schema documentation
Add Docker Postgres setup
Scaffold Spring Boot backend
Add initial Flyway migration
Add company domain model
Add contact domain model
Add GraphQL company queries
Add GraphQL contact mutations
Scaffold Angular frontend
Add Bulma styling setup
Add contact list page
```

Avoid vague commits like:

```text
updates
fix stuff
changes
work
```

## Phase 0: Project Foundation

### Goal

Establish the repo structure and project docs.

### Tasks

- Create repo.
- Add MIT license.
- Add Java `.gitignore`.
- Add README.
- Add `docs/schema.md`.
- Add `docs/plan.md`.
- Confirm project name and description.
- Confirm tech stack.
- Commit initial documentation.

### Review Checkpoint

User should review:

- README
- Schema doc
- Plan doc
- Project structure

### Done When

- Repo exists.
- Docs exist.
- README is concise.
- Schema and plan are committed.

## Phase 1: Local Infrastructure

### Goal

Set up the local database environment only.

### Tasks

- Add root `docker-compose.yml`.
- Add Postgres service.
- Configure database name, username, and password for local development.
- Add `.env.example` if needed.
- Confirm Docker starts Postgres locally.
- Confirm database port is available.
- Do not scaffold the backend yet.

### Suggested Local Database Values

```text
POSTGRES_DB=keep_in_touch
POSTGRES_USER=keep_in_touch
POSTGRES_PASSWORD=keep_in_touch
```

### Review Checkpoint

User should review:

- Docker file
- Environment variable naming
- Local database setup

### Done When

- `docker compose up -d` starts Postgres successfully.
- Postgres can be connected to locally.
- No backend scaffold has been added yet unless explicitly approved.

## Phase 2: Backend Scaffold

### Goal

Create the Spring Boot backend and connect it to Postgres.

### Tasks

- Scaffold Spring Boot backend in `backend/`.
- Use Java 25.
- Use Spring Boot 4.x.
- Use Maven.
- Use package name `com.keepintouch`.
- Add Spring for GraphQL.
- Add Spring Data JPA.
- Add PostgreSQL driver.
- Add Flyway.
- Add Validation.
- Add JUnit/Spring Boot Test.
- Configure local application properties.
- Confirm backend starts.
- Confirm backend can connect to Postgres.
- Confirm Flyway is detected.
- Confirm GraphQL endpoint exists.

### Backend Dependencies

Likely dependencies:

- Spring Boot
- Spring for GraphQL
- Spring Data JPA
- PostgreSQL Driver
- Flyway
- Validation
- Spring Boot Test

### Review Checkpoint

User should review:

- Backend package structure
- Dependency choices
- Application configuration
- Maven setup

### Done When

- Backend starts locally.
- Backend connects to Postgres.
- GraphQL endpoint exists.
- Flyway is wired but no app-specific migration is required yet.
- No app-specific domain code is required yet.

## Phase 3: Database Schema

### Goal

Create the initial database schema with Flyway.

### Tasks

- Read `docs/schema.md` before writing migrations.
- Create `backend/src/main/resources/db/migration/`.
- Add `V1__create_core_tables.sql`.
- Create tables based on `docs/schema.md`.
- Include primary keys.
- Include foreign keys.
- Include required constraints.
- Include useful initial indexes.
- Seed one default user if appropriate.

### MVP Tables

- `users`
- `companies`
- `contacts`
- `interactions`
- `follow_ups`

### Not MVP Tables

- `tags`
- `contact_tags`
- `job_applications`
- `custom_personal_dates`
- `notifications`
- `import_batches`

### Review Checkpoint

User should review:

- Flyway migration
- Table names
- Column names
- Constraints
- Indexes
- Whether seed data is acceptable

### Done When

- Flyway migration runs successfully.
- Tables exist in Postgres.
- App starts with clean database.
- App can be restarted without rerunning completed migrations.

## Phase 4: Backend Domain Model

### Goal

Create Java domain models that map to the database.

### Tasks

- Read `docs/schema.md` before creating entities.
- Create JPA entities.
- Create enums.
- Create repositories.
- Add basic service classes.
- Keep business logic out of GraphQL controllers/resolvers when possible.
- Add simple repository/service tests where useful.

### Entities

- `User`
- `Company`
- `Contact`
- `Interaction`
- `FollowUp`

### Enums

- `ContactStatus`
- `RelationshipType`
- `InteractionType`
- `FollowUpStatus`

### Review Checkpoint

User should review:

- Entity mapping
- Enum values
- Package structure
- Repository names
- Service boundaries

### Done When

- Entities compile.
- Repositories compile.
- Basic save/query behavior works.
- Tests pass.

## Phase 5: First Vertical Slice — Companies and Contacts

### Goal

Build the first complete usable workflow.

A user should be able to create companies, create contacts, associate contacts with companies, view a contact list, and open contact details.

### Backend Tasks

- Add GraphQL schema for companies.
- Add GraphQL schema for contacts.
- Add company queries.
- Add contact queries.
- Add company mutations.
- Add contact mutations.
- Add service validation.
- Enforce unique email per user when email is present.
- Support contacts without last name.
- Support contacts without email, phone, or LinkedIn.
- Support optional company association.

### Frontend Tasks

- Scaffold Angular frontend in `frontend/`.
- Add Bulma.
- Add Apollo Angular.
- Configure GraphQL client.
- Add basic layout/nav.
- Add company list page.
- Add create company form.
- Add company detail page.
- Add contact list page.
- Add create contact form.
- Add contact detail page.
- Associate contact with company during create/edit flow if available.

### Suggested Pages

```text
/
  Dashboard placeholder or redirect to contacts

/contacts
  Contact list

/contacts/new
  Create contact

/contacts/:id
  Contact detail

/companies
  Company list

/companies/new
  Create company

/companies/:id
  Company detail
```

### Review Checkpoint

User should review:

- GraphQL schema for companies/contacts
- Contact form fields
- Company form fields
- Contact list UI
- Contact detail UI
- Company detail UI

### Done When

- User can create a company.
- User can view company list.
- User can view company detail.
- User can create a contact.
- User can associate a contact with a company.
- User can view contact list.
- User can view contact detail.
- Data persists after refresh.

## Phase 6: Second Vertical Slice — Interaction Logging

### Goal

Allow users to log and edit interactions with contacts.

### Backend Tasks

- Add GraphQL interaction types.
- Add query for contact interactions.
- Add mutation to create interaction.
- Add mutation to update interaction.
- Update `contacts.last_interaction_at` when an interaction is created or updated.
- Add service tests for interaction creation and updating.

### Frontend Tasks

- Add interaction form to contact detail page.
- Add interaction timeline to contact detail page.
- Add edit interaction flow.
- Show interaction type, date/time, summary, and outcome.
- Keep markdown support out of MVP.

### Review Checkpoint

User should review:

- Interaction fields
- Interaction timeline UI
- Edit interaction behavior
- How `last_interaction_at` displays

### Done When

- User can add an interaction to a contact.
- User can edit an interaction.
- User can see interactions on the contact detail page.
- Contact `last_interaction_at` updates correctly.

## Phase 7: Third Vertical Slice — Follow-Ups and Dashboard

### Goal

Allow users to schedule, view, and complete follow-ups.

### Backend Tasks

- Add GraphQL follow-up types.
- Add mutation to create follow-up.
- Add mutation to complete follow-up.
- Add mutation to cancel follow-up if needed.
- Enforce one open follow-up per contact in MVP.
- Allow follow-up to optionally reference an interaction.
- Update `contacts.next_follow_up_at` when follow-ups change.
- Add dashboard query for due and overdue follow-ups.
- Add service tests for follow-up rules.

### Frontend Tasks

- Add follow-up form on contact detail page.
- Add open follow-up display on contact detail page.
- Add complete follow-up button.
- Add dashboard page.
- Show due follow-ups.
- Show overdue follow-ups.
- Show upcoming follow-ups if useful.
- Keep recurring follow-ups out of MVP.

### Review Checkpoint

User should review:

- Follow-up creation flow
- Dashboard layout
- One-open-follow-up rule
- Complete follow-up behavior
- Optional interaction attachment behavior

### Done When

- User can add one open follow-up to a contact.
- User can complete a follow-up.
- User can see due and overdue follow-ups on the dashboard.
- Contact `next_follow_up_at` updates correctly.

## Phase 8: Fourth Vertical Slice — Birthdays

### Goal

Support birthday tracking in MVP.

### Backend Tasks

- Ensure contacts support birthday month/day/year fields.
- Add validation for birthday month and day.
- Birthday year should be optional.
- Add query for upcoming birthdays.
- Keep custom personal dates out of MVP.

### Frontend Tasks

- Add birthday fields to contact create/edit form.
- Show birthday on contact detail page.
- Show upcoming birthdays on dashboard.

### Birthday Modeling

Use:

```text
birthday_month SMALLINT
birthday_day SMALLINT
birthday_year INTEGER nullable
```

Reason:

A user may know someone's birthday but not birth year.

### Review Checkpoint

User should review:

- Birthday fields
- Dashboard birthday display
- Whether birthday year feels useful or unnecessary

### Done When

- User can add birthday info to a contact.
- User can view birthday info.
- Dashboard can show upcoming birthdays.

## Phase 9: Testing and CI

### Goal

Add enough testing and automation to make the project credible.

### Backend Tests

Prioritize service tests around business rules:

- Create contact.
- Reject duplicate email per user when email is present.
- Allow contact without last name.
- Allow contact without email, phone, or LinkedIn.
- Create interaction.
- Update interaction.
- Update `last_interaction_at`.
- Create follow-up.
- Enforce one open follow-up per contact.
- Complete follow-up.
- Update `next_follow_up_at`.
- Validate birthday fields.

### Frontend Tests

Keep frontend tests minimal at first.

Possible tests:

- Contact list renders.
- Contact form validates required fields.
- Dashboard renders follow-up data.

### CI Tasks

- Add GitHub Actions workflow.
- Run backend tests.
- Build frontend.
- Optionally run frontend tests if configured.

### Review Checkpoint

User should review:

- What tests exist
- What behavior is covered
- CI workflow file

### Done When

- Backend tests pass locally.
- Frontend builds locally.
- GitHub Actions runs successfully.

## Phase 10: Polish and MVP Review

### Goal

Make the MVP presentable and usable.

### Tasks

- Clean up README.
- Confirm setup instructions work.
- Confirm app can run locally from fresh clone.
- Add screenshots if desired.
- Clean obvious UI rough edges.
- Review naming across codebase.
- Review GraphQL schema naming.
- Review database schema against docs.
- Remove unused code.
- Confirm future ideas are not partially implemented.
- Create final MVP checklist.

### Review Checkpoint

User should review the whole app as a portfolio/project artifact.

### Done When

A user can:

- Create a company.
- View companies.
- Open company details.
- Create a contact.
- Associate a contact with a company.
- View contacts.
- Open contact details.
- Log an interaction.
- Edit an interaction.
- Schedule a follow-up.
- Complete a follow-up.
- View due/overdue follow-ups.
- Add birthday info.
- View upcoming birthdays.
- Run the app locally.
- Run tests locally.

## Post-MVP Ideas

Do not implement these until MVP is complete.

### Tags

Add tags for flexible organization.

Possible tables:

- `tags`
- `contact_tags`

Possible uses:

- Galvanize
- Hack Reactor
- Recruiter
- Referral possible
- Chattanooga
- Frontend
- Coffee chat

### CSV Import/Export

Allow users to import/export contacts.

Useful for:

- Backup
- Migration
- Sharing data across tools
- Starting from a spreadsheet

### Custom Personal Dates

Allow users to track dates beyond birthdays.

Examples:

- Work anniversary
- First met
- Last coffee chat
- Important personal event

### Job Application Tracking

Potentially add job-search-specific workflow.

Possible entities:

- `opportunities`
- `opportunity_contacts`
- `applications`

Only add this if the app direction becomes more job-search-specific.

### Recurring Follow-Ups

Allow contacts to have recurring reminders.

Example:

- Follow up every 30 days.
- Check in every 3 months.

### Markdown Interaction Notes

Allow interaction summaries/outcomes to support Markdown.

This is useful but should not block MVP.

### Notifications

Add real reminders through email, push, or calendar integration.

This is post-MVP because it adds infrastructure and user preference complexity.

### Authentication

Add real login and user management.

Potential options later:

- Spring Security
- OAuth
- Auth provider
- Session auth
- JWT

Do not add auth before the core app works locally.

### AI Features

Possible later features:

- Summarize contact history.
- Suggest follow-up wording.
- Extract next action from interaction notes.
- Identify stale contacts.
- Generate relationship context summary.

Do not add AI features during MVP.

## Ticket Strategy

Do not create detailed tickets yet.

For now, use this file as the roadmap.

Use lightweight checklists during early implementation.

Start creating tickets when:

- Work spans multiple context windows.
- There are multiple parallel workstreams.
- The implementation plan becomes hard to hold in memory.
- Bugs and feature requests start mixing together.
- The app has enough structure that ticket overhead becomes useful.

When tickets are added, they should map to vertical slices, not tiny disconnected tasks.

Good ticket examples:

```text
Create company GraphQL API
Create contact list page
Add interaction timeline to contact detail
Add follow-up dashboard query
Add birthday dashboard section
```

Avoid tickets like:

```text
Make button blue
Think about dashboard
Work on backend
Update stuff
```

## Current Next Step

The next implementation step is Phase 1 only.

Complete:

1. Add root `docker-compose.yml`.
2. Add Postgres service.
3. Configure local database name, username, and password.
4. Confirm `docker compose up -d` starts Postgres.
5. Document any local setup notes if needed.

Stop after Phase 1 and wait for review.

Do not scaffold the backend until Phase 1 is reviewed.
