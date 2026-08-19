package com.confiapix.application.usecase;

import com.confiapix.domain.exception.ResourceNotFoundException;
import com.confiapix.infrastructure.persistence.entity.Company;
import com.confiapix.application.usecase.CompanyUseCase;
import com.confiapix.presentation.request.SupplierRequest;
import com.confiapix.presentation.response.SupplierResponse;
import com.confiapix.infrastructure.persistence.entity.Supplier;
import com.confiapix.application.mapper.SupplierMapper;
import com.confiapix.infrastructure.persistence.repository.SupplierRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SupplierUseCase {

    private final SupplierRepository supplierRepository;
    private final CompanyUseCase companyService;
    private final SupplierMapper supplierMapper;

    @Transactional(readOnly = true)
    public Page<SupplierResponse> findAll(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return supplierRepository.findByTenantId(tenantId, pageable)
                .map(supplierMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public SupplierResponse findById(UUID id) {
        return supplierMapper.toResponse(getSupplierOrThrow(id));
    }

    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        Supplier supplier = supplierMapper.toEntity(request);
        supplier.setCompany(company);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public SupplierResponse update(UUID id, SupplierRequest request) {
        Supplier supplier = getSupplierOrThrow(id);
        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        supplierMapper.updateEntity(request, supplier);
        supplier.setCompany(company);
        return supplierMapper.toResponse(supplierRepository.save(supplier));
    }

    @Transactional
    public void delete(UUID id) {
        Supplier supplier = getSupplierOrThrow(id);
        supplierRepository.delete(supplier);
    }

    public Supplier getSupplierOrThrow(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return supplierRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Fornecedor", id));
    }
}
