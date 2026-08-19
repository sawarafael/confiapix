-- ID da conta Stone (account_id usado nos endpoints /api/v1/pix/{account_id}/...)
ALTER TABLE stone_credentials
    ADD COLUMN account_id VARCHAR(100);

UPDATE stone_credentials
SET account_id = merchant_id
WHERE account_id IS NULL AND merchant_id IS NOT NULL;
