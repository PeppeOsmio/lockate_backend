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
            AGMemberEntity entity, @Nullable LocationRecordDto locationRecordDto) {
        var encoder = Base64.getEncoder();
        Optional<LocationRecordDto> agLocation = Optional.empty();
        if (locationRecordDto != null) {
            agLocation =
                    Optional.of(locationRecordDto);
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
