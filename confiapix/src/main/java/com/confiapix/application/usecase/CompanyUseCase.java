package com.confiapix.application.usecase;

import com.confiapix.domain.exception.ResourceNotFoundException;
import com.confiapix.presentation.request.CompanyRequest;
import com.confiapix.presentation.response.CompanyResponse;
import com.confiapix.infrastructure.persistence.entity.Company;
import com.confiapix.application.mapper.CompanyMapper;
import com.confiapix.infrastructure.persistence.repository.CompanyRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.infrastructure.persistence.entity.Tenant;
import com.confiapix.infrastructure.persistence.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompanyUseCase {

    private final CompanyRepository companyRepository;
    private final TenantRepository tenantRepository;
    private final CompanyMapper companyMapper;

    @Transactional(readOnly = true)
    public Page<CompanyResponse> findAll(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return companyRepository.findByTenantId(tenantId, pageable)
                .map(companyMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CompanyResponse findById(UUID id) {
        return companyMapper.toResponse(getCompanyOrThrow(id));
    }

    @Transactional
    public CompanyResponse create(CompanyRequest request) {
        UUID tenantId = TenantContextHolder.getTenantId();
        Tenant tenant = tenantRepository.getReferenceById(tenantId);

        Company company = companyMapper.toEntity(request);
        company.setTenant(tenant);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Transactional
    public CompanyResponse update(UUID id, CompanyRequest request) {
        Company company = getCompanyOrThrow(id);
        companyMapper.updateEntity(request, company);
        return companyMapper.toResponse(companyRepository.save(company));
    }

    @Transactional
    public void delete(UUID id) {
        Company company = getCompanyOrThrow(id);
        companyRepository.delete(company);
    }

    public Company getCompanyOrThrow(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return companyRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa", id));
    }
}
