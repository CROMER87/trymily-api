-- Extend tenants table with profile info and JSONB settings
ALTER TABLE tenants ADD COLUMN address TEXT;
ALTER TABLE tenants ADD COLUMN phone VARCHAR(20);
ALTER TABLE tenants ADD COLUMN logo_url TEXT;
ALTER TABLE tenants ADD COLUMN settings JSONB DEFAULT '{}'::jsonb;

-- Update audit table
ALTER TABLE tenants_aud ADD COLUMN address TEXT;
ALTER TABLE tenants_aud ADD COLUMN address_mod BOOLEAN;
ALTER TABLE tenants_aud ADD COLUMN phone VARCHAR(20);
ALTER TABLE tenants_aud ADD COLUMN phone_mod BOOLEAN;
ALTER TABLE tenants_aud ADD COLUMN logo_url TEXT;
ALTER TABLE tenants_aud ADD COLUMN logo_url_mod BOOLEAN;
ALTER TABLE tenants_aud ADD COLUMN settings JSONB;
ALTER TABLE tenants_aud ADD COLUMN settings_mod BOOLEAN;

-- Create GIN index for faster settings lookups if needed
CREATE INDEX idx_tenants_settings ON tenants USING GIN (settings);
