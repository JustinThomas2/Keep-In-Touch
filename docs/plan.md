You are working on the Personal CRM / Keep-In-Touch project.

Before making changes:

* Inspect the existing repo structure.
* Read AGENTS.md if present.
* Read docs/plan.md if present.
* Preserve the current project direction.
* Do not overwrite useful existing planning context.

Goal:
Add a systematic plan for a “company job watcher” feature that tracks job postings at the user's actual target companies, matches them against relevant USA-based SWE roles, and alerts the user in Discord when a relevant role appears so they can quickly ask the right contact for a referral.

Product framing:
This is not a generic job scraper.
This is a relationship-aware company watchlist.

Core user story:
As a job seeker, I want to watch companies where I have warm contacts, detect newly posted relevant USA-based SWE roles, and get notified in Discord with the best contact to reach out to.

Why this matters:
The user has already made warm connections at some companies, and some contacts may be willing to offer referrals. The missing piece is timing: the right job may not be posted yet. This feature should help the user become an early applicant while also having a warm referral path.

Important:
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
* Prefer GitHub Actions scheduled workflow for MVP.
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

Tasks:

1. Review existing project structure

* Identify current backend stack, data model, migrations, docs, and tests.
* Find where project planning docs live.
* If docs/plan.md exists, update it carefully.
* If a more focused doc is better, create docs/job-watcher-plan.md and link/reference it from docs/plan.md.
* Do not overwrite useful existing planning context.
* Preserve the current project direction and add this as a practical extension of the personal CRM.

2. Create a job watcher implementation plan

Document the feature in phases.

Phase 1: Source research spike

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

Phase 2: Data model

Add or plan entities/tables for:

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
* title
* location
* country nullable
* url
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

Recommended channel values:

* CONSOLE
* DISCORD_WEBHOOK
* MANUAL

Recommended alert status values:

* SENT
* FAILED
* SKIPPED

Phase 3: Adapter design

Create an adapter-based ingestion design.

JobSourceAdapter:

* fetchRaw(source)
* parseJobs(raw, source)
* normalizeJob(rawJob, source)
* getExternalId(normalizedJob)
* getContentHash(normalizedJob)

NormalizedJob:

* externalId
* title
* companyName
* location
* country nullable
* url
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

Watcher runner responsibilities:

* Load enabled watched sources.
* Run each adapter.
* Normalize jobs.
* Fingerprint jobs.
* Compare with previously seen jobs.
* Run deterministic matching.
* Create alerts only for new relevant postings.
* Send Discord webhook notifications only for new matches when not in dry-run mode.
* Log summary.

Phase 4: Matching

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

Design the alert to be sent through Discord using a webhook.

MVP notification decision:

* Notifications should happen through Discord.
* Use a Discord webhook for MVP.
* Store the webhook URL in a GitHub Actions repository secret.
* Do not commit webhook URLs or tokens.
* Notification delivery should happen only after source ingestion, matching, and idempotency are working.
* Prefer console dry-run before real Discord delivery.

GitHub Actions secret:

* DISCORD_WEBHOOK_URL

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

Phase 6: Scheduled execution

Plan a GitHub Actions workflow:

* Runs every 6 hours.
* Executes the watcher in dry-run or live mode.
* Uses DISCORD_WEBHOOK_URL from repository secrets when Discord sending is enabled.
* Checks configured sources.
* Matches relevant jobs.
* Sends Discord webhook notifications for new matches only.
* Logs summary:

  * sources checked
  * jobs found
  * new jobs
  * matches
  * alerts sent
  * failed sources
* Does not fail the entire workflow because one company source breaks.
* Each source failure should be recorded and reported in the run summary.

Suggested cron:
17 */6 * * *

Reason:
Use an offset minute instead of exactly the top of the hour.

Scheduled runner behavior:

* Run every 6 hours.
* Check configured sources.
* Match relevant jobs.
* Send Discord webhook notifications for new matches only.
* Do not resend alerts for jobs already alerted.
* Log a summary in the workflow output.
* Support dry-run mode that prints the Discord payload without sending.

Phase 7: MVP implementation scope

Define a small first implementation that can be completed safely later today.

MVP target:

