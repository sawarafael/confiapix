package com.confiapix.application.usecase;

import com.confiapix.application.mapper.NotificationMapper;
import com.confiapix.domain.valueobject.NotificationType;
import com.confiapix.infrastructure.persistence.entity.Notification;
import com.confiapix.infrastructure.persistence.repository.NotificationRepository;
import com.confiapix.infrastructure.tenant.TenantContext;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationUseCaseTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @InjectMocks
    private NotificationUseCase notificationUseCase;

    private UUID tenantId;

    @BeforeEach
    void setUp() {
        tenantId = UUID.randomUUID();
        TenantContextHolder.set(new TenantContext(tenantId, UUID.randomUUID(), "test@confiapix.test"));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldListNotificationsByTenant() {
        Notification entity = Notification.builder()
                .tenantId(tenantId)
                .type(NotificationType.PIX_RECEIVED)
                .title("PIX recebido")
                .message("Teste")
                .build();

        when(notificationRepository.findByTenantIdOrderByCreatedAtDesc(tenantId, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(entity)));
        when(notificationMapper.toResponse(entity)).thenReturn(
                com.confiapix.presentation.response.NotificationResponse.builder()
                        .title("PIX recebido")
                        .build());

        var page = notificationUseCase.list(PageRequest.of(0, 10));

        assertThat(page.getContent()).hasSize(1);
    }

    @Test
    void shouldMarkAllAsRead() {
        when(notificationRepository.markAllAsRead(tenantId)).thenReturn(2);
        when(notificationRepository.countByTenantIdAndReadFalse(tenantId)).thenReturn(0L);

        var result = notificationUseCase.markAllAsRead();

        assertThat(result.getCount()).isZero();
        verify(notificationRepository).markAllAsRead(tenantId);
    }
}
