-- Version: V1.0.0

-- Project
CREATE TABLE IF NOT EXISTS public.projects (
id                          SERIAL              NOT NULL PRIMARY KEY,
title                       varchar(255)        NULL,
production_year             varchar(255)        NULL,
country                     varchar(255)        DEFAULT 'de',
aka                         TEXT                NULL,
orig_vers                   varchar(255)        NULL,
vers_info                   varchar(255)        NULL,
production                  TEXT                NULL,
regie                       TEXT                NULL,
incharge                    varchar(255)        NULL,
incharge_sec                varchar(255)        NULL,
book_author                 varchar(255)        NULL,
co_author                   varchar(255)        NULL,
premiere_date               varchar(255)        NULL,
cinema_start_date           varchar(255)        NULL,
tv_start_date               varchar(255)        NULL,
synopsis_de                 TEXT                NULL,
festival_info               TEXT                NULL,
awards                      TEXT                NULL,
status                      varchar(255)        NULL,
minutes                     varchar(255)        NULL,
meters                      varchar(255)        NULL,
formats                     varchar(255)        NULL,
movieid                     varchar(255)        NULL,
vod_link                    varchar(255)        NULL,
screener_link               varchar(255)        NULL,
trailer_link                varchar(255)        NULL,
web_keywords                varchar(255)        NULL,
isan                        varchar(255)        NULL,
imdb                        varchar(255)        NULL,
eingaben_foerderer          TEXT                NULL,
kopien                      TEXT                NULL,
tonstudio                   TEXT                NULL,
mischung                    varchar(255)        NULL,
weitere_tonbearbeitung      TEXT                NULL,
videotechnik                varchar(255)        NULL,
schnittassi                 varchar(255)        NULL,
schnitt                     varchar(255)        NULL,
weitere_bildbearbeitung     TEXT                NULL,
stereodolby                 varchar(255)        NULL,
format_dreh                 TEXT                NULL,
format_schnitt              TEXT                NULL,
auswertung                  TEXT                NULL,
filmformat                  varchar(255)        NULL,
bildformat                  varchar(255)        NULL,
labor                       TEXT                NULL,
tonsystem                   varchar(255)        NULL,
negmont                     TEXT                NULL,
created_at                  TIMESTAMPTZ         NULL DEFAULT NOW(),
updated_at                  TIMESTAMPTZ         NULL DEFAULT NOW(),
deleted_at                  TIMESTAMPTZ         NULL DEFAULT NOW()
);
CREATE INDEX index_projects_id ON projects (id);

-- Contact
CREATE TABLE IF NOT EXISTS public.contacts (
id                          SERIAL              NOT NULL PRIMARY KEY,
ahv_number                  TEXT                NULL,
nationality                 TEXT                NULL,
withprefix                  TEXT                NULL,
titels                      TEXT                NULL,
prefix                      TEXT                NULL,
name                        TEXT                NULL,
firstname                   TEXT                NULL,
private_address_street      TEXT                NULL,
private_address_postcode    TEXT                NULL,
private_address_city        TEXT                NULL,
private_address_country     TEXT                NULL,
companyposition             TEXT                NULL,
postcheck_account           TEXT                NULL,
bank                        TEXT                NULL,
contact_notes               TEXT                NULL,
phone                       TEXT                NULL,
profession                  TEXT                NULL,
birthdate                   TEXT                NULL,
bankaccount                 TEXT                NULL,
phone_company               TEXT                NULL,
phone_central               TEXT                NULL,
fax                         TEXT                NULL,
email1                      TEXT                NULL,
email2                      TEXT                NULL,
company_is                  TEXT                NULL,
created_at                  TIMESTAMPTZ         NULL,
updated_at                  TIMESTAMPTZ         NULL,
deleted_at                  TIMESTAMPTZ         NULL
);
CREATE INDEX index_contacts_id ON contacts (id);

