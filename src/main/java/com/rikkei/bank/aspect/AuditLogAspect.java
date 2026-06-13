package com.rikkei.bank.aspect;

import com.rikkei.bank.dto.transaction.TransferRequest;
import com.rikkei.bank.entity.AuditLog;
import com.rikkei.bank.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(
            "@annotation(com.rikkei.bank.aspect.AuditTransfer)"
    )
    public void success(
            JoinPoint joinPoint
    ) {

        try {
            String username = getUsername();
            String ipAddress = getClientIp();
            TransferRequest request = (TransferRequest) joinPoint.getArgs()[0];

            AuditLog auditLog = AuditLog.builder()
                    .username(username)
                    .action("TRANSFER")
                    .endpoint("/api/v1/customer/transactions/transfer")
                    .ipAddress(ipAddress)
                    .amount(request.getAmount())
                    .status("SUCCESS")
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            log.info(
                    "[AUDIT] Transfer success by user: {}, Amount: {}",
                    username,
                    request.getAmount()
            );
        } catch (Exception e) {
            log.error("[AUDIT] Error saving audit log: {}", e.getMessage());
        }
    }

    @AfterThrowing(
            value = "@annotation(com.rikkei.bank.aspect.AuditTransfer)",
            throwing = "ex"
    )
    public void fail(
            JoinPoint joinPoint,
            Exception ex
    ) {

        try {
            String username = getUsername();
            String ipAddress = getClientIp();
            TransferRequest request = (TransferRequest) joinPoint.getArgs()[0];

            AuditLog auditLog = AuditLog.builder()
                    .username(username)
                    .action("TRANSFER")
                    .endpoint("/api/v1/customer/transactions/transfer")
                    .ipAddress(ipAddress)
                    .amount(request.getAmount())
                    .status("FAILED - " + ex.getMessage())
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);

            log.error(
                    "[AUDIT] Transfer failed by user: {}, Error: {}",
                    username,
                    ex.getMessage()
            );
        } catch (Exception e) {
            log.error("[AUDIT] Error saving failed audit log: {}", e.getMessage());
        }
    }

    private String getUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                ? authentication.getName()
                : "UNKNOWN";
    }

    private String getClientIp() {
        try {
            ServletRequestAttributes requestAttributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = requestAttributes.getRequest();
                String ip = request.getHeader("X-Forwarded-For");
                if (ip == null || ip.isEmpty()) {
                    ip = request.getRemoteAddr();
                }
                return ip;
            }
        } catch (Exception e) {
            log.warn("Could not get client IP: {}", e.getMessage());
        }
        return "UNKNOWN";
    }
}