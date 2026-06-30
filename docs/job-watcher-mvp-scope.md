# Job Watcher MVP Scope

This document captures the Phase 7 MVP implementation scope for the company job
watcher.

## Status

Complete

## Goal

Build the smallest useful local-first job watcher that can:

- read watched source configuration from Postgres
- fetch and normalize jobs from at least one real source
- match relevant USA-based SWE roles deterministically
- print dry-run output with Discord payload previews
- avoid duplicate alerts through Postgres-backed state

## First Implementation Sequence

### Slice 1: Persistence Foundation

Implement the watcher tables from `docs/schema.md` with Flyway, JPA entities,
repositories, and focused persistence tests.

Tables:

- `watched_job_sources`
- `job_postings`
- `job_match_rules`
- `job_alerts`

Keep this slice backend-only.

### Slice 2: Core Types And Pure Logic

Add Java types and tests for:

- watched source config
- normalized job
- match result
- Discord payload model
- stable job key generation
- content hash generation
- deterministic matching
- Discord payload formatting

This slice should use fixtures and pure unit tests where practical.

### Slice 3: Dry-Run Runner With Fixtures

Add a command-and-exit dry-run runner using a manual fixture adapter first.

The runner should:

- load enabled watched sources when persistence is available
- run adapters
- print source summaries
- print found jobs
- print matched jobs
- print Discord payload previews
- avoid Discord sends
- avoid database mutation by default in dry-run mode
- exit after the run

### Slice 4: First Real Adapter

Implement `CapitalOneCareersAdapter` first.

Reason:

- Source research found fetchable server-rendered result pages.
- Result cards expose job IDs, titles, locations, posted dates, and detail URLs.
- It is the lowest-risk real adapter to validate the ingestion path.

### Slice 5: Live Manual Mode

After dry-run works, add live manual mode.

Live mode should:

- update source check status
- upsert job postings
- update `last_seen_at` and `content_hash`
- check existing `SENT` alerts before sending
- keep Discord disabled unless explicitly enabled
- persist Discord send outcomes only for live send attempts

### Slice 6: Additional Source Coverage

Add source coverage incrementally:

1. `MoodysCareersAdapter`
2. `ClerkAshbyAdapter`
3. `VanguardCareersAdapter` scaffold or implementation after API route
   discovery

Prefer one working adapter over several partial adapters.

## MVP Non-Goals

Do not include these in the first implementation pass:

- GitHub Actions scheduling
- Playwright/browser automation
- LinkedIn scraping
- AI matching or summarization
- frontend UI for watched sources
- frontend UI for ignored jobs
- frontend UI for alert history
- recurring jobs or complex scheduling inside the app
- full production deployment

## Command Requirements

The worker command must run once and exit.

It should not accidentally start the normal long-running web app and hang
forever unless that mode is explicitly requested later.

Preferred initial command shape:

```bash
cd backend
./mvnw spring-boot:run \
  -Dspring-boot.run.profiles=local \
  -Dspring-boot.run.arguments="job-watcher --dry-run"
```

If implementation discovers a cleaner command pattern, update
`docs/job-watcher-worker-design.md`.

## Verification Target

Before considering the MVP implementation done, run the relevant documented
backend checks:

- backend formatting check
- backend tests

If frontend code is untouched, frontend verification is not required for the
first worker slices.

## Completion Criteria

MVP implementation is complete when:

- at least one real source can be checked by direct HTTP fetch
- dry-run prints found jobs, matched jobs, source failures, and Discord payload
  previews
- live mode can persist seen jobs and source check status
- sent Discord alerts are not repeated for the same job
- Discord sending is disabled unless explicitly enabled
- tests cover stable keys, content hashes, matching, payload formatting, and
  idempotency
