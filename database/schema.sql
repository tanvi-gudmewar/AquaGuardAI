CREATE DATABASE IF NOT EXISTS aquaguard
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE aquaguard;

-- Spring Data JPA creates the application tables on startup when ddl-auto=update.
-- This file intentionally creates only the database; entity definitions are the source
-- of truth for the starter schema. Do not use ddl-auto=update as a production migration strategy.
