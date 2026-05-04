INSERT INTO users (id, email)
VALUES ('11111111-1111-1111-1111-111111111111', 'default@criptopulse.com')
ON CONFLICT (email) DO NOTHING;