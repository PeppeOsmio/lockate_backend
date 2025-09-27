package com.peppeosmio.lockate.anonymous_group.entity;

import com.peppeosmio.lockate.anonymous_group.exceptions.Base64Exception;
import com.peppeosmio.lockate.common.classes.EncryptedString;
import com.peppeosmio.lockate.common.dto.EncryptedStringDto;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Slf4j
@Entity
@Table(name = "anonymous_group")
@NoArgsConstructor
@Getter
@Setter
public class AnonymousGroupEntity {

    public AnonymousGroupEntity(
            EncryptedString encryptedName,
            byte[] memberPasswordSrpVerifier,
            byte[] memberPasswordSrpSalt,
            String adminPasswordHash
    ) {
        this.nameCipher = encryptedName.cipherText();
        this.nameIv = encryptedName.iv();
        this.nameAuthTag = encryptedName.authTag();
        this.nameSalt = encryptedName.salt();
        this.memberPasswordSrpVerifier = memberPasswordSrpVerifier;
        this.memberPasswordSrpSalt = memberPasswordSrpSalt;
        this.adminPasswordHash = adminPasswordHash;
        this.createdAt = LocalDateTime.now();
    }

    public static AnonymousGroupEntity fromBase64Fields(
            EncryptedStringDto encryptedNameDto,
            String memberPasswordSrpVerifier,
            String memberPasswordSrpSalt,
            String adminPasswordHash
    ) throws Base64Exception {
        var decoder = Base64.getDecoder();
        try {
            return new AnonymousGroupEntity(
                    encryptedNameDto.toEncryptedString(),
                    decoder.decode(memberPasswordSrpVerifier),
                    decoder.decode(memberPasswordSrpSalt),
                    adminPasswordHash
            );
        } catch (IllegalArgumentException e) {
            throw new Base64Exception();
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    // Encrypted fields
    @Column(name = "name_salt", nullable = false)
    private byte[] nameSalt;

    @Column(name = "name_cipher", nullable = false)
    private byte[] nameCipher;

    @Column(name = "name_iv", nullable = false)
    private byte[] nameIv;

    @Column(name = "name_auth_tag", nullable = false)
    private byte[] nameAuthTag;

    @Column(name = "member_password_srp_verifier", nullable = false)
    private byte[] memberPasswordSrpVerifier;

    @Column(name = "member_password_srp_salt", nullable = false)
    private byte[] memberPasswordSrpSalt;

    @Column(name = "admin_password_hash", nullable = false)
    private String adminPasswordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "anonymousGroupEntity", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("createdAt DESC, id DESC")
    private List<AGMemberEntity> agMemberEntities;

    @OneToMany(mappedBy = "anonymousGroupEntity", fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @OrderBy("createdAt DESC, token DESC")
    private List<AGAdminTokenEntity> agAdminTokenEntities;
}