* Add job watcher plan doc.
* Add adapter interface.
* Add normalized job type.
* Implement CapitalOneCareersAdapter first if source inspection shows stable fetch-based parsing.
* Implement MoodysCareersAdapter second if time allows.
* Add ClerkCareersAdapter or ClerkAshbyAdapter scaffold.
* Add VanguardCareersAdapter scaffold.
* Add deterministic matching function.
* Add Discord webhook payload formatter.
* Add dry-run CLI command that prints matching jobs and Discord payloads.
* Add tests for normalization, fingerprinting, matching, and Discord payload formatting.
* Add GitHub Actions scheduled workflow only after dry-run works locally.

Suggested dry-run command behavior:

* Run all configured sources.
* Print all found jobs.
* Print matched jobs separately.
* Print source failures separately.
* Print Discord payloads without sending.
* Do not send notifications.
* Do not mutate persisted state unless explicitly configured.

3. Add implementation notes

Engineering constraints:

* Do not scrape LinkedIn.
* Do not overbuild a crawler.
* Do not start with Playwright.
* Do not start with OpenClaw or an AI agent.
* Keep adapters isolated so one company change does not break the whole watcher.
* Prefer stable APIs, embedded JSON, and server-rendered HTML before dynamic scraping.
* The watcher must be idempotent.
* The same job should not alert repeatedly.
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

* [ ] Add docs/job-watcher-plan.md
* [ ] Reference job watcher plan from docs/plan.md
* [ ] Document the four initial watched companies and seed URLs
* [ ] Document that seed URLs preserve the user's current manual filters but may not be canonical implementation URLs
* [ ] Document Discord webhook notification design
* [ ] Document how to create and configure the DISCORD_WEBHOOK_URL secret

Source research:

* [ ] Inspect Capital One source structure
* [ ] Inspect Clerk source structure and ATS link
* [ ] Inspect Vanguard source structure
* [ ] Inspect Moody's source structure
* [ ] Document extraction strategy for each source

Core types:

* [ ] Add normalized job type
* [ ] Add watched source config type/entity plan
* [ ] Add job source adapter interface
* [ ] Add match result type
* [ ] Add Discord notification payload type if useful

Adapters:

* [ ] Add CapitalOneCareersAdapter
* [ ] Add MoodysCareersAdapter
* [ ] Add ClerkCareersAdapter or ClerkAshbyAdapter scaffold
* [ ] Add VanguardCareersAdapter scaffold
* [ ] Add ManualFixtureAdapter for tests

Matching:

* [ ] Add matching function
* [ ] Add default include keywords
* [ ] Add default exclude keywords
* [ ] Add USA/remote-USA location matching
* [ ] Add explanation output for matches and exclusions

Fingerprinting:

* [ ] Add fingerprint/content hash function
* [ ] Ensure same job does not alert repeatedly
* [ ] Decide whether changed content should trigger a new alert or only update contentHash

Runner:

* [ ] Add dry-run CLI command
* [ ] Print source summaries
* [ ] Print matched jobs
* [ ] Print source errors without failing the entire run
* [ ] Print Discord payload in dry-run mode

Notifications:

* [ ] Add Discord webhook notification sender
* [ ] Read DISCORD_WEBHOOK_URL from environment
* [ ] Add dry-run mode that prints Discord payload without sending
* [ ] Add test for Discord payload formatting
* [ ] Add safe error handling for failed Discord sends
* [ ] Ensure failed Discord sends are logged without crashing the entire watcher run

Tests:

* [ ] Add tests for matching rules
* [ ] Add tests for seniority exclusions
* [ ] Add tests for USA/remote matching
* [ ] Add tests for source normalization
* [ ] Add tests for fingerprinting/idempotency
* [ ] Add tests for Discord payload formatting

Scheduling:

* [ ] Add GitHub Actions schedule plan
* [ ] Use cron cadence around every 6 hours
* [ ] Use DISCORD_WEBHOOK_URL from GitHub Actions secrets
* [ ] Only enable scheduled workflow after dry-run works

Future:

* [ ] Add CRM relationship-aware alert payload
* [ ] Add UI for watched sources
* [ ] Add UI for ignored jobs
* [ ] Add UI for alert history
* [ ] Add Playwright fallback only if a high-priority source cannot be parsed with fetch
* [ ] Add AI summarization later only after deterministic ingestion and matching work

5. Do not build too much

If implementation is safe:

* Create lightweight scaffolding and tests.
* Prefer one working adapter over four half-working adapters.
* Prefer dry-run output over notification delivery.
* Prefer deterministic matching over AI summarization.
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

The MVP notification path should be:

1. Console dry-run output first.
2. Discord webhook payload preview second.
3. Real Discord webhook sending only after matching and idempotency are working.
