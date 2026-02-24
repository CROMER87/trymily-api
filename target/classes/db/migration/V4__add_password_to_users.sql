-- Add password column to users table for LOCAL provider support
ALTER TABLE users ADD COLUMN password VARCHAR(255);

-- Update audit table
ALTER TABLE users_aud ADD COLUMN password VARCHAR(255);
ALTER TABLE users_aud ADD COLUMN password_mod BOOLEAN;
