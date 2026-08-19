package com.confiapix.presentation.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateProfileResponse {

    private UserProfileResponse profile;
    private String token;
    private String refreshToken;
    private Long expiresIn;
}
