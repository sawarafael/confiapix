package com.confiapix.application.usecase;

import com.confiapix.application.mapper.UserProfileMapper;
import com.confiapix.domain.exception.BusinessException;
import com.confiapix.domain.valueobject.UserRole;
import com.confiapix.infrastructure.persistence.entity.Tenant;
import com.confiapix.infrastructure.persistence.entity.User;
import com.confiapix.infrastructure.persistence.repository.UserRepository;
import com.confiapix.infrastructure.security.JwtService;
import com.confiapix.infrastructure.security.RefreshTokenService;
import com.confiapix.infrastructure.tenant.TenantContext;
import com.confiapix.infrastructure.tenant.TenantContextHolder;
import com.confiapix.presentation.request.ChangePasswordRequest;
import com.confiapix.presentation.request.UpdateProfileRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileMapper userProfileMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private UserProfileUseCase userProfileUseCase;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        Tenant tenant = Tenant.builder()
                .id(UUID.randomUUID())
                .name("Empresa Teste")
                .plan("FREE")
                .active(true)
                .platformOperator(false)
                .build();

        user = User.builder()
                .id(userId)
                .name("João Silva")
                .email("joao@teste.com")
                .password("encoded-old")
                .active(true)
                .role(UserRole.ADMIN)
                .tenant(tenant)
                .build();

        TenantContextHolder.set(new TenantContext(tenant.getId(), userId, user.getEmail()));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void shouldGetCurrentProfile() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userProfileMapper.toResponse(user)).thenReturn(
                com.confiapix.presentation.response.UserProfileResponse.builder()
                        .name("João Silva")
                        .email("joao@teste.com")
                        .build());

        var profile = userProfileUseCase.getCurrentProfile();

        assertThat(profile.getName()).isEqualTo("João Silva");
        assertThat(profile.getEmail()).isEqualTo("joao@teste.com");
    }

    @Test
    void shouldUpdateProfileWithoutReissuingTokenWhenEmailUnchanged() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("João Atualizado");
        request.setEmail("joao@teste.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userProfileMapper.toResponse(user)).thenReturn(
                com.confiapix.presentation.response.UserProfileResponse.builder()
                        .name("João Atualizado")
                        .email("joao@teste.com")
                        .build());

        var response = userProfileUseCase.updateProfile(request);

        assertThat(response.getProfile().getName()).isEqualTo("João Atualizado");
        assertThat(response.getToken()).isNull();
        assertThat(user.getName()).isEqualTo("João Atualizado");
    }

    @Test
    void shouldReissueTokenWhenEmailChanges() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("João Silva");
        request.setEmail("novo@teste.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("novo@teste.com", userId)).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(jwtService.generateToken(userId, user.getTenant().getId(), "novo@teste.com", UserRole.ADMIN))
                .thenReturn("new-token");
        when(refreshTokenService.issueRefreshToken(userId)).thenReturn("new-refresh");
        when(jwtService.getExpirationMs()).thenReturn(3600000L);
        when(userProfileMapper.toResponse(user)).thenReturn(
                com.confiapix.presentation.response.UserProfileResponse.builder()
                        .email("novo@teste.com")
                        .build());

        var response = userProfileUseCase.updateProfile(request);

        assertThat(response.getToken()).isEqualTo("new-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh");
        assertThat(user.getEmail()).isEqualTo("novo@teste.com");
    }

    @Test
    void shouldRejectDuplicateEmailOnUpdate() {
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setName("João Silva");
        request.setEmail("outro@teste.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailAndIdNot("outro@teste.com", userId)).thenReturn(true);

        assertThatThrownBy(() -> userProfileUseCase.updateProfile(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("E-mail já cadastrado");
    }

    @Test
    void shouldChangePassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("senha-atual");
        request.setNewPassword("nova-senha");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("senha-atual", "encoded-old")).thenReturn(true);
        when(passwordEncoder.matches("nova-senha", "encoded-old")).thenReturn(false);
        when(passwordEncoder.encode("nova-senha")).thenReturn("encoded-new");
        when(userRepository.save(user)).thenReturn(user);

        userProfileUseCase.changePassword(request);

        assertThat(user.getPassword()).isEqualTo("encoded-new");
        verify(userRepository).save(user);
    }

    @Test
    void shouldRejectWrongCurrentPassword() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setCurrentPassword("errada");
        request.setNewPassword("nova-senha");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq("errada"), any())).thenReturn(false);

        assertThatThrownBy(() -> userProfileUseCase.changePassword(request))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Senha atual incorreta");
    }
}
