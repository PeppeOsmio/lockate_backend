package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AnonymousGroupEntity;
import com.peppeosmio.lockate.common.dto.EncryptedStringDto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

public record AnonymousGroupDto(UUID id, EncryptedStringDto encryptedName,
                                LocalDateTime createdAt) {
    public static AnonymousGroupDto fromEntity(
            AnonymousGroupEntity entity) {
        Base64.Encoder encoder = Base64.getEncoder();
        return new AnonymousGroupDto(entity.getId(),
                new EncryptedStringDto(
                        encoder.encodeToString(entity.getNameCipher()),
                        encoder.encodeToString(entity.getNameIv()),
                        encoder.encodeToString(entity.getNameAuthTag()),
                        encoder.encodeToString(entity.getNameSalt())),
                entity.getCreatedAt());
    }
}


