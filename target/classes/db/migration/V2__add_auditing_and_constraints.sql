CREATE EXTENSION IF NOT EXISTS btree_gist;

-- Prevent overlapping appointments for the same tenant
-- This ensures that a salon cannot have two appointments at the same time for the same resource (simplified here to just the tenant/salon)
ALTER TABLE appointments ADD CONSTRAINT no_overlap_appointments
EXCLUDE USING gist (
    tenant_id WITH =,
    tstzrange(start_time, end_time) WITH &&
);

-- Hibernate Envers Audit Tables
CREATE TABLE revinfo (
    rev INTEGER PRIMARY KEY,
    revtstmp BIGINT
);

CREATE SEQUENCE revinfo_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE tenants_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    name VARCHAR(255),
    name_mod BOOLEAN,
    status VARCHAR(50),
    status_mod BOOLEAN,
    PRIMARY KEY (id, rev)
);

CREATE TABLE appointments_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    customer_name VARCHAR(255),
    customer_name_mod BOOLEAN,
    start_time TIMESTAMP WITH TIME ZONE,
    start_time_mod BOOLEAN,
    end_time TIMESTAMP WITH TIME ZONE,
    end_time_mod BOOLEAN,
    status VARCHAR(50),
    status_mod BOOLEAN,
    tenant_id UUID,
    tenant_id_mod BOOLEAN,
    PRIMARY KEY (id, rev)
);
