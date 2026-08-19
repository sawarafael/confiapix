package com.confiapix.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "bank_integrations")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class BankIntegrationJpaEntity extends BaseEntity {

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false, length = 30)
    private String provider;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret_encrypted", nullable = false, length = 512)
    private String clientSecretEncrypted;

    @Column(name = "account_ref", length = 100)
    private String accountRef;

    @Column(name = "merchant_ref", length = 100)
    private String merchantRef;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json")
    private String configJson;

    @Column(nullable = false)
    @lombok.Builder.Default
    private boolean active = true;
}
