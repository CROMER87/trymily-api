-- Add slug column to tenants table
ALTER TABLE tenants ADD COLUMN slug VARCHAR(255);

-- Generate slugs from existing tenant names:
-- lowercase, replace spaces with hyphens, remove non-alphanumeric chars (except hyphens)
UPDATE tenants SET slug = LOWER(
    REGEXP_REPLACE(
        REGEXP_REPLACE(
            TRANSLATE(name, 'áàãâéèêíìîóòõôúùûçÁÀÃÂÉÈÊÍÌÎÓÒÕÔÚÙÛÇ', 'aaaaeeeiiioooouuucAAAAEEEIIIOOOOUUUC'),
            '[^a-zA-Z0-9\s-]', '', 'g'
        ),
        '\s+', '-', 'g'
    )
);

-- Ensure uniqueness: append id suffix for any duplicates
WITH duplicates AS (
    SELECT id, slug, ROW_NUMBER() OVER (PARTITION BY slug ORDER BY created_at) as rn
    FROM tenants
    WHERE slug IS NOT NULL
)
UPDATE tenants t
SET slug = d.slug || '-' || SUBSTRING(CAST(t.id AS VARCHAR), 1, 8)
FROM duplicates d
WHERE t.id = d.id AND d.rn > 1;

-- Make slug NOT NULL and UNIQUE
ALTER TABLE tenants ALTER COLUMN slug SET NOT NULL;
ALTER TABLE tenants ADD CONSTRAINT uk_tenants_slug UNIQUE (slug);
