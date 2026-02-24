-- Table: users
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(255),
    picture_url TEXT,
    provider VARCHAR(50) NOT NULL DEFAULT 'LOCAL',
    provider_id VARCHAR(255) UNIQUE,
    role VARCHAR(50) NOT NULL DEFAULT 'ROLE_CUSTOMER',
    tenant_id UUID REFERENCES tenants(id),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Enable RLS (Users are special, they might need to be global or restricted)
ALTER TABLE users ENABLE ROW LEVEL SECURITY;
ALTER TABLE users FORCE ROW LEVEL SECURITY;

-- Policy for users:
-- 1. Admins/Staff only see users within their tenant.
-- 2. Global Customers (no tenant_id) are handled by application logic (or a specific policy).
-- For now, let's allow access if tenant_id matches or if it's the user's own record.
CREATE POLICY user_isolation_policy ON users
    USING (
        tenant_id = current_setting('app.current_tenant', true)::uuid 
        OR (current_setting('app.current_tenant', true) = '')
    );

-- Hibernate Envers Audit Table for users
CREATE TABLE users_aud (
    id UUID NOT NULL,
    rev INTEGER NOT NULL REFERENCES revinfo(rev),
    revtype SMALLINT,
    email VARCHAR(255),
    email_mod BOOLEAN,
    full_name VARCHAR(255),
    full_name_mod BOOLEAN,
    picture_url TEXT,
    picture_url_mod BOOLEAN,
    provider VARCHAR(50),
    provider_mod BOOLEAN,
    provider_id VARCHAR(255),
    provider_id_mod BOOLEAN,
    role VARCHAR(50),
    role_mod BOOLEAN,
    tenant_id UUID,
    tenant_id_mod BOOLEAN,
    PRIMARY KEY (id, rev)
);

-- Index for performance
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_provider_id ON users(provider_id);
CREATE INDEX idx_users_tenant_id ON users(tenant_id);
