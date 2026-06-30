# Job Watcher Notification Design

This document captures the Phase 5 Discord notification payload design for the
company job watcher.

## Status

Complete

## Design Goals

- Send concise Discord alerts for new relevant jobs.
- Keep Discord sending disabled unless explicitly enabled.
- Support console dry-run before live delivery.
- Include enough context to decide whether to ask for a referral.
- Avoid repeated notifications for the same job.
- Persist send outcomes without storing secrets.

## Environment Variables

Required for live Discord sending:

```text
DISCORD_WEBHOOK_URL
```

Explicit live-send enable flag:

```text
JOB_WATCHER_SEND_DISCORD=true
```

Rules:

- Do not commit webhook URLs or tokens.
- Read webhook values only from the local process, Docker, or deployment
  environment.
- If `JOB_WATCHER_SEND_DISCORD` is not true, print the payload preview and do
  not send.
- If Discord sending is enabled but `DISCORD_WEBHOOK_URL` is missing, skip send
  and record a failed alert attempt with a safe error message.

## Payload Input

The notification formatter should receive already-matched job context.

```java
record JobAlertPayloadInput(
    NormalizedJob job,
    JobMatchResult match,
    CompanyReferralContext companyContext
) {}
```

Company referral context:

```java
record CompanyReferralContext(
    String companyName,
    List<ReferralContactSummary> contacts
) {}
```

Contact summary:

```java
record ReferralContactSummary(
    UUID contactId,
    String displayName,
    String roleTitle,
    String relationshipType,
    String status,
    String notesPreview
) {}
```

These are formatter inputs, not a required database schema.

## Payload Fields

Each job alert should include:

- company name
- role title
- location
- job URL
- apply URL if available
- posted date if available
- matched keywords
- why it matched
- best contact at the company, if available
- referral status or relationship notes, if available
- suggested next action

## Discord Message Shape

Use one Discord message per matched job for normal runs.

Example:

```text
New matching role found at Clerk

Role: Frontend Engineer
Location: Remote - US
Matched keywords: frontend, React, TypeScript
Why it matched: Matched because title contains Frontend Engineer and location appears remote-USA. No excluded seniority or specialty keywords found.
Best contact: Jordan Bott - former coworker
Suggested action: Ask whether she knows which team owns this role or who would be best to talk to.

Job: https://jobs.ashbyhq.com/Clerk/example
Apply: https://jobs.ashbyhq.com/Clerk/example/application
```

Formatting rules:

- Keep the message plain text for MVP.
- Include the direct job URL.
- Include the apply URL only when different or clearly available.
- Keep notes previews short and sanitized.
- Do not include secrets, webhook URLs, or raw source HTML.
- Prefer one concise message over a dense embed until the payload is proven.

## Best Contact Selection

MVP contact selection should be deterministic and simple.

Suggested order:

1. Contacts at the same company with `ACTIVE` or recent interaction status.
2. Contacts with notes or relationship type suggesting referral relevance.
3. Contacts with the most recent `last_interaction_at`.
4. Any contact at the company.

If no contact is available, omit the best contact line or say:

```text
Best contact: None saved for this company.
```

Do not invent contacts or referral status.

## Suggested Next Action

Suggested next action should be deterministic template text, not AI-generated.

Examples:

```text
Ask whether they know which team owns this role or who would be best to talk to.
```

```text
Ask whether they would be comfortable pointing you toward the right recruiter or hiring manager.
```

```text
Review the role first, then ask your saved contact whether this team is hiring actively.
```

## Dry-Run Behavior

Dry-run mode should:

- Run source ingestion and matching.
- Print payload previews to the console.
- Not send Discord messages.
- Not require `DISCORD_WEBHOOK_URL`.
- Avoid creating `SENT` `JobAlert` records.

Dry-run may optionally create no database records at all until the runner phase
decides the dry-run persistence policy.

## Live Send Behavior

Live mode should:

1. Check that the job matched.
2. Check that no `SENT` `DISCORD_WEBHOOK` alert already exists for the job.
3. Format the Discord payload.
4. Send only if `JOB_WATCHER_SEND_DISCORD=true`.
5. Persist `JobAlert` with `SENT` and `sent_at` on success.
6. Persist `JobAlert` with `FAILED` and a safe `error_message` on failure.

If Discord sending is disabled, the runner should print the payload preview and
skip creating a `SENT` alert.

## Failure Handling

Discord send failures should not fail the whole watcher run.

Failure examples:

- missing webhook URL when sending is enabled
- non-2xx Discord response
- timeout
- malformed webhook URL
- network failure

Failure behavior:

- Record `JobAlert.status = FAILED`.
- Store a short safe `error_message`.
- Repeated failures for the same job/channel may update the latest failed alert
  or be rate-limited by the runner to avoid noisy duplicate failure rows.
- Continue processing other matched jobs.
- Include failed sends in the run summary.

## Duplicate Notification Guard

Before sending Discord:

```text
Find existing JobAlert where:
  job_posting_id = current job
  channel = DISCORD_WEBHOOK
  status = SENT
```

If one exists, do not send again.

Changed job content should update `job_postings.content_hash` and
`job_postings.last_seen_at`, but should not trigger a new Discord alert by
default.

## Testing Strategy

Notification tests should cover:

- Payload includes company, role title, location, URL, matched keywords, and
  explanation.
- Apply URL is omitted when unavailable.
- Best contact is included when available.
- Missing contact is handled cleanly.
- Dry-run prints payload without sending.
- Live mode refuses to send unless explicitly enabled.
- Missing webhook URL records a safe failure when sending is enabled.
- Non-2xx Discord response records `FAILED`.
- Existing `SENT` alert prevents duplicate send.
