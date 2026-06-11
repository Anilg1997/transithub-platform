-- Flyway migration: V1__create_tables.sql
-- Schema: inventory_lock_schema
-- This migration creates the base tables for the inventory-lock-service service.
-- Tables are already created by init-schemas.sql, this ensures Flyway tracking.

-- Note: Schema creation is handled by Docker init script.
-- This migration ensures Flyway has a baseline to work with.

SELECT 1;
