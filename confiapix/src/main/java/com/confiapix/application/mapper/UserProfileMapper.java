package com.confiapix.application.mapper;

import com.confiapix.infrastructure.persistence.entity.User;
import com.confiapix.presentation.response.UserProfileResponse;
import org.springframework.stereotype.Component;

@Component
public class UserProfileMapper {

    public UserProfileResponse toResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .tenantId(user.getTenant().getId())
                .tenantName(user.getTenant().getName())
                .plan(user.getTenant().getPlan())
                .platformOperator(user.getTenant().isPlatformOperator())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
