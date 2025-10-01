package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberEntity;
import com.peppeosmio.lockate.common.dto.EncryptedDataDto;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public record AGMemberDto(
        UUID id,
        EncryptedDataDto encryptedName,
        LocalDateTime createdAt,
        Optional<LocationRecordDto> encryptedLastLocationRecord) {
    public static AGMemberDto fromEntity(
            AGMemberEntity entity, @Nullable EncryptedDataDto encryptedLastLocation) {
        var encoder = Base64.getEncoder();
        Optional<LocationRecordDto> agLocation = Optional.empty();
        if (encryptedLastLocation != null) {
            agLocation =
                    Optional.of(new LocationRecordDto(encryptedLastLocation, entity.getLastSeen()));
        }
        return new AGMemberDto(
                entity.getId(),
                new EncryptedDataDto(
                        encoder.encodeToString(entity.getNameCipher()),
                        encoder.encodeToString(entity.getNameIv())),
                entity.getCreatedAt(),
                agLocation);
    }
}
