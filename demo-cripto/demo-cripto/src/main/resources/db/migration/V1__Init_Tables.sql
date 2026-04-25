-- 1. Crear tabla de usuarios
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Crear catálogo de criptomonedas
CREATE TABLE cryptocurrencies (
    symbol VARCHAR(10) PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 3. Crear historial de precios
CREATE TABLE price_history (
    id BIGSERIAL PRIMARY KEY,
    crypto_symbol VARCHAR(10) REFERENCES cryptocurrencies(symbol),
    price NUMERIC(18, 8) NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Crear un índice para búsquedas rápidas en gráficas (por moneda y fecha)
CREATE INDEX idx_price_history_symbol_date ON price_history(crypto_symbol, recorded_at);

-- 4. Crear tabla de alertas
CREATE TABLE alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    crypto_symbol VARCHAR(10) REFERENCES cryptocurrencies(symbol),
    target_price NUMERIC(18, 8) NOT NULL,
    condition VARCHAR(10) CHECK (condition IN ('ABOVE', 'BELOW')) NOT NULL,
    status VARCHAR(15) CHECK (status IN ('ACTIVE', 'TRIGGERED', 'CANCELLED')) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);