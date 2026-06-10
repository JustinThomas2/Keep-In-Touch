# Keep in Touch

A lightweight personal CRM for remembering people, conversations, and next steps.

## Overview

Keep in Touch helps users track relationships without turning their network into a sales pipeline.

The app is designed to answer:

- Who do I know?
- Where do they work?
- What have we talked about?
- When should I follow up?
- Who am I letting go cold?

## MVP Features

- Create and manage contacts
- Create and manage companies
- Associate contacts with companies
- Log interactions with contacts
- Edit interactions
- Schedule follow-ups
- Complete follow-ups
- View due and overdue follow-ups
- Track birthdays
- View upcoming birthdays
- View contact and company detail pages
- View contact history and relationship context

## Tech Stack

- Angular 19
- Apollo Angular
- Spring Boot 4
- Postgres
- Flyway
- JUnit
- Testcontainers
- Docker

## Local Setup

### Prerequisites

- Java 25
- Node.js and npm
- Docker with Docker Compose

### Environment

Create a root `.env` file. The same values are used by Docker Compose and the backend local profile.

```env
POSTGRES_DB=keep_in_touch
POSTGRES_USER=keep_in_touch
POSTGRES_PASSWORD=keep_in_touch
LOCAL_USER_EMAIL=local@keep-in-touch.test
```

### Start Postgres

From the repo root:

```bash
docker compose up -d
docker compose ps
```

### Start the Backend

From `backend/`:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

The GraphQL endpoint runs at:

```text
http://localhost:8080/graphql
```

GraphiQL is available for local development at:

```text
http://localhost:8080/graphiql
```

### Start the Frontend

From `frontend/`:

```bash
npm install
npm start
```

The Angular dev server runs at:

```text
http://localhost:4200
```

## Verification

Backend:

```bash
cd backend
./mvnw spotless:check
./mvnw test
```

Frontend:

```bash
cd frontend
npm run format:check
npm run codegen
npm run lint
npm run build
```

Frontend unit tests use Karma and Chrome. Do not run `npm test` until ChromeHeadless or `CHROME_BIN` is configured locally.

## MVP Review Checklist

See [docs/mvp-checklist.md](docs/mvp-checklist.md) for the final manual review flow.

## Future Ideas

- Tags
- CSV import/export
- Custom personal dates
- Job application tracking
- Recurring follow-ups
- Markdown interaction notes
- Notifications and reminders

## License

MIT
