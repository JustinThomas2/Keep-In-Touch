CREATE TABLE watched_job_sources (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    source_type VARCHAR(50) NOT NULL,
    original_source_url VARCHAR(1000) NOT NULL,
    canonical_source_url VARCHAR(1000),
    enabled BOOLEAN NOT NULL DEFAULT true,
    last_checked_at TIMESTAMPTZ,
    last_successful_check_at TIMESTAMPTZ,
    last_error TEXT,
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_watched_job_sources_source_type CHECK (
        source_type IN (
            'CAPITAL_ONE_CAREERS',
            'CLERK_CAREERS',
            'CLERK_ASHBY',
            'VANGUARD_CAREERS',
            'MOODYS_CAREERS',
            'CUSTOM_HTML',
            'MANUAL'
        )
    ),
    CONSTRAINT uniq_watched_job_sources_id_company_id UNIQUE (id, company_id)
);

CREATE TABLE job_postings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    company_id UUID NOT NULL REFERENCES companies(id) ON DELETE CASCADE,
    source_id UUID NOT NULL,
    external_id VARCHAR(255),
    stable_key VARCHAR(1000) NOT NULL,
    title VARCHAR(500) NOT NULL,
    location VARCHAR(500),
    country VARCHAR(100),
    url VARCHAR(1000) NOT NULL,
    canonical_url VARCHAR(1000),
    apply_url VARCHAR(1000),
    department VARCHAR(255),
    job_category VARCHAR(255),
    experience_level VARCHAR(255),
    posted_at TIMESTAMPTZ,
    description_snippet TEXT,
    first_seen_at TIMESTAMPTZ NOT NULL,
    last_seen_at TIMESTAMPTZ NOT NULL,
    content_hash VARCHAR(128) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_job_postings_status CHECK (
        status IN (
            'ACTIVE',
            'REMOVED',
            'IGNORED'
        )
    ),
    CONSTRAINT chk_job_postings_seen_at CHECK (
        last_seen_at >= first_seen_at
    ),
    CONSTRAINT fk_job_postings_source_company FOREIGN KEY (source_id, company_id)
        REFERENCES watched_job_sources(id, company_id)
        ON DELETE CASCADE
);

CREATE TABLE job_match_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID,
    include_keywords TEXT NOT NULL,
    exclude_keywords TEXT NOT NULL,
    include_countries TEXT NOT NULL,
    include_locations TEXT,
    remote_preference VARCHAR(50) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_job_match_rules_remote_preference CHECK (
        remote_preference IN (
            'US_ONLY',
            'REMOTE_US_ALLOWED',
            'REMOTE_ALLOWED',
            'ONSITE_ONLY'
        )
    ),
    CONSTRAINT fk_job_match_rules_company_user FOREIGN KEY (company_id, user_id)
        REFERENCES companies(id, user_id)
        ON DELETE CASCADE
);

CREATE TABLE job_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    job_posting_id UUID NOT NULL REFERENCES job_postings(id) ON DELETE CASCADE,
    sent_at TIMESTAMPTZ,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    payload_preview TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_job_alerts_channel CHECK (
        channel IN (
            'CONSOLE',
            'DISCORD_WEBHOOK',
            'MANUAL'
        )
    ),
    CONSTRAINT chk_job_alerts_status CHECK (
        status IN (
            'SENT',
            'FAILED',
            'SKIPPED'
        )
    ),
    CONSTRAINT chk_job_alerts_sent_at CHECK (
        (status = 'SENT' AND sent_at IS NOT NULL)
        OR (status <> 'SENT')
    )
);

CREATE INDEX idx_watched_job_sources_company_id
    ON watched_job_sources(company_id);

CREATE INDEX idx_watched_job_sources_enabled
    ON watched_job_sources(enabled);

CREATE INDEX idx_job_postings_company_status
    ON job_postings(company_id, status);

CREATE INDEX idx_job_postings_source_last_seen_at
    ON job_postings(source_id, last_seen_at DESC);

CREATE INDEX idx_job_postings_status_posted_at
    ON job_postings(status, posted_at DESC);

CREATE INDEX idx_job_match_rules_user_enabled
    ON job_match_rules(user_id, enabled);

CREATE INDEX idx_job_match_rules_company_id
    ON job_match_rules(company_id);

CREATE INDEX idx_job_alerts_job_posting_id
    ON job_alerts(job_posting_id);

CREATE INDEX idx_job_alerts_channel_status
    ON job_alerts(channel, status);

CREATE UNIQUE INDEX uniq_job_postings_source_stable_key
    ON job_postings(source_id, stable_key);

CREATE UNIQUE INDEX uniq_job_alerts_sent_discord
    ON job_alerts(job_posting_id, channel)
    WHERE channel = 'DISCORD_WEBHOOK'
      AND status = 'SENT';

CREATE TRIGGER trg_watched_job_sources_set_updated_at
BEFORE UPDATE ON watched_job_sources
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_job_postings_set_updated_at
BEFORE UPDATE ON job_postings
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_job_match_rules_set_updated_at
BEFORE UPDATE ON job_match_rules
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_job_alerts_set_updated_at
BEFORE UPDATE ON job_alerts
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();
