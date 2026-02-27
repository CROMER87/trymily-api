-- Add new columns to the appointments table to store service and establishment details

DO $$ 
    BEGIN
        BEGIN
            ALTER TABLE appointments ADD COLUMN establishment_name VARCHAR(255) NOT NULL DEFAULT 'N/A';
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column establishment_name already exists in appointments.';
        END;

        BEGIN
            ALTER TABLE appointments ADD COLUMN service_id UUID;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column service_id already exists in appointments.';
        END;

        BEGIN
            ALTER TABLE appointments ADD COLUMN service_name VARCHAR(255) NOT NULL DEFAULT 'N/A';
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column service_name already exists in appointments.';
        END;

        BEGIN
            ALTER TABLE appointments ADD COLUMN service_price DOUBLE PRECISION;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column service_price already exists in appointments.';
        END;

        BEGIN
            ALTER TABLE appointments ADD COLUMN service_duration INTEGER;
        EXCEPTION
            WHEN duplicate_column THEN RAISE NOTICE 'column service_duration already exists in appointments.';
        END;
    END;
$$;
