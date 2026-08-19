ALTER TABLE stone_credentials
    ADD COLUMN auth_mode VARCHAR(30) NOT NULL DEFAULT 'OPEN_BANKING';

ALTER TABLE stone_credentials
    ADD COLUMN business_model VARCHAR(30) DEFAULT 'GATEWAY';

ALTER TABLE stone_credentials
    ALTER COLUMN client_id DROP NOT NULL;

ALTER TABLE stone_credentials
    ADD CONSTRAINT chk_stone_auth_mode CHECK (auth_mode IN ('OPEN_BANKING', 'API_KEY'));

ALTER TABLE stone_credentials
    ADD CONSTRAINT chk_stone_business_model CHECK (business_model IS NULL OR business_model IN ('GATEWAY', 'SUBACQUIRER'));