--Company
CREATE TABLE IF NOT EXISTS public.companies (
id                          SERIAL        NOT NULL PRIMARY KEY,
company_name                TEXT          NULL,
company_mail                varchar(255)  DEFAULT NULL,
company_phone               varchar(255)  DEFAULT NULL,
company_phone2              TEXT          NULL,
company_phone3              varchar(255)  DEFAULT NULL,
company_phone4              varchar(255)  DEFAULT NULL,
company_phone5              varchar(255)  DEFAULT NULL,
company_street              varchar(255)  DEFAULT NULL,
company_city                varchar(255)  DEFAULT NULL,
company_postcode            varchar(255)  DEFAULT NULL,
company_state               varchar(255)  DEFAULT NULL,
company_postbox             varchar(255)  DEFAULT NULL,
company_country             varchar(255)  DEFAULT NULL,
company_website             varchar(255)  DEFAULT NULL,
company_facebook            varchar(255)  DEFAULT NULL,
company_fax                 varchar(255)  DEFAULT NULL,
company_durchwahl           varchar(255)  DEFAULT NULL,
company_instagram           varchar(255)  DEFAULT NULL,
company_twitter             varchar(255)  DEFAULT NULL,
firmenadresscat             TEXT          DEFAULT NULL,
company_notes               TEXT          NULL,
created_at                  TIMESTAMPTZ   NULL,
updated_at                  TIMESTAMPTZ   NULL,
deleted_at                  TIMESTAMPTZ   NULL
);
CREATE INDEX index_companies_id ON companies (id);


-- User
CREATE TABLE IF NOT EXISTS public.users
(
    user_id                  SERIAL        NOT NULL PRIMARY KEY,
    user_fname               VARCHAR(150)  NULL,
    user_lname               VARCHAR(150)  NULL,
    user_email               VARCHAR(150)  NOT NULL,
    user_pass                VARCHAR(100)  NOT NULL,
    user_salt                VARCHAR(200)  NULL,
    user_mobile              VARCHAR(50)   NULL,
    user_roles               TEXT          NULL,
    is_Active                BOOLEAN       NULL,
    is_verified              BOOLEAN       NULL,
    valid_until              TIMESTAMPTZ   NULL,
    last_login               TIMESTAMPTZ   NULL,
    password_requested_at    TIMESTAMPTZ   NULL,
    created_at               TIMESTAMPTZ   NULL DEFAULT NOW(),
    updated_at               TIMESTAMPTZ   NULL DEFAULT NOW()
);
CREATE INDEX index_user_id ON users (user_id);


-- Create the user_roles table
CREATE TABLE IF NOT EXISTS public.user_roles
(
    role_id           SERIAL PRIMARY KEY,
    role_name         VARCHAR(150) NOT NULL,
    role_description  VARCHAR(255),
    role_permission   JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at        TIMESTAMPTZ DEFAULT NOW(),
    updated_at        TIMESTAMPTZ DEFAULT NOW()
);

-- Create an index for faster lookups
CREATE INDEX IF NOT EXISTS index_role_id ON public.user_roles (role_id);

-- Insert a default Administrator role (only if it doesn't already exist)
INSERT INTO public.user_roles (role_name, role_description, role_permission)
SELECT
    'Administrator',
    'Has full access to all system features and settings.',
    '{
        "Dashboard": {"read": true, "write": true, "option": "all"},
        "Project": {"read": true, "write": true, "option": "all"},
        "Contact": {"read": true, "write": true, "option": "all"},
        "Firm": {"read": true, "write": true, "option": "all"},
        "User": {"read": true, "write": true, "option": "all"},
        "Role": {"read": true, "write": true, "option": "all"},
        "Setting": {"read": true, "write": true, "option": "all"}
    }'::jsonb
WHERE NOT EXISTS (
    SELECT 1 FROM public.user_roles WHERE role_name = 'Administrator'
);