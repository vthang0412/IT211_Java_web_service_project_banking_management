package com.rikkei.bank.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rikkei.bank.dto.kyc.KycUploadRequest;
import com.rikkei.bank.dto.kyc.KycUploadResponse;
import com.rikkei.bank.entity.KycProfile;
import com.rikkei.bank.entity.User;
import com.rikkei.bank.entity.enums.KycStatus;
import com.rikkei.bank.exception.ResourceNotFoundException;
import com.rikkei.bank.repository.KycProfileRepository;
import com.rikkei.bank.repository.UserRepository;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class KycService {

    private final KycProfileRepository kycProfileRepository;

    private final UserRepository userRepository;

    private final CloudStorageService cloudStorageService;

    private final Validator validator;

    public KycUploadResponse uploadKyc(

            Long userId,

            MultipartFile file,

            String fullName,

            String idNumber
    ) {

        validateUserId(userId);
        KycUploadRequest request = new KycUploadRequest();
        request.setFile(file);
        request.setFullName(fullName);
        request.setIdNumber(idNumber);
        validateRequest(request);

        User user =
                userRepository.findById(userId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "User not found"
                                        )
                        );

        String imageUrl =
                cloudStorageService.upload(
                        file
                );

        // If a KYC profile already exists for this user, update it instead of inserting a new record
        java.util.Optional<KycProfile> existingOpt = kycProfileRepository.findByUser(user);

        KycProfile profile;

        if (existingOpt.isPresent()) {
            profile = existingOpt.get();
            profile.setFullName(fullName);
            profile.setIdNumber(idNumber);
            profile.setIdCardFrontUrl(imageUrl);
            profile.setStatus(KycStatus.PENDING);
            profile.setCreatedAt(LocalDateTime.now());
            kycProfileRepository.save(profile);
        } else {
            profile = KycProfile.builder()
                    .fullName(fullName)
                    .idNumber(idNumber)
                    .idCardFrontUrl(imageUrl)
                    .status(KycStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .user(user)
                    .build();

            kycProfileRepository.save(profile);
        }

        return KycUploadResponse.builder()
                .id(profile.getId())
                .fullName(profile.getFullName())
                .idNumber(profile.getIdNumber())
                .imageUrl(profile.getIdCardFrontUrl())
                .status(profile.getStatus().name())
                .build();
    }

    public void approveKyc(
            Long kycId
    ) {

        validateUserId(kycId);

        KycProfile profile =
                kycProfileRepository
                        .findById(kycId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "KYC not found"
                                        )
                        );

        profile.setStatus(
                KycStatus.CONFIRM
        );

        profile.setVerifiedAt(
                LocalDateTime.now()
        );

        User user =
                profile.getUser();

        user.setIsKyc(true);
        user.setIsActive(true);

        userRepository.save(user);

        kycProfileRepository.save(profile);
    }

    public void rejectKyc(
            Long kycId
    ) {

        validateUserId(kycId);

        KycProfile profile =
                kycProfileRepository
                        .findById(kycId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "KYC not found"
                                        )
                        );

        profile.setStatus(
                KycStatus.REJECT
        );

        kycProfileRepository.save(
                profile
        );
    }

    private void validateUserId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("KYC ID must be greater than 0");
        }
    }

    private void validateRequest(KycUploadRequest request) {
        Set<ConstraintViolation<KycUploadRequest>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
