CREATE TABLE notifications (
    id             UUID PRIMARY KEY,
    tenant_id      UUID          NOT NULL REFERENCES tenants (id) ON DELETE CASCADE,
    type           VARCHAR(50)   NOT NULL,
    title          VARCHAR(255)  NOT NULL,
    message        VARCHAR(1000) NOT NULL,
    is_read        BOOLEAN       NOT NULL DEFAULT FALSE,
    reference_id   UUID,
    reference_type VARCHAR(50),
    created_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_notification_type CHECK (type IN (
        'PIX_RECEIVED',
        'RECONCILIATION_MATCHED',
        'RECONCILIATION_DIVERGENT',
        'RECONCILIATION_PENDING',
        'STONE_SYNC'
    ))
);

CREATE INDEX idx_notifications_tenant_id ON notifications (tenant_id);
CREATE INDEX idx_notifications_tenant_created ON notifications (tenant_id, created_at DESC);
CREATE INDEX idx_notifications_tenant_unread ON notifications (tenant_id, is_read) WHERE is_read = FALSE;
