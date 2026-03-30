package com.example.banking.system.dto.response;

import com.example.banking.system.model.AuditLog;
import com.example.banking.system.model.enums.AuditAction;

import java.time.LocalDateTime;

public class AuditLogResponseDTO {

    private Long id;
    private String actorUsername;
    private AuditAction action;
    private String targetEntity;
    private Long targetId;
    private String details;
    private String status;
    private LocalDateTime createdAt;

    public AuditLogResponseDTO(AuditLog log) {
        this.id = log.getId();
        this.actorUsername = log.getActorUsername();
        this.action = log.getAction();
        this.targetEntity = log.getTargetEntity();
        this.targetId = log.getTargetId();
        this.details = log.getDetails();
        this.status = log.getStatus();
        this.createdAt = log.getCreatedAt();
    }

    public Long getId() { return id; }
    public String getActorUsername() { return actorUsername; }
    public AuditAction getAction() { return action; }
    public String getTargetEntity() { return targetEntity; }
    public Long getTargetId() { return targetId; }
    public String getDetails() { return details; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}