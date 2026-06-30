You are working on the Personal CRM / Keep-In-Touch project.

Before making changes:

* Inspect the existing repo structure.
* Read AGENTS.md if present.
* Read docs/plan.md if present.
* Preserve the current project direction.
* Do not overwrite useful existing planning context.

Goal:
Add a systematic plan for a “company job watcher” feature that tracks job postings at the user's actual target companies, matches them against relevant USA-based SWE roles, persists watcher state in the CRM database, and alerts the user in Discord when a relevant role appears so they can quickly ask the right contact for a referral.

Product framing:
This is not a generic job scraper.
This is a relationship-aware company watchlist.

Core user story:
As a job seeker, I want to watch companies where I have warm contacts, detect newly posted relevant USA-based SWE roles, and get notified in Discord with the best contact to reach out to.

Why this matters:
The user has already made warm connections at some companies, and some contacts may be willing to offer referrals. The missing piece is timing: the right job may not be posted yet. This feature should help the user become an early applicant while also having a warm referral path.

Important runtime direction:
This feature should be built as a local-first worker that writes state to the CRM Postgres database.

Do not use GitHub Actions as the MVP runtime.
Do not use GitHub Actions cache, artifacts, or committed JSON state files as the source of truth.
GitHub Actions can remain a future deployment option, but the MVP should use the app's real database for idempotency, alert history, and source status.

Preferred progression:

1. Manual dry-run command.
2. Manual live run with Discord disabled by default.
3. Local scheduled worker using cron, systemd timer, or Docker Compose.
4. Later move the same worker to an always-on machine, home server, VPS, or cloud host.

Important source URL note:
The provided URLs are the user's current manually filtered watch URLs. Treat them as seed URLs, not guaranteed canonical API URLs.

For each source:

1. Preserve the user-provided URL in config as originalSourceUrl.
2. During the adapter spike, discover whether there is a better canonical results URL, API endpoint, embedded JSON payload, ATS endpoint, or stable HTML structure.
3. If the provided URL encodes filters, preserve those filters.
4. If filters are only partially represented or unreliable, ingest broadly and apply deterministic matching/post-filtering in code.
5. Always enforce the user's actual intent in matching:

   * company-specific roles
   * relevant SWE roles
   * USA-based or remote-USA roles
   * exclude clearly too-senior or wrong-specialty postings by default

Current target job boards:

1. Capital One

Original seed URL:
https://www.capitalonecareers.com/search-jobs/software%20engineer/234/1

Intent:
Watch Capital One for relevant Software Engineer roles in the USA.

Notes:

* The URL appears to represent a software engineer search.
* Treat this as a filtered seed URL.
* The adapter should inspect whether the search URL exposes a stable HTML structure, embedded data, or a backing API.
* Parse search result pages for job title, location, posted date if available, external ID if available, and detail URL.
* Individual job detail pages may provide richer metadata.
* Capital One jobs may ultimately apply through Workday, but the Capital One careers page should be treated as the source to inspect first.

2. Clerk

Original seed URL:
https://clerk.com/careers#open-roles

Intent:
Watch Clerk for relevant SWE roles, especially frontend, full-stack, product engineering, developer experience, internal tools, or platform/product engineering roles.

Notes:

* Clerk is high priority because the user has warm contacts there.
* The Clerk careers page is a landing page seed, not necessarily the canonical job source.
* The adapter should inspect the Clerk careers page and discover whether open roles are rendered directly, linked through Ashby, or exposed via an API.
* If Clerk uses Ashby, create a ClerkAshbyAdapter or generic Ashby adapter configured for Clerk.
* The adapter must handle zero open roles cleanly.

3. Vanguard

Original seed URL:
https://www.vanguardjobs.com/?source=Corporate_Website

Intent:
Watch Vanguard for relevant Software Engineer / Application Engineer roles in the USA.

Notes:

* This is likely a general careers landing page, not a fully filtered SWE search URL.
* Treat it as the starting point for source discovery.
* The adapter spike should find the real search/results URL and determine whether filters can be encoded in URL params or whether filtering must happen after ingestion.
* Relevant filters may include software engineering, technology, application engineering, USA, remote, hybrid, or locations the user is open to.
* Do not assume this source is simple HTML; inspect for embedded JSON, AJAX endpoints, or client-rendered results.
* Do not jump straight to Playwright unless simple fetch-based parsing is not viable.

4. Moody's

Original seed URL:
https://careers.moodys.com/en/search-jobs/software%20engineer/49841/1

