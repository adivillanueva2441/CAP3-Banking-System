package com.example.banking.system.service.impl;

import com.example.banking.system.model.AuditLog;
import com.example.banking.system.model.enums.AuditAction;
import com.example.banking.system.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService {

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void log(String actorUsername,
                    AuditAction action,
                    String targetEntity,
                    Long targetId,
                    String details,
                    String status) {

        AuditLog log = new AuditLog();
        log.setActorUsername(actorUsername);
        log.setAction(action);
        log.setTargetEntity(targetEntity);
        log.setTargetId(targetId);
        log.setDetails(details);
        log.setStatus(status);
        auditLogRepository.save(log);
    }

    // Convenience method for SUCCESS logs
    public void logSuccess(String actorUsername,
                           AuditAction action,
                           String targetEntity,
                           Long targetId,
                           String details) {
        log(actorUsername, action, targetEntity, targetId, details, "SUCCESS");
    }

    // Convenience method for FAILED logs
    public void logFailed(String actorUsername,
                          AuditAction action,
                          String targetEntity,
                          Long targetId,
                          String details) {
        log(actorUsername, action, targetEntity, targetId, details, "FAILED");
    }
}