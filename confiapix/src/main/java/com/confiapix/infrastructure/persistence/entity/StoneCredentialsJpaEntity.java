package com.confiapix.infrastructure.persistence.entity;

import com.confiapix.infrastructure.integration.stone.model.StoneAuthMode;
import com.confiapix.infrastructure.integration.stone.model.StoneBusinessModel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "stone_credentials")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class StoneCredentialsJpaEntity extends BaseEntity {

    @Column(name = "tenant_id", nullable = false, unique = true)
    private UUID tenantId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_secret_encrypted", nullable = false, length = 512)
    private String clientSecretEncrypted;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_mode", nullable = false)
    @lombok.Builder.Default
    private StoneAuthMode authMode = StoneAuthMode.OPEN_BANKING;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_model")
    @lombok.Builder.Default
    private StoneBusinessModel businessModel = StoneBusinessModel.GATEWAY;

    @Column(name = "account_id", length = 100)
    private String accountId;

    @Column(name = "merchant_id", length = 100)
    private String merchantId;

    @Column(nullable = false)
    @lombok.Builder.Default
    private boolean active = true;
}