Intent:
Watch Moody's for relevant Software Engineer roles in the USA.

Notes:

* The URL appears to represent a software engineer search.
* Treat this as a filtered seed URL.
* The adapter should inspect whether the search page has stable result markup, embedded data, or a backing API.
* Parse search results for title, location, detail URL, pagination, posted date if available, and external job reference if available.
* Individual job detail pages may provide richer metadata such as job reference, category, line of business, experience level, description, and apply URL.
* Moody's may use SuccessFactors as an apply destination, but the Moody's careers page should be treated as the source to inspect first.

Global assumptions:

* Check cadence: every 6 hours.
* Free/low-cost first.
* Prefer local-first execution for MVP.
* Persist watcher state in Postgres.
* Prefer direct HTTP fetch + parsing before browser automation.
* Avoid LinkedIn scraping.
* Avoid broad web crawling.
* Browser automation / Playwright is a later fallback, not MVP.
* Keep the first implementation deterministic and boring.
* Do not make an AI agent responsible for deciding whether a job exists.
* Do not use OpenClaw or agentic browsing for the MVP.
* Initial scope should be based on these four real boards, not hypothetical Greenhouse/Lever coverage.
* Notifications should happen through Discord using a Discord webhook.
* Console dry-run should come before real Discord delivery.
* Discord sending should be disabled by default unless explicitly configured.
* The same job should not trigger repeated alerts.

Tasks:

1. Review existing project structure

* Identify current backend stack, data model, migrations, docs, and tests.
* Find where project planning docs live.
* If docs/plan.md exists, update it carefully.
* If a more focused doc is better, create a supporting research or notes doc and link/reference it from docs/plan.md.
* Do not overwrite useful existing planning context.
* Preserve the current project direction and add this as a practical extension of the personal CRM.

2. Create a job watcher implementation plan

Document the feature in phases.

Phase 1: Source research spike

Status: Complete

Findings are documented in `docs/job-watcher-research.md`.

For each of the four target boards, determine the least brittle extraction strategy.

Questions to answer for each source:

* Does the seed URL contain useful filters?
* Is there a canonical search/results URL?
* Are results server-rendered?
* Is job data embedded as JSON in the HTML?
* Is there a backing API endpoint?
* Is the source an ATS wrapper such as Ashby, Workday, SuccessFactors, etc.?
* Can search result pages provide enough metadata, or do we need to fetch individual job detail pages?
* What fields can be reliably extracted?
* What fields need best-effort normalization?
* What should the adapter do when there are zero results?
* What should the adapter do when parsing fails?

Initial target adapters:

CapitalOneCareersAdapter:

* Use the Capital One seed URL.
* Preserve originalSourceUrl.
* Discover the best canonical data source.
* Parse search result pages for:

  * externalId / job ID if available
  * title
  * posted date if available
  * location
  * detail URL
* Fetch individual detail pages only if needed.
* Treat Workday as the apply destination unless a better canonical source is found.

ClerkCareersAdapter / ClerkAshbyAdapter:

* Use the Clerk seed URL.
* Preserve originalSourceUrl.
* Inspect the careers page for linked ATS/job board source.
* If Ashby is used, prefer a generic Ashby adapter configured for Clerk.
* Parse:

  * title
  * location
  * department/team
  * detail URL
  * apply URL
* Must handle empty role lists gracefully.
* Should never error just because Clerk has no open roles.

VanguardCareersAdapter:

* Use the Vanguard seed URL.
* Preserve originalSourceUrl.
* Discover the real search/results URL.
* Investigate whether jobs are server-rendered, embedded JSON, AJAX-backed, or client-rendered.
* Parse:

  * title
  * location
  * department/category
  * detail URL
  * posted date if available
  * job ID if available
* If no stable data source is found, create the adapter scaffold and document the remaining research.
* Do not add Playwright in MVP unless absolutely necessary.

MoodysCareersAdapter:

* Use the Moody's seed URL.
* Preserve originalSourceUrl.
* Discover the best canonical data source.
* Parse search result pages for:

  * title
  * location
  * detail URL
  * pagination
  * job reference if available
* Fetch individual detail pages only if needed.
* Treat SuccessFactors as the apply destination unless a better canonical source is found.

Phase 2: Data model and state persistence

Status: Complete

Schema decisions are documented in `docs/schema.md`.

Add or plan entities/tables for watcher state.

Important:
Because the MVP is local-first, Postgres is the source of truth for watcher state.

