-- Seed PIX + conciliacoes de demonstracao (Empresa Demo LTDA)
-- Idempotente: remove registros DEMO-* antes de reinserir

DO $$
DECLARE
    v_tenant_id UUID;
BEGIN
    SELECT u.tenant_id INTO v_tenant_id
    FROM users u
    WHERE u.email = 'admin@empresa.demo'
    LIMIT 1;

    IF v_tenant_id IS NULL THEN
        RAISE EXCEPTION 'Tenant demo nao encontrado. Execute o seed de contas primeiro.';
    END IF;

    DELETE FROM reconciliations r
    WHERE r.tenant_id = v_tenant_id
      AND r.pix_transaction_id IN (
          SELECT p.id FROM pix_transactions p
          WHERE p.tenant_id = v_tenant_id AND p.txid LIKE 'DEMO-%'
      );

    DELETE FROM pix_transactions p
    WHERE p.tenant_id = v_tenant_id
      AND p.txid LIKE 'DEMO-%';

    INSERT INTO pix_transactions (
        id, tenant_id, txid, end_to_end_id, amount,
        payer_name, payer_document, received_at, source, provider,
        created_at, updated_at
    ) VALUES
        ('a1000001-0000-4000-8000-000000000001', v_tenant_id, 'DEMO-TX-001', 'E123456782024081012345678901201', 250.00,  'Maria Souza',       '52998224725',    NOW() - INTERVAL '6 days',  'WEBHOOK', 'STONE', NOW() - INTERVAL '6 days',  NOW() - INTERVAL '6 days'),
        ('a1000001-0000-4000-8000-000000000002', v_tenant_id, 'DEMO-TX-002', 'E123456782024081012345678901202',  89.90,  'Carlos Pereira',    '39053344705',    NOW() - INTERVAL '5 days',  'SYNC',    'STONE', NOW() - INTERVAL '5 days',  NOW() - INTERVAL '5 days'),
        ('a1000001-0000-4000-8000-000000000003', v_tenant_id, 'DEMO-TX-003', 'E123456782024081012345678901203', 1200.00, 'Loja Virtual XYZ',  '11222333000181', NOW() - INTERVAL '4 days',  'WEBHOOK', 'STONE', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days'),
        ('a1000001-0000-4000-8000-000000000004', v_tenant_id, 'DEMO-TX-004', 'E123456782024081012345678901204', 450.00,  'Ana Paula Lima',    '12345678909',    NOW() - INTERVAL '3 days',  'STONE',   'STONE', NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days'),
        ('a1000001-0000-4000-8000-000000000005', v_tenant_id, 'DEMO-TX-005', 'E123456782024081012345678901205',  75.50,  'Pedro Henrique',    '98765432100',    NOW() - INTERVAL '2 days',  'WEBHOOK', 'STONE', NOW() - INTERVAL '2 days',  NOW() - INTERVAL '2 days'),
        ('a1000001-0000-4000-8000-000000000006', v_tenant_id, 'DEMO-TX-006', 'E123456782024081012345678901206', 320.00,  'Juliana Martins',   '45678912345',    NOW() - INTERVAL '1 day',   'SYNC',    'STONE', NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day'),
        ('a1000001-0000-4000-8000-000000000007', v_tenant_id, 'DEMO-TX-007', 'E123456782024081012345678901207', 999.99,  'Tech Solutions ME', '55666777888999', NOW() - INTERVAL '8 hours', 'WEBHOOK', 'STONE', NOW() - INTERVAL '8 hours', NOW() - INTERVAL '8 hours'),
        ('a1000001-0000-4000-8000-000000000008', v_tenant_id, 'DEMO-TX-008', 'E123456782024081012345678901208', 180.00,  'Fernando Alves',    '32165498700',    NOW() - INTERVAL '2 hours',  'STONE',   '077', NOW() - INTERVAL '2 hours',  NOW() - INTERVAL '2 hours');

    INSERT INTO reconciliations (
        id, tenant_id, pix_transaction_id,
        expected_amount, received_amount, status,
        reconciled_at, notes, created_at, updated_at
    ) VALUES
        ('b2000001-0000-4000-8000-000000000001', v_tenant_id, 'a1000001-0000-4000-8000-000000000001', 250.00,  250.00,  'MATCHED',   NOW() - INTERVAL '6 days'  + INTERVAL '5 minutes', NULL,                                          NOW() - INTERVAL '6 days',  NOW() - INTERVAL '6 days'),
        ('b2000001-0000-4000-8000-000000000002', v_tenant_id, 'a1000001-0000-4000-8000-000000000002',  89.90,   89.90,  'MATCHED',   NOW() - INTERVAL '5 days'  + INTERVAL '3 minutes', NULL,                                          NOW() - INTERVAL '5 days',  NOW() - INTERVAL '5 days'),
        ('b2000001-0000-4000-8000-000000000003', v_tenant_id, 'a1000001-0000-4000-8000-000000000003', 1150.00, 1200.00, 'DIVERGENT', NOW() - INTERVAL '4 days'  + INTERVAL '10 minutes', 'Valor recebido diverge do titulo em R$ 50,00', NOW() - INTERVAL '4 days',  NOW() - INTERVAL '4 days'),
        ('b2000001-0000-4000-8000-000000000004', v_tenant_id, 'a1000001-0000-4000-8000-000000000004', 450.00,  450.00,  'PENDING',   NULL,                                                          'Aguardando confirmacao bancaria',              NOW() - INTERVAL '3 days',  NOW() - INTERVAL '3 days'),
        ('b2000001-0000-4000-8000-000000000006', v_tenant_id, 'a1000001-0000-4000-8000-000000000006', 320.00,  320.00,  'MATCHED',   NOW() - INTERVAL '1 day'   + INTERVAL '2 minutes', NULL,                                          NOW() - INTERVAL '1 day',   NOW() - INTERVAL '1 day'),
        ('b2000001-0000-4000-8000-000000000007', v_tenant_id, 'a1000001-0000-4000-8000-000000000007', 1000.00, 999.99,  'DIVERGENT', NOW() - INTERVAL '8 hours' + INTERVAL '4 minutes', 'Valor recebido diverge do titulo em R$ 0,01',  NOW() - INTERVAL '8 hours', NOW() - INTERVAL '8 hours'),
        ('b2000001-0000-4000-8000-000000000008', v_tenant_id, 'a1000001-0000-4000-8000-000000000008', 180.00,  180.00,  'PENDING',   NULL,                                                          'Conciliacao manual pendente (Inter)',          NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours');

    RAISE NOTICE 'Seed PIX demo: 8 transacoes, 7 conciliacoes (tenant %)', v_tenant_id;
END $$;
