package com.peppeosmio.lockate.anonymous_group.entity;

import com.peppeosmio.lockate.common.classes.EncryptedString;
import com.peppeosmio.lockate.common.dto.EncryptedStringDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "ag_member",
        indexes = {
                @Index(name = "idx_ag_member_ag_id", columnList = "anonymous_group_id")
        }
)
@Getter
@Setter
@ToString
@NoArgsConstructor
public class AGMemberEntity {

    public AGMemberEntity(
            EncryptedString encryptedUserName,
            byte[] token,
            AnonymousGroupEntity anonymousGroupEntity
    ) {
        var tokenHash = BCrypt.hashpw(token, BCrypt.gensalt());
        this.nameCipher = encryptedUserName.cipherText();
        this.nameIv = encryptedUserName.iv();
        this.nameAuthTag = encryptedUserName.authTag();
        this.nameSalt = encryptedUserName.salt();
        this.tokenHash = tokenHash;
        this.createdAt = LocalDateTime.now();
        this.anonymousGroupEntity = anonymousGroupEntity;
        this.anonymousGroupId = anonymousGroupEntity.getId();
    }

    public static AGMemberEntity fromBase64Fields(
            EncryptedStringDto encryptedMemberNameDto,
            byte[] token,
            AnonymousGroupEntity anonymousGroupEntity
    ) {
        return new AGMemberEntity(
                encryptedMemberNameDto.toEncryptedString(),
                token,
                anonymousGroupEntity
        );
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "token_hash", nullable = false)
    private String tokenHash;

    // Encrypted fields
    @Column(name = "name_cipher", nullable = false)
    private byte[] nameCipher;

    @Column(name = "name_iv", nullable = false)
    private byte[] nameIv;

    @Column(name = "name_auth_tag", nullable = false)
    private byte[] nameAuthTag;

    @Column(name = "name_salt", nullable = false)
    private byte[] nameSalt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "last_seen")
    private LocalDateTime lastSeen;

    // Anonymous group
    @Column(name = "anonymous_group_id", nullable = false, insertable = false, updatable = false)
    private UUID anonymousGroupId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "anonymous_group_id", nullable = false)
    @ToString.Exclude
    private AnonymousGroupEntity anonymousGroupEntity;

    @OneToMany(mappedBy = "agMemberEntity", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @ToString.Exclude
    private List<AGMemberLocationEntity> agMemberLocationEntities;
}
