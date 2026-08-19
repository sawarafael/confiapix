package com.confiapix.application.usecase;

import com.confiapix.application.mapper.NotificationMapper;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.infrastructure.persistence.repository.NotificationRepository;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.response.NotificationResponse;
import com.confiapix.presentation.response.UnreadCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationUseCase {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> list(Pageable pageable) {
        UUID tenantId = TenantContextHolder.getTenantId();
        return notificationRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, pageable)
                .map(notificationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UnreadCountResponse unreadCount() {
        UUID tenantId = TenantContextHolder.getTenantId();
        return UnreadCountResponse.builder()
                .count(notificationRepository.countByTenantIdAndReadFalse(tenantId))
                .build();
    }

    @Transactional
    public NotificationResponse markAsRead(UUID id) {
        UUID tenantId = TenantContextHolder.getTenantId();
        var notification = notificationRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new BusinessException("Notificação não encontrada"));

        if (!notification.isRead()) {
            notification.setRead(true);
            notification = notificationRepository.save(notification);
        }

        return notificationMapper.toResponse(notification);
    }

    @Transactional
    public UnreadCountResponse markAllAsRead() {
        UUID tenantId = TenantContextHolder.getTenantId();
        notificationRepository.markAllAsRead(tenantId);
        return unreadCount();
    }
}
