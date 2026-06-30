# Job Watcher Researcher

This document tracks implementation details for the company job watcher feature.
The current phase is the source research spike for the first four watched
companies.

## Phase 1: Source Research Spike

Status: Complete

Research date: 2026-06-30

### Repo Fit

Keep-In-Touch is currently a Spring Boot backend, Postgres database, Flyway
migrations, GraphQL API, and Angular/Apollo frontend. The watcher should fit as
a local-first backend worker that stores source state, seen jobs, and alert
history in the existing Postgres database. No frontend or GraphQL changes are
needed for the source spike.

### Shared Adapter Guidance

- Preserve each user-provided URL as `originalSourceUrl`.
- Store any discovered implementation URL as `canonicalSourceUrl`.
- Prefer direct HTTP fetch and deterministic parsing.
- Do not add Playwright for MVP.
- Keep adapter failures source-specific.
- Treat zero results as a valid adapter result, not an exception.
- On parse failure, return a source-level failure with enough context to update
  the adapter.
- Apply job relevance rules after ingestion, not inside source adapters.

### Capital One

Original source URL:

```text
https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1
```

Findings:

- The seed URL is useful and encodes the `software engineer` keyword search.
- The page is server-rendered and fetchable with plain HTTP.
- The source appears to be a Radancy/TalentBrew careers site.
- Search result HTML includes the result count, pagination, detail URLs,
  `data-job-id`, posted date, title, and location.
- The page exposes a `/search-jobs/results` AJAX endpoint, but a simple GET
  without the expected module parameters returns an empty payload. The
  server-rendered search URL is the safer MVP target.
- Detail pages are server-rendered and include richer fields such as internal
  requisition ID, posted date, title, category, experience, primary address,
  description, and a Workday apply link.
- Workday appears to be the apply destination, not the best source for MVP
  search ingestion.

Recommended extraction strategy:

- Use the original search URL as the canonical source for MVP.
- Parse the server-rendered result list first.
- Follow pagination via `Next` links until exhausted or until a configured page
  limit is reached.
- Fetch detail pages only when the result card does not provide enough data or
  when the adapter needs apply URL, category, experience, or fuller location.

Reliable fields:

- `externalId` from `data-job-id` or the final URL segment.
- `title` from the result link text.
- `location` from `.job-location`.
- `postedAt` from `.job-date-posted`.
- `url` from the result link.
- `applyUrl`, category, experience, and richer description from detail pages.

Best-effort fields:

- `country`, inferred from location text or detail page address.
- `experienceLevel`, parsed from detail page metadata and title.
- `jobCategory`, parsed from detail page metadata.

Zero-result behavior:

- Return an empty job list and mark the source check successful.

Parse-failure behavior:

- Mark only this source as failed, store the failing URL and missing selector or
  unexpected result shape in `lastError`.

### Clerk

Original source URL:

```text
https://clerk.com/careers#open-roles
```

Findings:

- The seed URL is a landing page anchor, not a structured results URL.
- The Clerk careers page is a Next.js-rendered page and is fetchable with plain
  HTTP.
- The page links to Ashby for jobs:
  `https://jobs.ashbyhq.com/Clerk/0aaf7916-ce76-4c72-b461-1604bf1b5bc2`
- The Clerk page currently renders an empty-state message saying there are no
  open positions.
- Ashby has a public non-user GraphQL endpoint that can return job postings for
  `organizationHostedJobsPageName: "Clerk"`.
- A minimal Ashby query for Clerk returned an empty `jobPostings` array, which
  confirms the empty-state path can be handled without browser automation.

Recommended extraction strategy:

- Preserve the Clerk landing page as `originalSourceUrl`.
- Use the Ashby public GraphQL endpoint as the canonical implementation source.
- Implement this as either `ClerkAshbyAdapter` or a generic `AshbyAdapter`
  configured with `organizationHostedJobsPageName = "Clerk"`.
- Handle empty `jobPostings` as a successful zero-result check.

Reliable fields:

- `externalId` from Ashby job posting `id`.
- `title` from Ashby job posting `title`.
- `location` from Ashby `locationName` and secondary locations when available.
- `employmentType` when exposed by the job board query.
- `url` can be constructed as `https://jobs.ashbyhq.com/Clerk/{id}` if Ashby
  does not expose a hosted URL in the chosen query.

Best-effort fields:

- Department/team, depending on the exact Ashby query fields accepted for this
  board.
- Description and apply URL from individual Ashby job detail queries.

Zero-result behavior:

