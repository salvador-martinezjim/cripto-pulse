-- 1. Borramos la regla (llave foránea) que apunta a la tabla vieja
ALTER TABLE alerts DROP CONSTRAINT alerts_crypto_symbol_fkey;

-- 2. Creamos la nueva regla apuntando a nuestra tabla moderna 'supported_cryptos'
ALTER TABLE alerts ADD CONSTRAINT alerts_crypto_symbol_fkey 
FOREIGN KEY (crypto_symbol) REFERENCES supported_cryptos(symbol);