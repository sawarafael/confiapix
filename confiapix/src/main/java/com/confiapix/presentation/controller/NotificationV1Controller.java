package com.confiapix.presentation.controller;

import com.confiapix.application.usecase.NotificationUseCase;
import com.confiapix.presentation.response.ApiResponse;
import com.confiapix.presentation.response.NotificationResponse;
import com.confiapix.presentation.response.PageResponse;
import com.confiapix.presentation.response.UnreadCountResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Notificações recentes do tenant")
@SecurityRequirement(name = "bearerAuth")
public class NotificationV1Controller {

    private final NotificationUseCase notificationUseCase;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Listar últimas notificações")
    public ApiResponse<PageResponse<NotificationResponse>> list(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ApiResponse.ok(PageResponse.from(notificationUseCase.list(pageable)));
    }

    @GetMapping("/unread-count")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Contagem de notificações não lidas")
    public ApiResponse<UnreadCountResponse> unreadCount() {
        return ApiResponse.ok(notificationUseCase.unreadCount());
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Marcar notificação como lida")
    public ApiResponse<NotificationResponse> markAsRead(@PathVariable UUID id) {
        return ApiResponse.ok(notificationUseCase.markAsRead(id));
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAnyRole('ADMIN','FINANCIAL','VIEWER')")
    @Operation(summary = "Marcar todas as notificações como lidas")
    public ApiResponse<UnreadCountResponse> markAllAsRead() {
        return ApiResponse.ok(notificationUseCase.markAllAsRead());
    }
}
