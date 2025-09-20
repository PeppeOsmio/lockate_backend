package com.peppeosmio.lockate.anonymous_group.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ag_admin_token", indexes = {
        @Index(name = "idx_ag_admin_token_ag_id", columnList = "anonymous_group_id")})
@NoArgsConstructor
@Getter
@Setter
public class AGAdminTokenEntity {
    public AGAdminTokenEntity(LocalDateTime createdAt, LocalDateTime expiresAt,
                              AnonymousGroupEntity anonymousGroupEntity) {
        var secureRandom = new SecureRandom();
        var token = new byte[32];
        secureRandom.nextBytes(token);
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.anonymousGroupEntity = anonymousGroupEntity;
        this.anonymousGroupId = anonymousGroupEntity.getId();
    }

    @Id
    @Column(name = "token")
    byte[] token;

    @Column(name = "created_at", nullable = false)
    LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    // Anonymous group
    @Column(name = "anonymous_group_id", nullable = false, insertable = false,
            updatable = false)
    private UUID anonymousGroupId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "anonymous_group_id", nullable = false)
    AnonymousGroupEntity anonymousGroupEntity;

}
