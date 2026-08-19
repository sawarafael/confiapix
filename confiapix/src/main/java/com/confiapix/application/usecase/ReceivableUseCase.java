package com.confiapix.application.usecase;

import com.confiapix.domain.valueobject.AccountStatus;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.exception.ResourceNotFoundException;
import com.confiapix.domain.service.AccountStatusHelper;
import com.confiapix.infrastructure.persistence.entity.Company;
import com.confiapix.application.usecase.CompanyUseCase;
import com.confiapix.infrastructure.persistence.entity.Customer;
import com.confiapix.application.usecase.CustomerUseCase;
import com.confiapix.presentation.request.ReceivableRequest;
import com.confiapix.presentation.response.ReceivableResponse;
import com.confiapix.infrastructure.persistence.entity.AccountReceivable;
import com.confiapix.application.mapper.ReceivableMapper;
import com.confiapix.infrastructure.persistence.repository.AccountReceivableRepository;
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
public class ReceivableUseCase {

    private final AccountReceivableRepository receivableRepository;
    private final CompanyUseCase companyService;
    private final CustomerUseCase customerService;
    private final ReceivableMapper receivableMapper;

    @Transactional(readOnly = true)
    public Page<ReceivableResponse> findAll(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return receivableRepository.findByTenantId(tenantId, pageable)
                .map(this::toResponseWithResolvedStatus);
    }

    @Transactional(readOnly = true)
    public ReceivableResponse findById(UUID id) {
        return toResponseWithResolvedStatus(getReceivableOrThrow(id));
    }

    @Transactional
    public ReceivableResponse create(ReceivableRequest request) {
        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        Customer customer = customerService.getCustomerOrThrow(request.getCustomerId());
        validateSameCompany(company, customer.getCompany().getId());

        AccountReceivable receivable = receivableMapper.toEntity(request);
        receivable.setCompany(company);
        receivable.setCustomer(customer);
        receivable.setStatus(AccountStatusHelper.resolveStatus(AccountStatus.PENDING, request.getDueDate()));

        return toResponseWithResolvedStatus(receivableRepository.save(receivable));
    }

    @Transactional
    public ReceivableResponse update(UUID id, ReceivableRequest request) {
        AccountReceivable receivable = getReceivableOrThrow(id);
        ensureEditable(receivable);

        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        Customer customer = customerService.getCustomerOrThrow(request.getCustomerId());
        validateSameCompany(company, customer.getCompany().getId());

        receivableMapper.updateEntity(request, receivable);
        receivable.setCompany(company);
        receivable.setCustomer(customer);
        receivable.setStatus(AccountStatusHelper.resolveStatus(receivable.getStatus(), request.getDueDate()));

        return toResponseWithResolvedStatus(receivableRepository.save(receivable));
    }

    @Transactional
    public ReceivableResponse pay(UUID id) {
        AccountReceivable receivable = getReceivableOrThrow(id);
        if (receivable.getStatus() == AccountStatus.PAID) {
            throw new BusinessException("Conta a receber já está paga");
        }
        if (receivable.getStatus() == AccountStatus.CANCELED) {
            throw new BusinessException("Conta a receber cancelada não pode ser paga");
        }

        receivable.setStatus(AccountStatus.PAID);
        receivable.setPaymentDate(LocalDate.now());
        return toResponseWithResolvedStatus(receivableRepository.save(receivable));
    }

    @Transactional
    public void delete(UUID id) {
        AccountReceivable receivable = getReceivableOrThrow(id);
        receivableRepository.delete(receivable);
    }

    public AccountReceivable getReceivableOrThrow(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return receivableRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta a receber", id));
    }

    private ReceivableResponse toResponseWithResolvedStatus(AccountReceivable receivable) {
        receivable.setStatus(AccountStatusHelper.resolveStatus(receivable.getStatus(), receivable.getDueDate()));
        return receivableMapper.toResponse(receivable);
    }

    private void ensureEditable(AccountReceivable receivable) {
        if (receivable.getStatus() == AccountStatus.PAID) {
            throw new BusinessException("Conta a receber paga não pode ser alterada");
        }
    }

    private void validateSameCompany(Company company, UUID customerCompanyId) {
        if (!company.getId().equals(customerCompanyId)) {
            throw new BusinessException("Cliente deve pertencer à mesma empresa informada");
        }
    }
}
