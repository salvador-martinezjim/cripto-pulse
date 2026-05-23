CREATE TABLE supported_cryptos (
    id BIGSERIAL PRIMARY KEY,
    symbol VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(50) NOT NULL
);

INSERT INTO supported_cryptos (symbol, name) VALUES 
('bitcoin', 'Bitcoin'),
('ethereum', 'Ethereum'),
('solana', 'Solana'),
('cardano', 'Cardano'),
('dogecoin', 'Dogecoin');