package com.peppeosmio.lockate.anonymous_group.entity;

import com.peppeosmio.lockate.common.classes.EncryptedString;
import com.peppeosmio.lockate.common.dto.EncryptedStringDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "ag_member_location",
        indexes = {
                @Index(name = "idx_location_timestamp", columnList = "timestamp"),
                @Index(name = "idx_ag_location_ag_id", columnList = "anonymous_group_id"),
                @Index(name = "idx_ag_location_ag_member_id", columnList = "ag_member_id")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class AGMemberLocationEntity {

    public AGMemberLocationEntity(
            EncryptedString encryptedCoordinates,
            AnonymousGroupEntity anonymousGroupEntity,
            AGMemberEntity agMemberEntity
    ) {
        this.coordinatesCipher = encryptedCoordinates.cipherText();
        this.coordinatesIv = encryptedCoordinates.iv();
        this.coordinatesAuthTag = encryptedCoordinates.authTag();
        this.coordinatesSalt = encryptedCoordinates.salt();
        this.anonymousGroupEntity = anonymousGroupEntity;
        this.anonymousGroupId = anonymousGroupEntity.getId();
        this.agMemberEntity = agMemberEntity;
        this.agMemberId = agMemberEntity.getId();
        this.timestamp = LocalDateTime.now();
    }

    public static AGMemberLocationEntity fromBase64Fields(
            EncryptedStringDto encryptedCoordinatesDto,
            AnonymousGroupEntity anonymousGroupEntity,
            AGMemberEntity agMemberEntity
    ) {
        var encryptedCoordinates = encryptedCoordinatesDto.toEncryptedString();
        return new AGMemberLocationEntity(encryptedCoordinates, anonymousGroupEntity, agMemberEntity);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "coordinates_cipher", nullable = false)
    private byte[] coordinatesCipher;

    @Column(name = "coordinates_iv", nullable = false)
    private byte[] coordinatesIv;

    @Column(name = "coordinates_auth_tag", nullable = false)
    private byte[] coordinatesAuthTag;

    @Column(name = "coordinates_salt", nullable = false)
    private byte[] coordinatesSalt;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    // Anonymous group
    @Column(name = "anonymous_group_id", nullable = false, insertable = false, updatable = false)
    private UUID anonymousGroupId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "anonymous_group_id", nullable = false)
    private AnonymousGroupEntity anonymousGroupEntity;

    // Anonymous group member
    @Column(name = "ag_member_id", nullable = false, insertable = false, updatable = false)
    private UUID agMemberId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "ag_member_id", nullable = false)
    private AGMemberEntity agMemberEntity;
}
