package com.confiapix.infrastructure.persistence.entity;

import com.confiapix.infrastructure.persistence.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "tenants")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Tenant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String plan = "FREE";

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(name = "platform_operator", nullable = false)
    @Builder.Default
    private boolean platformOperator = false;
}
