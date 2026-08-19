ALTER TABLE tenants
    ADD COLUMN platform_operator BOOLEAN NOT NULL DEFAULT FALSE;

-- Bootstrap: primeiro tenant existente vira operador da plataforma (ambiente dev)
UPDATE tenants
SET platform_operator = TRUE
WHERE id = (SELECT id FROM tenants ORDER BY created_at ASC LIMIT 1)
  AND NOT EXISTS (SELECT 1 FROM tenants WHERE platform_operator = TRUE);
