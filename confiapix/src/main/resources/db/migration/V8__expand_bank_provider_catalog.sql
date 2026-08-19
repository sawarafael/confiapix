-- Permite integracao com qualquer instituicao financeira (codigo COMPE ou STONE)
ALTER TABLE bank_integrations DROP CONSTRAINT IF EXISTS chk_bank_integrations_provider;
ALTER TABLE pix_transactions DROP CONSTRAINT IF EXISTS chk_pix_provider;

-- Migra codigos legados para COMPE padronizado
UPDATE bank_integrations SET provider = '077' WHERE provider = 'INTER';
UPDATE bank_integrations SET provider = '336' WHERE provider = 'C6';

UPDATE pix_transactions SET provider = '077' WHERE provider = 'INTER';
UPDATE pix_transactions SET provider = '336' WHERE provider = 'C6';
