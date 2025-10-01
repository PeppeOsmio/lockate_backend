package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AnonymousGroupEntity;
import com.peppeosmio.lockate.common.dto.EncryptedDataDto;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

public record AnonymousGroupDto(
        UUID id, EncryptedDataDto encryptedName, LocalDateTime createdAt, String keySalt) {
    public static AnonymousGroupDto fromEntity(AnonymousGroupEntity entity) {
        Base64.Encoder encoder = Base64.getEncoder();
        return new AnonymousGroupDto(
                entity.getId(),
                new EncryptedDataDto(
                        encoder.encodeToString(entity.getNameCipher()),
                        encoder.encodeToString(entity.getNameIv())),
                entity.getCreatedAt(),
                encoder.encodeToString(entity.getKeySalt()));
    }
}
