-- TransitHub PostgreSQL Schema Initialization
-- This script creates all 11 schemas for the multi-service platform

CREATE SCHEMA IF NOT EXISTS auth_schema;
CREATE SCHEMA IF NOT EXISTS user_schema;
CREATE SCHEMA IF NOT EXISTS flight_schema;
CREATE SCHEMA IF NOT EXISTS bus_schema;
CREATE SCHEMA IF NOT EXISTS train_schema;
CREATE SCHEMA IF NOT EXISTS booking_schema;
CREATE SCHEMA IF NOT EXISTS payment_schema;
CREATE SCHEMA IF NOT EXISTS refund_schema;
CREATE SCHEMA IF NOT EXISTS document_schema;
CREATE SCHEMA IF NOT EXISTS fare_schema;
CREATE SCHEMA IF NOT EXISTS admin_schema;

-- ============================================================
-- AUTH SCHEMA
-- ============================================================

CREATE TABLE IF NOT EXISTS auth_schema.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20) UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    is_active BOOLEAN DEFAULT true,
    email_verified BOOLEAN DEFAULT false,
    phone_verified BOOLEAN DEFAULT false,
    role VARCHAR(50) DEFAULT 'ROLE_USER',
    last_login_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth_schema.refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES auth_schema.users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth_schema.otp_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(255),
    otp VARCHAR(6) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    verified BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS auth_schema.outbox_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    published BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON auth_schema.users(email);
CREATE INDEX IF NOT EXISTS idx_users_phone ON auth_schema.users(phone);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user ON auth_schema.refresh_tokens(user_id);
CREATE INDEX IF NOT EXISTS idx_outbox_published ON auth_schema.outbox_events(published) WHERE published = false;

-- ============================================================
-- USER SCHEMA
-- ============================================================

CREATE TABLE IF NOT EXISTS user_schema.profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    date_of_birth DATE,
    gender VARCHAR(20),
    loyalty_points INT DEFAULT 0,
    loyalty_tier VARCHAR(50) DEFAULT 'SILVER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_schema.saved_travellers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    id_type VARCHAR(50) NOT NULL,
    id_number VARCHAR(100) NOT NULL,
    is_primary BOOLEAN DEFAULT false,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_schema.wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID UNIQUE NOT NULL,
    balance NUMERIC(12,2) DEFAULT 0,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_schema.wallet_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    amount NUMERIC(12,2) NOT NULL,
    description TEXT,
    reference_id VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_travellers_user ON user_schema.saved_travellers(user_id);
CREATE INDEX IF NOT EXISTS idx_wallet_tx_user ON user_schema.wallet_transactions(user_id);

-- ============================================================
-- FLIGHT SCHEMA
-- ============================================================

CREATE TABLE IF NOT EXISTS flight_schema.airports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    iata_code VARCHAR(3) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    city VARCHAR(100) NOT NULL,
    country VARCHAR(100) NOT NULL,
    terminal VARCHAR(50),
    timezone VARCHAR(50) DEFAULT 'UTC',
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS flight_schema.airlines (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(2) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    logo_url VARCHAR(500),
    is_active BOOLEAN DEFAULT true
);

CREATE TABLE IF NOT EXISTS flight_schema.aircraft_types (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    model VARCHAR(100) NOT NULL,
    manufacturer VARCHAR(100),
    seat_config JSONB NOT NULL,
    total_seats INT NOT NULL
);

CREATE TABLE IF NOT EXISTS flight_schema.flights (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flight_number VARCHAR(10) NOT NULL,
    airline_id UUID NOT NULL REFERENCES flight_schema.airlines(id),
    origin_id UUID NOT NULL REFERENCES flight_schema.airports(id),
    destination_id UUID NOT NULL REFERENCES flight_schema.airports(id),
    departure_time TIME NOT NULL,
    arrival_time TIME NOT NULL,
    duration_minutes INT NOT NULL,
    aircraft_type_id UUID REFERENCES flight_schema.aircraft_types(id),
    days_of_week VARCHAR(10)[],
    is_active BOOLEAN DEFAULT true,
    is_codeshare BOOLEAN DEFAULT false,
    operating_airline_id UUID REFERENCES flight_schema.airlines(id),
    UNIQUE(flight_number)
);

CREATE TABLE IF NOT EXISTS flight_schema.flight_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flight_id UUID NOT NULL REFERENCES flight_schema.flights(id),
    schedule_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ON_TIME',
    delay_minutes INT DEFAULT 0,
    gate VARCHAR(10),
    available_economy INT NOT NULL,
    available_business INT NOT NULL,
    available_first INT NOT NULL,
    available_premium_economy INT NOT NULL DEFAULT 0,
    base_fare_economy NUMERIC(10,2) NOT NULL,
    base_fare_business NUMERIC(10,2),
    base_fare_first NUMERIC(10,2),
    base_fare_premium_economy NUMERIC(10,2),
    UNIQUE(flight_id, schedule_date)
);

CREATE TABLE IF NOT EXISTS flight_schema.flight_bookings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_ref VARCHAR(20) UNIQUE NOT NULL,
    pnr VARCHAR(10) UNIQUE,
    user_id UUID NOT NULL,
    flight_schedule_id UUID NOT NULL REFERENCES flight_schema.flight_schedules(id),
    cabin_class VARCHAR(20) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'INITIATED',
    total_fare NUMERIC(12,2) NOT NULL,
    gst_amount NUMERIC(10,2) DEFAULT 0,
    convenience_fee NUMERIC(10,2) DEFAULT 30.00,
    is_refundable BOOLEAN DEFAULT true,
    cancellation_fee NUMERIC(10,2) DEFAULT 0,
    booked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS flight_schema.flight_passengers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL REFERENCES flight_schema.flight_bookings(id),
    name VARCHAR(255) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(20) NOT NULL,
    id_type VARCHAR(50) NOT NULL,
    id_number VARCHAR(100) NOT NULL,
    seat_number VARCHAR(5),
    baggage_option VARCHAR(10) DEFAULT 'KG_15',
    meal_preference VARCHAR(50),
    is_checked_in BOOLEAN DEFAULT false
);

CREATE TABLE IF NOT EXISTS flight_schema.outbox_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_type VARCHAR(100) NOT NULL,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NU
