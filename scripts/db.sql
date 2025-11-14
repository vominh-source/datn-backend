-- Run in psql
-- Example: psql -U your_user -d postgres -f full_setup.sql

-- Step 1 & 2: Terminate connections and Drop database
-- (Connect to 'postgres' or another maintenance DB to run this)
-- Or just simple
SELECT pg_terminate_backend(pid)
FROM pg_stat_activity
WHERE datname = 'doca' AND pid <> pg_backend_pid();

DROP DATABASE IF EXISTS doca;

-- Step 3: Create the database
CREATE DATABASE doca;

-- Step 4: RECONNECT to the new 'doca' database
-- THIS IS THE PSQL-ONLY COMMAND. It tells the psql
-- client to drop the old connection and open a new one.
\c doca

-- ALL COMMANDS BELOW THIS LINE ARE EXECUTED
-- WITHIN THE 'doca' DATABASE

-- Step 5: Enable pgcrypto extension
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Step 6: Create enum type
DO
$$
BEGIN
   IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'userrole') THEN
       CREATE TYPE UserRole AS ENUM ('ADMIN', 'USER', 'GUEST');
   END IF;
END
$$;

-- Step 7: Create users table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password_hash TEXT NOT NULL,
    email VARCHAR(255) UNIQUE,
    role UserRole DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT now()
);

-- Step 8: Create conversation table
CREATE TABLE IF NOT EXISTS conversation (
    id SERIAL PRIMARY KEY,
    sender_user_id INT NOT NULL REFERENCES users(id),
    receiver_user_id INT NOT NULL REFERENCES users(id),
    created_at TIMESTAMP DEFAULT now(),
    CONSTRAINT unique_receiver_sender UNIQUE (receiver_user_id, sender_user_id)
);

-- Step 9: Create message table
CREATE TABLE IF NOT EXISTS message (
    id SERIAL PRIMARY KEY,
    id_conversation INT REFERENCES conversation(id),
    content TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT now(),
    read_at TIMESTAMP
);

-- Step 10: Create index
CREATE INDEX IF NOT EXISTS idx_message_sent_at ON message(sent_at);