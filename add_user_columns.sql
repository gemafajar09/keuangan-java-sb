-- SQL script to add missing columns to users table
-- Run this if Hibernate auto-update doesn't work

-- Add is_online column
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS is_online BOOLEAN NOT NULL DEFAULT false;

-- Add last_login_at column
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS last_login_at TIMESTAMP;

-- Optional: Update existing users to set default values
UPDATE users 
SET is_online = false 
WHERE is_online IS NULL;

-- Verify the changes
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'users'
ORDER BY ordinal_position;
