-- V6: Create Services Table with RLS Support
CREATE TABLE services (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    duration_minutes INTEGER NOT NULL DEFAULT 30,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    version INTEGER DEFAULT 0,

    CONSTRAINT fk_services_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id) ON DELETE CASCADE
);

-- Enable Row Level Security
ALTER TABLE services ENABLE ROW LEVEL SECURITY;

-- Create Tenant Isolation Policy for Services
CREATE POLICY tenant_isolation_policy ON services
    USING (tenant_id = (SELECT current_setting('app.current_tenant')::uuid));

-- Index for performance
CREATE INDEX idx_services_tenant_id ON services(tenant_id);

-- Auditing (Envers) table
CREATE TABLE services_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL,
    revtype SMALLINT,
    tenant_id UUID,
    name VARCHAR(255),
    description TEXT,
    price NUMERIC(12, 2),
    duration_minutes INTEGER,
    status VARCHAR(50),
    PRIMARY KEY (id, rev),
    CONSTRAINT fk_services_aud_revinfo FOREIGN KEY (rev) REFERENCES revinfo(rev)
);
