package com.confiapix.application.usecase;

import com.confiapix.domain.valueobject.AccountStatus;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.exception.ResourceNotFoundException;
import com.confiapix.domain.service.AccountStatusHelper;
import com.confiapix.infrastructure.persistence.entity.Company;
import com.confiapix.application.usecase.CompanyUseCase;
import com.confiapix.presentation.request.PayableRequest;
import com.confiapix.presentation.response.PayableResponse;
import com.confiapix.infrastructure.persistence.entity.AccountPayable;
import com.confiapix.application.mapper.PayableMapper;
import com.confiapix.infrastructure.persistence.repository.AccountPayableRepository;
import com.confiapix.infrastructure.persistence.entity.Supplier;
import com.confiapix.application.usecase.SupplierUseCase;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayableUseCase {

    private final AccountPayableRepository payableRepository;
    private final CompanyUseCase companyService;
    private final SupplierUseCase supplierService;
    private final PayableMapper payableMapper;

    @Transactional(readOnly = true)
    public Page<PayableResponse> findAll(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return payableRepository.findByTenantId(tenantId, pageable)
                .map(this::toResponseWithResolvedStatus);
    }

    @Transactional(readOnly = true)
    public PayableResponse findById(UUID id) {
        return toResponseWithResolvedStatus(getPayableOrThrow(id));
    }

    @Transactional
    public PayableResponse create(PayableRequest request) {
        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        Supplier supplier = supplierService.getSupplierOrThrow(request.getSupplierId());
        validateSameCompany(company, supplier.getCompany().getId());

        AccountPayable payable = payableMapper.toEntity(request);
        payable.setCompany(company);
        payable.setSupplier(supplier);
        payable.setStatus(AccountStatusHelper.resolveStatus(AccountStatus.PENDING, request.getDueDate()));

        return toResponseWithResolvedStatus(payableRepository.save(payable));
    }

    @Transactional
    public PayableResponse update(UUID id, PayableRequest request) {
        AccountPayable payable = getPayableOrThrow(id);
        ensureEditable(payable);

        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        Supplier supplier = supplierService.getSupplierOrThrow(request.getSupplierId());
        validateSameCompany(company, supplier.getCompany().getId());

        payableMapper.updateEntity(request, payable);
        payable.setCompany(company);
        payable.setSupplier(supplier);
        payable.setStatus(AccountStatusHelper.resolveStatus(payable.getStatus(), request.getDueDate()));

        return toResponseWithResolvedStatus(payableRepository.save(payable));
    }

    @Transactional
    public PayableResponse pay(UUID id) {
        AccountPayable payable = getPayableOrThrow(id);
        if (payable.getStatus() == AccountStatus.PAID) {
            throw new BusinessException("Conta a pagar já está paga");
        }
        if (payable.getStatus() == AccountStatus.CANCELED) {
            throw new BusinessException("Conta a pagar cancelada não pode ser paga");
        }

        payable.setStatus(AccountStatus.PAID);
        payable.setPaymentDate(LocalDate.now());
        return toResponseWithResolvedStatus(payableRepository.save(payable));
    }

    @Transactional
    public void delete(UUID id) {
        AccountPayable payable = getPayableOrThrow(id);
        payableRepository.delete(payable);
    }

    public AccountPayable getPayableOrThrow(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return payableRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a pagar", id));
    }

    private PayableResponse toResponseWithResolvedStatus(AccountPayable payable) {
        payable.setStatus(AccountStatusHelper.resolveStatus(payable.getStatus(), payable.getDueDate()));
        return payableMapper.toResponse(payable);
    }

    private void ensureEditable(AccountPayable payable) {
        if (payable.getStatus() == AccountStatus.PAID) {
            throw new BusinessException("Conta a pagar paga não pode ser alterada");
        }
    }

    private void validateSameCompany(Company company, UUID supplierCompanyId) {
        if (!company.getId().equals(supplierCompanyId)) {
            throw new BusinessException("Fornecedor deve pertencer à mesma empresa informada");
        }
    }
}
