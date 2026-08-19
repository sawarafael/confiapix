package com.confiapix.application.usecase;

import com.confiapix.presentation.response.AuthResponse;
import com.confiapix.presentation.request.LoginRequest;
import com.confiapix.presentation.request.RefreshTokenRequest;
import com.confiapix.presentation.request.RegisterRequest;
import com.confiapix.infrastructure.persistence.entity.User;
import com.confiapix.infrastructure.persistence.repository.UserRepository;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.UserRole;
import com.confiapix.infrastructure.security.RefreshTokenService;
import com.confiapix.infrastructure.security.JwtService;
import com.confiapix.infrastructure.persistence.entity.Tenant;
import com.confiapix.infrastructure.persistence.repository.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUseCase {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("E-mail já cadastrado");
        }

        Tenant tenant = tenantRepository.save(Tenant.builder()
                .name(request.getTenantName())
                .plan("FREE")
                .active(true)
                .build());

        User user = userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .active(true)
                .role(UserRole.ADMIN)
                .tenant(tenant)
                .build());

        return buildAuthResponse(user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (!user.isActive()) {
            throw new BusinessException("Usuário inativo");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        UUID userId = refreshTokenService.validateAndConsume(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        if (!user.isActive()) {
            throw new BusinessException("Usuário inativo");
        }

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(
                user.getId(),
                user.getTenant().getId(),
                user.getEmail(),
                user.getRole());
        String refreshToken = refreshTokenService.issueRefreshToken(user.getId());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(jwtService.getExpirationMs())
                .userId(user.getId())
                .tenantId(user.getTenant().getId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .platformOperator(user.getTenant().isPlatformOperator())
                .build();
    }
}