Do not use GitHub Actions artifacts, cache, or committed state files for idempotency.
The database should answer:

* Have we seen this job before?
* Has this job already triggered a Discord alert?
* When did this source last run successfully?
* Which sources are failing?
* Which jobs disappeared from the source?

WatchedJobSource:

* id
* companyId
* sourceType
* originalSourceUrl
* canonicalSourceUrl nullable
* enabled
* createdAt
* updatedAt
* lastCheckedAt nullable
* lastSuccessfulCheckAt nullable
* lastError nullable
* notes nullable

Recommended sourceType values:

* CAPITAL_ONE_CAREERS
* CLERK_CAREERS
* CLERK_ASHBY
* VANGUARD_CAREERS
* MOODYS_CAREERS
* CUSTOM_HTML
* MANUAL

JobPosting:

* id
* companyId
* sourceId
* externalId
* stableKey
* title
* location
* country nullable
* url
* canonicalUrl nullable
* applyUrl nullable
* department nullable
* jobCategory nullable
* experienceLevel nullable
* postedAt nullable
* descriptionSnippet nullable
* firstSeenAt
* lastSeenAt
* contentHash
* status

Recommended status values:

* ACTIVE
* REMOVED
* IGNORED

JobMatchRule:

* id
* companyId nullable
* includeKeywords
* excludeKeywords
* includeCountries
* includeLocations
* remotePreference
* enabled
* createdAt
* updatedAt

JobAlert:

* id
* jobPostingId
* sentAt
* channel
* status
* errorMessage nullable
* payloadPreview nullable

Recommended channel values:

* CONSOLE
* DISCORD_WEBHOOK
* MANUAL

Recommended alert status values:

* SENT
* FAILED
* SKIPPED

Idempotency rules:

* Use sourceType + externalId as the preferred stable job key.
* If externalId is missing, use sourceType + normalized title + normalized location + canonical URL.
* Do not send a Discord alert if a JobAlert with status SENT already exists for the JobPosting.
* If a previously seen job changes, update contentHash and lastSeenAt but do not resend by default.
* If a job disappears from the source, mark it REMOVED after a safe threshold or after a clear missing-from-source run.

Phase 3: Adapter design

Status: Complete

Adapter design is documented in `docs/job-watcher-adapter-design.md`.

Create an adapter-based ingestion design.

JobSourceAdapter:

* fetchRaw(source)
* parseJobs(raw, source)
* normalizeJob(rawJob, source)
* getExternalId(normalizedJob)
* getContentHash(normalizedJob)

NormalizedJob:

* externalId
* stableKey nullable
* title
* companyName
* location
* country nullable
* url
* canonicalUrl nullable
* applyUrl nullable
* department nullable
* jobCategory nullable
* experienceLevel nullable
* postedAt nullable
* descriptionSnippet nullable
* rawSource nullable

Adapter responsibilities:

* Fetch only from configured source URLs or discovered canonical URLs.
* Normalize output into a shared shape.
* Avoid applying user-specific matching logic inside adapters.
* Be resilient to empty results.
* Fail source-by-source, not run-wide.
* Return parse errors in a way the runner can log.
* Avoid persisting directly; adapters should return normalized data to the runner/service layer.

Watcher runner responsibilities:

* Load enabled watched sources from Postgres.
* Run each adapter.
* Normalize jobs.
* Generate stable keys.
* Fingerprint jobs.
* Upsert job postings into Postgres.
* Compare against previously seen jobs in Postgres.
* Run deterministic matching.
* Create JobAlert records only for new relevant postings.
* Send Discord webhook notifications only for new matches when not in dry-run mode.
* Log summary.

Phase 4: Matching

Status: Complete

Matching design is documented in `docs/job-watcher-matching-design.md`.

Create deterministic matching logic focused on relevant SWE roles in the USA.

Default include keywords:

* software engineer
* software developer
* frontend
* front-end
* front end
* full stack
* full-stack
* react
* typescript
* javascript
* ui
* product engineer
* application engineer
* internal tools
* platform engineer

Default exclude keywords:

* staff
* principal
* distinguished
* director
* manager
* mobile-only
* ios
* android
* embedded
* firmware
* devops
* sre
* data scientist
* machine learning engineer
* internship
* new grad

Important nuance:

