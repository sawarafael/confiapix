package com.confiapix.application.usecase;

import com.confiapix.domain.exception.ResourceNotFoundException;
import com.confiapix.infrastructure.persistence.entity.Company;
import com.confiapix.application.usecase.CompanyUseCase;
import com.confiapix.presentation.request.CustomerRequest;
import com.confiapix.presentation.response.CustomerResponse;
import com.confiapix.infrastructure.persistence.entity.Customer;
import com.confiapix.application.mapper.CustomerMapper;
import com.confiapix.infrastructure.persistence.repository.CustomerRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CompanyUseCase companyService;
    private final CustomerMapper customerMapper;

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return customerRepository.findByTenantId(tenantId, pageable)
                .map(customerMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(UUID id) {
        return customerMapper.toResponse(getCustomerOrThrow(id));
    }

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        Customer customer = customerMapper.toEntity(request);
        customer.setCompany(company);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse update(UUID id, CustomerRequest request) {
        Customer customer = getCustomerOrThrow(id);
        Company company = companyService.getCompanyOrThrow(request.getCompanyId());
        customerMapper.updateEntity(request, customer);
        customer.setCompany(company);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Transactional
    public void delete(UUID id) {
        Customer customer = getCustomerOrThrow(id);
        customerRepository.delete(customer);
    }

    public Customer getCustomerOrThrow(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return customerRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
    }
}
