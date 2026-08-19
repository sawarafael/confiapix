package com.confiapix.domain.entity;

import com.confiapix.domain.valueobject.Money;
import com.confiapix.domain.valueobject.ReconciliationStatus;

import java.time.Instant;
import java.util.UUID;

public class Reconciliation {

    private UUID id;
    private UUID tenantId;
    private UUID pixTransactionId;
    private UUID receivableId;
    private Money expectedAmount;
    private Money receivedAmount;
    private ReconciliationStatus status;
    private Instant reconciledAt;
    private String notes;
    private Instant createdAt;
    private Instant updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public void setTenantId(UUID tenantId) {
        this.tenantId = tenantId;
    }

    public UUID getPixTransactionId() {
        return pixTransactionId;
    }

    public void setPixTransactionId(UUID pixTransactionId) {
        this.pixTransactionId = pixTransactionId;
    }

    public UUID getReceivableId() {
        return receivableId;
    }

    public void setReceivableId(UUID receivableId) {
        this.receivableId = receivableId;
    }

    public Money getExpectedAmount() {
        return expectedAmount;
    }

    public void setExpectedAmount(Money expectedAmount) {
        this.expectedAmount = expectedAmount;
    }

    public Money getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(Money receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public ReconciliationStatus getStatus() {
        return status;
    }

    public void setStatus(ReconciliationStatus status) {
        this.status = status;
    }

    public Instant getReconciledAt() {
        return reconciledAt;
    }

    public void setReconciledAt(Instant reconciledAt) {
        this.reconciledAt = reconciledAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
