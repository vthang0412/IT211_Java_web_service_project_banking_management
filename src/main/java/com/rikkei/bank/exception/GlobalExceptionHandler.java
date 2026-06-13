package com.rikkei.bank.exception;

import com.rikkei.bank.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            ResourceNotFoundException.class
    )
    public ResponseEntity<ErrorResponse> handleNotFound(

            ResourceNotFoundException ex,

            HttpServletRequest request
    ) {

        return ResponseEntity.status(
                        HttpStatus.NOT_FOUND
                )
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(404)
                                .error("Not Found")
                                .message(
                                        ex.getMessage()
                                )
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            UnauthorizedException.class
    )
    public ResponseEntity<ErrorResponse> handleUnauthorized(

            UnauthorizedException ex,

            HttpServletRequest request
    ) {

        return ResponseEntity.status(
                        HttpStatus.UNAUTHORIZED
                )
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(401)
                                .error("Unauthorized")
                                .message(
                                        ex.getMessage()
                                )
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        ErrorResponse.builder()
                                .timestamp(LocalDateTime.now().toString())
                                .status(409)
                                .error("Conflict")
                                .message(ex.getMessage())
                                .errors(List.of(ex.getMessage()))
                                .path(request.getRequestURI())
                                .build()
                );
    }

    @ExceptionHandler(
            InsufficientBalanceException.class
    )
    public ResponseEntity<ErrorResponse> handleBalance(

            InsufficientBalanceException ex,

            HttpServletRequest request
    ) {

        return ResponseEntity.status(
                        HttpStatus.CONFLICT
                )
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(409)
                                .error("Conflict")
                                .message(
                                        ex.getMessage()
                                )
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(

            Exception ex,

            HttpServletRequest request
    ) {

        return ResponseEntity.status(
                        HttpStatus.INTERNAL_SERVER_ERROR
                )
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(500)
                                .error("Internal Server Error")
                                .message(
                                        ex.getMessage()
                                )
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(400)
                                .error("Bad Request")
                                .message(ex.getMessage())
                                .errors(
                                        List.of(ex.getMessage())
                                )
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<ErrorResponse>
    handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        return buildValidationResponse(
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError ->
                                fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        )
                        .toList(),
                request
        );
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(
            BindException ex,
            HttpServletRequest request
    ) {

        return buildValidationResponse(
                ex.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError ->
                                fieldError.getField() + ": " + fieldError.getDefaultMessage()
                        )
                        .toList(),
                request
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestParameter(
            MissingServletRequestParameterException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .timestamp(LocalDateTime.now().toString())
                                .status(400)
                                .error("Bad Request")
                                .message("Missing required request parameter")
                                .errors(
                                        java.util.List.of(
                                                ex.getParameterName() + " is required"
                                        )
                                )
                                .path(request.getRequestURI())
                                .build()
                );
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestPart(
            MissingServletRequestPartException ex,
            HttpServletRequest request
    ) {

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .timestamp(LocalDateTime.now().toString())
                                .status(400)
                                .error("Bad Request")
                                .message("Missing required request part")
                                .errors(
                                        java.util.List.of(
                                                ex.getRequestPartName() + " is required"
                                        )
                                )
                                .path(request.getRequestURI())
                                .build()
                );
    }

    @ExceptionHandler(
            ConstraintViolationException.class
    )
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {

        List<String> errors =
                ex.getConstraintViolations()
                        .stream()
                        .map(violation ->
                                violation.getPropertyPath() + ": " + violation.getMessage()
                        )
                        .toList();

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(400)
                                .error("Bad Request")
                                .message("Validation failed")
                                .errors(errors)
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }

    private ResponseEntity<ErrorResponse> buildValidationResponse(
            List<String> errors,
            HttpServletRequest request
    ) {

        return ResponseEntity.badRequest()
                .body(
                        ErrorResponse.builder()
                                .timestamp(
                                        LocalDateTime.now()
                                                .toString()
                                )
                                .status(400)
                                .error("Bad Request")
                                .message("Validation failed")
                                .errors(errors)
                                .path(
                                        request.getRequestURI()
                                )
                                .build()
                );
    }
}
