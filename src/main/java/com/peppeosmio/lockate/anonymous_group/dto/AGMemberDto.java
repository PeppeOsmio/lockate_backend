package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberEntity;
import com.peppeosmio.lockate.common.dto.EncryptedStringDto;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public record AGMemberDto(UUID id, EncryptedStringDto encryptedName,
                          LocalDateTime createdAt,
                          Optional<AGLocationDto> encryptedLastLocation) {
    public static AGMemberDto fromEntity(AGMemberEntity entity, @Nullable
    EncryptedStringDto encryptedLastLocation) {
        var encoder = Base64.getEncoder();
        Optional<AGLocationDto> agLocation = Optional.empty();
        if(encryptedLastLocation != null) {
            agLocation = Optional.of(new AGLocationDto(encryptedLastLocation, entity.getLastSeen()));
        }
        return new AGMemberDto(entity.getId(),
                new EncryptedStringDto(encoder.encodeToString(entity.getNameCipher()),
                        encoder.encodeToString(entity.getNameIv()),
                        encoder.encodeToString(entity.getNameAuthTag()),
                        encoder.encodeToString(entity.getNameSalt())),
                entity.getCreatedAt(), agLocation);
    }
}
