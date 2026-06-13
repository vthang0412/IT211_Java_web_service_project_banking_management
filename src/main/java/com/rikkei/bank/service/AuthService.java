package com.rikkei.bank.service;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rikkei.bank.dto.auth.ForgotPasswordRequest;
import com.rikkei.bank.dto.auth.LoginRequest;
import com.rikkei.bank.dto.auth.LoginResponse;
import com.rikkei.bank.dto.auth.RefreshTokenRequest;
import com.rikkei.bank.dto.auth.RefreshTokenResponse;
import com.rikkei.bank.dto.auth.RegisterRequest;
import com.rikkei.bank.dto.auth.RegisterResponse;
import com.rikkei.bank.dto.auth.ResetPasswordRequest;
import com.rikkei.bank.dto.kyc.KycUploadResponse;
import com.rikkei.bank.entity.OtpVerification;
import com.rikkei.bank.entity.RefreshToken;
import com.rikkei.bank.entity.User;
import com.rikkei.bank.entity.Role;
import com.rikkei.bank.exception.ConflictException;
import com.rikkei.bank.exception.ResourceNotFoundException;
import com.rikkei.bank.exception.UnauthorizedException;
import com.rikkei.bank.repository.OtpVerificationRepository;
import com.rikkei.bank.repository.RefreshTokenRepository;
import com.rikkei.bank.service.TokenBlacklistService;
import com.rikkei.bank.repository.RoleRepository;
import com.rikkei.bank.repository.UserRepository;
import com.rikkei.bank.security.JwtProvider;
import com.rikkei.bank.security.UserPrincipal;
import com.rikkei.bank.service.KycService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final TokenBlacklistService tokenBlacklistService;

    private final OtpVerificationRepository otpRepository;

    private final EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    private final RoleRepository roleRepository;

    private final KycService kycService;

    private final Validator validator;

    public LoginResponse login(
            LoginRequest request
    ) {

        validateRequest(request);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getUsername(),
                                request.getPassword()
                        )
                );

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        String accessToken =
                jwtProvider.generateAccessToken(
                        principal
                );

        String refreshToken =
                jwtProvider.generateRefreshToken(
                        principal
                );

        User user =
                userRepository.findByUsername(
                        principal.getUsername()
                ).orElseThrow();

        // If a refresh token already exists for this user, update it instead of inserting a new row
        refreshTokenRepository.findByUser(user).ifPresentOrElse(existing -> {
            existing.setToken(refreshToken);
            existing.setExpiryDate(Instant.now().plusSeconds(86400));
            existing.setRevoked(false);
            refreshTokenRepository.save(existing);
        }, () -> {
            RefreshToken token = RefreshToken.builder()
                    .token(refreshToken)
                    .expiryDate(Instant.now().plusSeconds(86400))
                    .revoked(false)
                    .user(user)
                    .build();

            refreshTokenRepository.save(token);
        });

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public RefreshTokenResponse refreshToken(
            RefreshTokenRequest request
    ) {

        validateRequest(request);

        RefreshToken refreshToken =
                refreshTokenRepository
                        .findByToken(
                                request.getRefreshToken()
                        )
                        .orElseThrow(
                                () -> new UnauthorizedException(
                                        "Refresh token invalid"
                                )
                        );

        if (Boolean.TRUE.equals(
                refreshToken.getRevoked()
        )) {

            throw new UnauthorizedException(
                    "Refresh token revoked"
            );
        }

        if (
                refreshToken.getExpiryDate()
                        .isBefore(
                                Instant.now()
                        )
        ) {

            throw new UnauthorizedException(
                    "Refresh token expired"
            );
        }

        User user =
                refreshToken.getUser();

        UserPrincipal principal =
                UserPrincipal.create(user);

        String newAccessToken =
                jwtProvider.generateAccessToken(
                        principal
                );

        return RefreshTokenResponse
                .builder()
                .accessToken(newAccessToken)
                .build();
    }
    public void logout(
            HttpServletRequest request
    ) {

        String header =
                request.getHeader(
                        "Authorization"
                );

        if (
                header == null ||
                        !header.startsWith(
                                "Bearer "
                        )
        ) {

            throw new UnauthorizedException(
                    "Token missing"
            );
        }

        String token =
                header.substring(7);

        // Blacklist token in Redis with TTL equal to access token expiration
        long ttlSeconds = jwtProvider.getAccessExpiration() / 1000;
        tokenBlacklistService.blacklistToken(token, ttlSeconds);
    }
    public void forgotPassword(
            ForgotPasswordRequest request
    ) {

        validateRequest(request);

        User user =
                userRepository.findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Email not found"
                                        )
                        );

        String otp =
                String.valueOf(
                        (int)(
                                Math.random() * 900000
                        ) + 100000
                );

        OtpVerification entity =
                OtpVerification.builder()
                        .email(user.getEmail())
                        .otp(otp)
                        .used(false)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .expiredAt(
                                LocalDateTime.now()
                                        .plusMinutes(5)
                        )
                        .build();

        otpRepository.save(entity);

        try {
            emailService.sendOtp(
                    user.getEmail(),
                    otp
            );
        } catch (Exception ex) {
            log.error("Failed to send OTP email to {}: {}", user.getEmail(), ex.getMessage(), ex);

        }
    }

    public void resetPassword(
            ResetPasswordRequest request
    ) {

        validateRequest(request);

        OtpVerification otp =
                otpRepository.findByOtp(
                                request.getOtp()
                        )
                        .orElseThrow(
                                () ->
                                        new UnauthorizedException(
                                                "OTP invalid"
                                        )
                        );

        if(Boolean.TRUE.equals(
                otp.getUsed()
        )) {

            throw new UnauthorizedException(
                    "OTP used"
            );
        }

        if(
                otp.getExpiredAt()
                        .isBefore(
                                LocalDateTime.now()
                        )
        ) {

            throw new UnauthorizedException(
                    "OTP expired"
            );
        }

        User user =
                userRepository.findByEmail(
                                otp.getEmail()
                        )
                        .orElseThrow();

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        otp.setUsed(true);

        otpRepository.save(otp);
    }

    @Transactional
    public RegisterResponse register(
            RegisterRequest request
    ) {

        validateRequest(request);

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new ConflictException("Phone number already exists");
        }

        Role customerRole =
                roleRepository.findByName("CUSTOMER")
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Customer role not found")
                        );

        User user =
                userRepository.save(
                        User.builder()
                                .username(request.getUsername())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .email(request.getEmail())
                                .phoneNumber(request.getPhoneNumber())
                                .role(customerRole)
                                .isActive(false)
                                .isKyc(false)
                                .build()
                );

        KycUploadResponse kyc =
                kycService.uploadKyc(
                        user.getId(),
                        request.getFile(),
                        request.getFullName(),
                        request.getIdNumber()
                );

        return RegisterResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .kycId(kyc.getId())
                .kycStatus(kyc.getStatus())
                .message("Registration submitted successfully. Awaiting KYC approval.")
                .build();
    }

    private void validateRequest(Object request) {
        Set<ConstraintViolation<Object>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
