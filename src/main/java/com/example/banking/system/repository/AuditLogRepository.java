package com.example.banking.system.repository;

import com.example.banking.system.model.AuditLog;
import com.example.banking.system.model.enums.AuditAction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    // Filter by actor
//    Page<AuditLog> findByActorUsernameContainingIgnoreCase(String actorUsername, Pageable pageable);
//
//    // Filter by action
//    Page<AuditLog> findByAction(String action, Pageable pageable);
//
//    // Filter by date range
//    Page<AuditLog> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // Combined search — actor + action + date range
    @Query("""
        SELECT a FROM AuditLog a
        WHERE (:actor IS NULL OR LOWER(a.actorUsername) LIKE LOWER(CONCAT('%', :actor, '%')))
        AND (:action IS NULL OR a.action = :action)
        AND (:from IS NULL OR a.createdAt >= :from)
        AND (:to IS NULL OR a.createdAt <= :to)
        ORDER BY a.createdAt DESC
    """)
    Page<AuditLog> search(
            @Param("actor") String actor,
            @Param("action") AuditAction action,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );
}