- Return an empty job list and mark the source check successful.
- Do not treat "no open positions" as an error.

Parse-failure behavior:

- If the Ashby API schema changes, mark this source failed and include the
  GraphQL validation error in `lastError`.

### Vanguard

Original source URL:

```text
https://www.vanguardjobs.com/?source=Corporate_Website
```

Findings:

- The seed URL is a general careers landing page, not a filtered SWE search.
- The canonical search page linked from the landing page is:
  `https://www.vanguardjobs.com/job-search-results/`
- The search page is a WordPress/XCloud/Symphony Talent style page.
- Search results are client-populated rather than fully server-rendered.
- The page exposes a jobs API base:
  `https://jobsapi-google.m-cloud.io/api/`
- The page exposes the org id:
  `companies/fbd5ce04-22d1-4aae-90dc-0282e45ee06f`
- The page configures filters such as `is_internal:External`.
- The visible form supports `SearchText`, `primary_country`, `primary_category`,
  `level`, `compliment`, and `countryStateCity`.
- The initial page HTML lists available filter values and columns, but not the
  actual job rows.
- The exact API route and parameter shape were not confirmed during this spike.

Recommended extraction strategy:

- Preserve the landing page as `originalSourceUrl`.
- Store `/job-search-results/` as the initial `canonicalSourceUrl`.
- Implement an adapter scaffold around the XCloud jobs API config, but leave the
  exact route discovery as a near-term adapter task.
- Start with broad `SearchText = software engineer`, `primary_country = US`,
  `primary_category = Technology`, and `is_internal:External` once the route is
  confirmed.
- Do not add Playwright in MVP unless the API route cannot be determined from
  the page scripts.

Reliable fields expected from the API or client-rendered rows:

- `title`
- `location` or `city_state_country`
- `postedAt` or `open_date`
- `department` or `primary_category`
- `experienceLevel` or `level`
- `workModel` from `compliment`
- `url` via `/job` detail path plus API-provided job slug/id

Best-effort fields:

- `externalId`, depending on API response shape.
- `country`, normalized from `primary_country` or location text.

Zero-result behavior:

- Return an empty job list and mark the source check successful.

Parse-failure behavior:

- If the API route is unavailable or returns an unknown shape, mark this source
  failed and keep the source enabled for the next run after adapter updates.

### Moody's

Original source URL:

```text
https://careers.moodys.com/en/search-jobs/software%20engineer/49841/1
```

Findings:

- The seed URL is useful and encodes the `software engineer` keyword search.
- The page is server-rendered and fetchable with plain HTTP.
- The source appears to be a Radancy/TalentBrew careers site.
- Search result HTML includes result count, pagination metadata, detail URLs,
  `data-job-id`, title, and location.
- The page exposes `/en/search-jobs/results` and `/en/search-jobs/resultspost`
  AJAX endpoints, but a simple GET without the expected module parameters
  returns an empty payload. The server-rendered search URL is the safer MVP
  target.
- The page includes a SuccessFactors returning-applicant link, so SuccessFactors
  is likely the apply destination.
- Search result cards do not reliably include posted date in the visible result
  snippet, so detail pages may be needed for posted date, category, job
  reference, apply URL, and richer metadata.

Recommended extraction strategy:

- Use the original search URL as the canonical source for MVP.
- Parse the server-rendered result list first.
- Follow pagination via the search page metadata or `Next` links.
- Fetch detail pages only if needed for posted date, apply URL, job reference,
  category, experience, or description.

Reliable fields:

- `externalId` from `data-job-id` or the final URL segment.
- `title` from the result link text.
- `location` from `.job-location`.
- `url` from the result link.

Best-effort fields:

- `postedAt`, job reference, category, line of business, experience level,
  apply URL, and description snippet from detail pages.
- `country`, inferred from location text or detail page metadata.

Zero-result behavior:

- Return an empty job list and mark the source check successful.

Parse-failure behavior:

- Mark only this source as failed, store the failing URL and missing selector or
  unexpected result shape in `lastError`.

## Phase 1 Decision

The first implementation should use:

1. `CapitalOneCareersAdapter` using server-rendered Radancy result pages.
2. `MoodysCareersAdapter` using server-rendered Radancy result pages.
3. `ClerkAshbyAdapter` using Ashby's public non-user GraphQL endpoint.
4. `VanguardCareersAdapter` scaffold using the discovered XCloud API base and
   org id, with exact route discovery kept as the next adapter task.

This keeps MVP ingestion deterministic and avoids browser automation.