* Senior Software Engineer should not be excluded by default.
* Lead Software Engineer should be configurable, but probably lower priority.
* Staff/Principal/Director/Manager should be excluded by default because they are likely too senior.
* Platform Engineer should not always be excluded because some platform roles may be application/platform-product engineering.
* DevOps/SRE-heavy roles should be filtered out by default.
* AI tooling or developer tooling roles can be relevant if they are real software engineering roles, not pure prompt-labeling or annotation roles.

USA relevance:

* Prefer locations in the United States.
* Include remote roles if they are open to the United States.
* Store country/location as best-effort normalized fields.
* If country cannot be determined from the search result, keep the job but mark country as UNKNOWN and let the matcher use location text.
* Do not discard uncertain jobs too aggressively in MVP. Prefer surfacing a possible match with explanation over silently dropping a potentially relevant role.

Matching output should include:

* matched: boolean
* matchedKeywords
* excludedKeywords
* locationReason
* seniorityReason
* explanation

Example matching explanation:
"Matched because title contains Software Engineer and location appears USA-based. No excluded seniority or specialty keywords found."

Example exclusion explanation:
"Excluded because title contains Principal, which is above the user's target seniority."

Phase 5: Discord notification payload

Status: Not started

Design the alert to be sent through Discord using a webhook.

MVP notification decision:

* Notifications should happen through Discord.
* Use a Discord webhook for MVP.
* Read DISCORD_WEBHOOK_URL from the local environment or Docker environment.
* Do not commit webhook URLs or tokens.
* Notification delivery should happen only after source ingestion, matching, and idempotency are working.
* Prefer console dry-run before real Discord delivery.
* Discord sending should be disabled by default unless explicitly enabled.

Environment variable:

* DISCORD_WEBHOOK_URL

Optional environment variable:

* JOB_WATCHER_SEND_DISCORD=true

Alert should include:

* Company
* Role title
* Location
* Job URL
* Apply URL if available
* Posted date if available
* Matched keywords
* Why it matched
* Best contacts at the company
* Referral status / relationship notes if available
* Suggested next action

Example Discord alert:

New matching role found at Clerk

Role: Frontend Engineer
Location: Remote
Matched because: frontend, React, TypeScript
Best contact: Jordan Bott
Suggested action: ask whether she knows which team owns this role or who would be best to talk to.

Discord formatting guidance:

* Keep messages short enough to read quickly.
* Prefer one Discord message per matched job.
* If many jobs match in one run, send a compact summary first.
* Send individual messages only for high-confidence matches.
* Include direct job URL.
* Include best contact if available.
* Include “why it matched” so the user can quickly judge relevance.
* Do not send repeated notifications for the same job.
* Persist JobAlert status after send attempt.
* If Discord sending fails, store JobAlert status FAILED and errorMessage.

Phase 6: Local-first worker execution

Status: Not started

Plan a local-first worker instead of a GitHub Actions scheduled workflow.

Runtime goal:
The job watcher should be runnable as a command that connects to the existing CRM Postgres database, checks watched sources, updates watcher state, and optionally sends Discord notifications.

Preferred execution modes:

1. Dry-run mode

* Runs adapters.
* Prints source summaries.
* Prints found jobs.
* Prints matched jobs.
* Prints Discord payload previews.
* Does not send Discord notifications.
* Does not mutate persisted state unless explicitly configured.

2. Live manual mode

* Runs adapters.
* Writes seen jobs to Postgres.
* Writes source check status to Postgres.
* Creates JobAlert records for new matches.
* Sends Discord notifications only if enabled and DISCORD_WEBHOOK_URL is configured.
* Does not resend alerts for jobs already alerted.

3. Local scheduled mode

* Runs every 6 hours using one of:

  * host cron
  * systemd timer
  * Docker Compose worker service
  * a small local scheduler process

Recommended first local command shape:
Create a dry-run command similar to:

./mvnw spring-boot:run -Dspring-boot.run.arguments="job-watcher --dry-run"

or, if the repo has a better existing command pattern, follow that pattern.

Possible later Docker command shape:

docker compose run --rm job-watcher --dry-run

Scheduling guidance:

* First support manual dry-run.
* Then support manual live run.
* Then document local cron/systemd scheduling.
* Then optionally add a Docker Compose worker service.
* Do not require the main web app to be open in a browser.
* The worker may either run as a command and exit, or run inside the Spring app as a scheduled job, but command-and-exit is preferred for MVP clarity.

Suggested cron cadence:

17 */6 * * *

Reason:
Run about every 6 hours, offset from the top of the hour.

