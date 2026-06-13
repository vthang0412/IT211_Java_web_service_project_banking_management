package com.rikkei.bank.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class ValidImageFileValidator implements ConstraintValidator<ValidImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) {
            return true;
        }

        String contentType = file.getContentType();

        return contentType != null &&
                (contentType.equalsIgnoreCase("image/png") ||
                        contentType.equalsIgnoreCase("image/jpg") ||
                        contentType.equalsIgnoreCase("image/jpeg"));
    }
}
