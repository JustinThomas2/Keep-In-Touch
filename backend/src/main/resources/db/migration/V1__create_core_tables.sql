CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    display_name VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE companies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    website VARCHAR(500),
    industry VARCHAR(255),
    location VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT uniq_companies_id_user_id UNIQUE (id, user_id)
);

CREATE TABLE contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    company_id UUID,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100),
    preferred_name VARCHAR(100),
    role_title VARCHAR(255),
    location VARCHAR(255),
    linkedin_url VARCHAR(500),
    email VARCHAR(255),
    phone VARCHAR(50),
    relationship_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'NEW',
    source VARCHAR(255),
    notes TEXT,
    birthday_month SMALLINT,
    birthday_day SMALLINT,
    birthday_year INTEGER,
    last_interaction_at TIMESTAMPTZ,
    next_follow_up_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_contacts_relationship_type CHECK (
        relationship_type IN (
            'FRIEND',
            'FORMER_COWORKER',
            'ALUMNI',
            'RECRUITER',
            'MENTOR',
            'COMMUNITY',
            'PROFESSIONAL',
            'OTHER'
        )
    ),
    CONSTRAINT chk_contacts_status CHECK (
        status IN (
            'NEW',
            'REACHED_OUT',
            'WAITING_FOR_RESPONSE',
            'CONVERSATION_SCHEDULED',
            'ACTIVE',
            'DORMANT',
            'PAUSED',
            'DO_NOT_CONTACT'
        )
    ),
    CONSTRAINT chk_contacts_birthday_parts CHECK (
        (birthday_month IS NULL AND birthday_day IS NULL)
        OR (birthday_month IS NOT NULL AND birthday_day IS NOT NULL)
    ),
    CONSTRAINT chk_contacts_birthday_year_requires_date CHECK (
        birthday_year IS NULL
        OR (birthday_month IS NOT NULL AND birthday_day IS NOT NULL)
    ),
    CONSTRAINT chk_contacts_birthday_date CHECK (
        birthday_month IS NULL
        OR (
            birthday_month BETWEEN 1 AND 12
            AND birthday_day BETWEEN 1 AND CASE
                WHEN birthday_month IN (1, 3, 5, 7, 8, 10, 12) THEN 31
                WHEN birthday_month IN (4, 6, 9, 11) THEN 30
                WHEN birthday_month = 2 THEN 29
            END
        )
    ),
    CONSTRAINT chk_contacts_birthday_leap_year CHECK (
        birthday_year IS NULL
        OR birthday_month <> 2
        OR birthday_day <> 29
        OR (
            birthday_year % 400 = 0
            OR (birthday_year % 4 = 0 AND birthday_year % 100 <> 0)
        )
    ),
    CONSTRAINT fk_contacts_company_user FOREIGN KEY (company_id, user_id)
        REFERENCES companies(id, user_id)
        ON DELETE NO ACTION
);

CREATE TABLE interactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contact_id UUID NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    interaction_type VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    summary TEXT NOT NULL,
    outcome TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_interactions_interaction_type CHECK (
        interaction_type IN (
            'LINKEDIN_MESSAGE',
            'EMAIL',
            'COFFEE_CHAT',
            'PHONE_CALL',
            'SLACK',
            'IN_PERSON',
            'APPLICATION_REFERRAL',
            'OTHER'
        )
    ),
    CONSTRAINT uniq_interactions_id_contact_id UNIQUE (id, contact_id)
);

CREATE TABLE follow_ups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contact_id UUID NOT NULL REFERENCES contacts(id) ON DELETE CASCADE,
    interaction_id UUID,
    due_at TIMESTAMPTZ NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN',
    reason TEXT,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_follow_ups_status CHECK (
        status IN (
            'OPEN',
            'COMPLETED',
            'CANCELLED'
        )
    ),
    CONSTRAINT chk_follow_ups_completed_at CHECK (
        (status = 'COMPLETED' AND completed_at IS NOT NULL)
        OR (status <> 'COMPLETED' AND completed_at IS NULL)
    ),
    CONSTRAINT fk_follow_ups_interaction_contact FOREIGN KEY (interaction_id, contact_id)
        REFERENCES interactions(id, contact_id)
        ON DELETE SET NULL (interaction_id)
);

CREATE INDEX idx_companies_user_id
    ON companies(user_id);

CREATE INDEX idx_contacts_user_id
    ON contacts(user_id);

CREATE INDEX idx_contacts_company_id
    ON contacts(company_id);

CREATE INDEX idx_contacts_user_status
    ON contacts(user_id, status);

CREATE INDEX idx_contacts_user_next_follow_up_at
    ON contacts(user_id, next_follow_up_at);

CREATE INDEX idx_contacts_user_last_interaction_at
    ON contacts(user_id, last_interaction_at);

CREATE INDEX idx_contacts_user_birthday_month_day
    ON contacts(user_id, birthday_month, birthday_day);

CREATE INDEX idx_interactions_contact_occurred_at
    ON interactions(contact_id, occurred_at DESC);

CREATE INDEX idx_follow_ups_interaction_id
    ON follow_ups(interaction_id);

CREATE INDEX idx_follow_ups_contact_status
    ON follow_ups(contact_id, status);

CREATE INDEX idx_follow_ups_status_due_at
    ON follow_ups(status, due_at);

CREATE UNIQUE INDEX uniq_contacts_user_email
    ON contacts(user_id, lower(email))
    WHERE email IS NOT NULL;

CREATE UNIQUE INDEX uniq_open_follow_up_per_contact
    ON follow_ups(contact_id)
    WHERE status = 'OPEN';

CREATE OR REPLACE FUNCTION set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_users_set_updated_at
BEFORE UPDATE ON users
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_companies_set_updated_at
BEFORE UPDATE ON companies
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_contacts_set_updated_at
BEFORE UPDATE ON contacts
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_interactions_set_updated_at
BEFORE UPDATE ON interactions
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_follow_ups_set_updated_at
BEFORE UPDATE ON follow_ups
FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

INSERT INTO users (email, display_name)
VALUES ('local@keep-in-touch.test', 'Local User');