Important:
If using host cron, document that the local machine or server must be on for the watcher to run.
This is acceptable for MVP because Postgres-backed state keeps the architecture clean and portable.

Phase 7: MVP implementation scope

Status: Not started

Define a small first implementation that can be completed safely later today.

MVP target:

* Add job watcher plan doc.
* Add local-first runtime decision.
* Add Postgres-backed state/idempotency plan.
* Add adapter interface.
* Add normalized job type.
* Add database migration plan or migration scaffolding for watched sources, job postings, match rules, and alerts.
* Implement CapitalOneCareersAdapter first if source inspection shows stable fetch-based parsing.
* Implement MoodysCareersAdapter second if time allows.
* Add ClerkCareersAdapter or ClerkAshbyAdapter scaffold.
* Add VanguardCareersAdapter scaffold.
* Add deterministic matching function.
* Add Discord webhook payload formatter.
* Add dry-run CLI command that prints matching jobs and Discord payloads.
* Add tests for normalization, fingerprinting, matching, idempotency, and Discord payload formatting.
* Do not add GitHub Actions scheduled workflow for MVP.

Suggested dry-run command behavior:

* Run all configured sources.
* Print all found jobs.
* Print matched jobs separately.
* Print source failures separately.
* Print Discord payloads without sending.
* Do not send notifications.
* Do not mutate persisted state unless explicitly configured.

Suggested live command behavior:

* Run all configured sources.
* Upsert source check status.
* Upsert found job postings.
* Mark matching jobs.
* Create JobAlert records for new matches.
* Send Discord notifications only when explicitly enabled.
* Do not resend alerts for jobs with an existing SENT JobAlert.

Phase 8: Future deployment options

Status: Not started

Keep the worker portable so the runtime can move later without rewriting core logic.

Future runtime options:

1. Home server / always-on local machine

* Run Postgres and worker locally.
* Schedule with cron, systemd, or Docker Compose.
* Good fit if the user already maintains local infrastructure.

2. VPS

* Run the CRM database and worker on a low-cost VPS.
* Good fit if always-on reliability becomes important.

3. Cloud/serverless

* Possible later, but not MVP.
* Would need a durable state store such as hosted Postgres.
* Avoid using GitHub Actions as a database.

4. GitHub Actions

* Future option only.
* If used later, it should connect to a real hosted database.
* It should not use committed JSON state, cache, or artifacts for core idempotency.

3. Add implementation notes

Engineering constraints:

* Do not scrape LinkedIn.
* Do not overbuild a crawler.
* Do not start with Playwright.
* Do not start with OpenClaw or an AI agent.
* Do not use GitHub Actions as the MVP runtime.
* Do not use GitHub Actions cache, artifacts, or committed JSON state files as the source of truth.
* Keep adapters isolated so one company change does not break the whole watcher.
* Prefer stable APIs, embedded JSON, and server-rendered HTML before dynamic scraping.
* The watcher must be idempotent.
* The same job should not alert repeatedly.
* Persist idempotency state in Postgres.
* If a job changes, update lastSeenAt/contentHash without resending unless configured.
* Store raw HTML samples or sanitized fixtures for tests where appropriate.
* Add source-specific tests so adapter changes are obvious.
* Keep the feature useful before making it elegant.
* Do not commit Discord webhook URLs or tokens.
* Read DISCORD_WEBHOOK_URL from environment only.
* Discord notifications should be disabled unless explicitly configured.

4. Add near-term task checklist

Create a checklist that can be implemented later today.

Documentation:

* [x] Add docs/job-watcher-research.md
* [x] Reference job watcher research from docs/plan.md
* [ ] Document the four initial watched companies and seed URLs
* [ ] Document that seed URLs preserve the user's current manual filters but may not be canonical implementation URLs
* [ ] Document local-first worker runtime decision
* [x] Document Postgres-backed state/idempotency decision
* [ ] Document Discord webhook notification design
* [ ] Document how to configure DISCORD_WEBHOOK_URL locally
* [ ] Document how to run the watcher manually in dry-run mode
* [ ] Document future scheduling options: cron, systemd, Docker Compose

Source research:

* [ ] Inspect Capital One source structure
* [ ] Inspect Clerk source structure and ATS link
* [ ] Inspect Vanguard source structure
* [ ] Inspect Moody's source structure
* [ ] Document extraction strategy for each source

Data model:

