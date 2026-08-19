package com.confiapix.application.usecase;

import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.UserRole;
import com.confiapix.infrastructure.persistence.entity.Tenant;
import com.confiapix.infrastructure.persistence.entity.User;
import com.confiapix.infrastructure.persistence.repository.TenantRepository;
import com.confiapix.infrastructure.persistence.repository.UserRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.request.CreateTenantAccessRequest;
import com.confiapix.presentation.request.UpdateTenantAccessRequest;
import com.confiapix.presentation.response.TenantAccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantAccessUseCase {

    private final TenantRepository tenantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<TenantAccessResponse> listCustomerTenants() {
        assertPlatformOperatorAdmin();

        return tenantRepository.findAll().stream()
                .filter(t -> !t.isPlatformOperator())
                .sorted(Comparator.comparing(Tenant::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public TenantAccessResponse create(CreateTenantAccessRequest request) {
        assertPlatformOperatorAdmin();

        if (userRepository.existsByEmail(request.getAdminEmail())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name(request.getTenantName())
                .plan(request.getPlan())
                .active(true)
                .platformOperator(false)
                .build());

        userRepository.save(User.builder()
                .name(request.getAdminName())
                .email(request.getAdminEmail())
                .password(passwordEncoder.encode(request.getAdminPassword()))
                .active(true)
                .role(UserRole.ADMIN)
                .tenant(tenant)
                .build());

        return toResponse(tenant);
    }

    @Transactional
    public TenantAccessResponse update(UUID tenantId, UpdateTenantAccessRequest request) {
        assertPlatformOperatorAdmin();

        Tenant tenant = getCustomerTenantOrThrow(tenantId);
        tenant.setName(request.getTenantName());
        tenant.setPlan(request.getPlan());
        tenant.setActive(request.isActive());

        return toResponse(tenantRepository.save(tenant));
    }

    private Tenant getCustomerTenantOrThrow(UUID tenantId) {
        Tenant tenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new BusinessException("Empresa não encontrada"));

        if (tenant.isPlatformOperator()) {
            throw new BusinessException("Operador da plataforma não pode ser alterado por aqui");
        }

        return tenant;
    }

    private void assertPlatformOperatorAdmin() {
        UUID tenantId = TenantContextHolder.getTenantId();
        Tenant operatorTenant = tenantRepository.findById(tenantId)
                .orElseThrow(() -> new AccessDeniedException("Tenant inválido"));

        if (!operatorTenant.isPlatformOperator()) {
            throw new AccessDeniedException("Apenas operador da plataforma pode gerenciar acessos");
        }

        User currentUser = userRepository.findById(TenantContextHolder.getUserId())
                .orElseThrow(() -> new AccessDeniedException("Usuário inválido"));

        if (currentUser.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Apenas administradores podem gerenciar acessos");
        }
    }

    private TenantAccessResponse toResponse(Tenant tenant) {
        User admin = userRepository.findFirstByTenantIdAndRole(tenant.getId(), UserRole.ADMIN)
                .orElse(null);

        return TenantAccessResponse.builder()
                .id(tenant.getId())
                .name(tenant.getName())
                .plan(tenant.getPlan())
                .active(tenant.isActive())
                .adminEmail(admin != null ? admin.getEmail() : null)
                .adminName(admin != null ? admin.getName() : null)
                .createdAt(tenant.getCreatedAt())
                .build();
    }
}
