package com.confiapix.application.usecase;

import com.confiapix.application.mapper.UserProfileMapper;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.infrastructure.persistence.entity.User;
import com.confiapix.infrastructure.persistence.repository.UserRepository;
import com.confiapix.infrastructure.security.JwtService;
import com.confiapix.infrastructure.security.RefreshTokenService;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.request.ChangePasswordRequest;
import com.confiapix.presentation.request.UpdateProfileRequest;
import com.confiapix.presentation.response.UpdateProfileResponse;
import com.confiapix.presentation.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserProfileUseCase {

    private final UserRepository userRepository;
    private final UserProfileMapper userProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentProfile() {
        return userProfileMapper.toResponse(findCurrentUser());
    }

    @Transactional
    public UpdateProfileResponse updateProfile(UpdateProfileRequest request) {
        User user = findCurrentUser();
        String normalizedEmail = request.getEmail().trim().toLowerCase();
        boolean emailChanged = !user.getEmail().equalsIgnoreCase(normalizedEmail);

        if (emailChanged && userRepository.existsByEmailAndIdNot(normalizedEmail, user.getId())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        user.setName(request.getName().trim());
        user.setEmail(normalizedEmail);
        userRepository.save(user);

        UserProfileResponse profile = userProfileMapper.toResponse(user);
        UpdateProfileResponse.UpdateProfileResponseBuilder response = UpdateProfileResponse.builder()
                .profile(profile);

        if (emailChanged) {
            String token = jwtService.generateToken(
                    user.getId(),
                    user.getTenant().getId(),
                    user.getEmail(),
                    user.getRole());
            String refreshToken = refreshTokenService.issueRefreshToken(user.getId());
            response.token(token)
                    .refreshToken(refreshToken)
                    .expiresIn(jwtService.getExpirationMs());
        }

        return response.build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = findCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Senha atual incorreta");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("A nova senha deve ser diferente da senha atual");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private User findCurrentUser() {
        UUID userId = TenantContextHolder.getUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }
}