* [x] Add or plan migration for watched job sources
* [x] Add or plan migration for job postings
* [x] Add or plan migration for job match rules
* [x] Add or plan migration for job alerts
* [x] Add stableKey uniqueness strategy
* [x] Add source status fields for lastCheckedAt, lastSuccessfulCheckAt, and lastError

Core types:

* [x] Plan normalized job type
* [x] Plan watched source config type/entity
* [x] Plan job source adapter interface
* [ ] Add match result type
* [ ] Add Discord notification payload type if useful

Adapters:

* [x] Plan CapitalOneCareersAdapter
* [x] Plan MoodysCareersAdapter
* [x] Plan ClerkCareersAdapter or ClerkAshbyAdapter scaffold
* [x] Plan VanguardCareersAdapter scaffold
* [x] Plan ManualFixtureAdapter for tests

Matching:

* [x] Plan matching function
* [x] Plan default include keywords
* [x] Plan default exclude keywords
* [x] Plan USA/remote-USA location matching
* [x] Plan explanation output for matches and exclusions

Fingerprinting and idempotency:

* [x] Plan stable job key function
* [x] Plan fingerprint/content hash function
* [ ] Ensure same job does not alert repeatedly
* [ ] Check JobAlert before sending Discord notification
* [ ] Decide whether changed content should trigger a new alert or only update contentHash

Runner:

* [ ] Add dry-run CLI command
* [ ] Print source summaries
* [ ] Print matched jobs
* [ ] Print source errors without failing the entire run
* [ ] Print Discord payload in dry-run mode
* [ ] Add live run mode that writes to Postgres
* [ ] Ensure live mode does not send Discord unless explicitly enabled

Notifications:

* [ ] Add Discord webhook notification sender
* [ ] Read DISCORD_WEBHOOK_URL from environment
* [ ] Add JOB_WATCHER_SEND_DISCORD or equivalent explicit enable flag
* [ ] Add dry-run mode that prints Discord payload without sending
* [ ] Add test for Discord payload formatting
* [ ] Add safe error handling for failed Discord sends
* [ ] Ensure failed Discord sends are logged without crashing the entire watcher run
* [ ] Persist Discord send outcome in JobAlert

Tests:

* [ ] Add tests for matching rules
* [ ] Add tests for seniority exclusions
* [ ] Add tests for USA/remote matching
* [ ] Add tests for source normalization
* [ ] Add tests for stable job key generation
* [ ] Add tests for fingerprinting/idempotency
* [ ] Add tests for Discord payload formatting

Scheduling:

* [ ] Do not add GitHub Actions schedule for MVP
* [ ] Add docs for local cron scheduling
* [ ] Add docs for systemd timer scheduling if useful
* [ ] Add Docker Compose worker service only if it fits the repo cleanly
* [ ] Use cadence around every 6 hours

Future:

* [ ] Add CRM relationship-aware alert payload
* [ ] Add UI for watched sources
* [ ] Add UI for ignored jobs
* [ ] Add UI for alert history
* [ ] Add Playwright fallback only if a high-priority source cannot be parsed with fetch
* [ ] Add AI summarization later only after deterministic ingestion and matching work
* [ ] Consider hosted deployment only after local worker proves useful

5. Do not build too much

If implementation is safe:

* Create lightweight scaffolding and tests.
* Prefer one working adapter over four half-working adapters.
* Prefer dry-run output over notification delivery.
* Prefer deterministic matching over AI summarization.
* Prefer Postgres-backed idempotency over clever external state.
* Prefer local manual execution before local scheduling.
* Prefer Discord webhook formatting before actual sending.

If repo state is uncertain:

* Limit changes to docs/plan updates and a clear task breakdown.
* Do not force a half-integrated implementation.

Do not build full notification delivery until source ingestion, matching, and idempotency are working.

Deliverable:
A committed-ready plan document and, if safe, minimal scaffolding for the job watcher feature based on the four real target companies:

* Capital One
* Clerk
* Vanguard
* Moody's

The plan should preserve the user's provided filtered seed URLs while allowing adapters to discover better canonical data sources during implementation.

The MVP runtime path should be:

1. Manual dry-run command first.
2. Postgres-backed state and idempotency second.
3. Discord webhook payload preview third.
4. Manual live run with Discord disabled by default.
5. Real Discord webhook sending only after matching and idempotency are working.
6. Local scheduled worker every 6 hours once manual runs are reliable.

Do not use GitHub Actions as the MVP runtime.
Do not use GitHub Actions artifacts, cache, or committed JSON state as the source of truth.
