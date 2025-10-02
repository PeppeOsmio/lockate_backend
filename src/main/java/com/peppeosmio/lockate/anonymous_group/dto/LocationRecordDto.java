package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberLocationEntity;
import com.peppeosmio.lockate.common.classes.EncryptedString;
import com.peppeosmio.lockate.common.dto.EncryptedDataDto;

import java.time.LocalDateTime;
import java.util.UUID;

public record LocationRecordDto(
        UUID id, EncryptedDataDto encryptedCoordinates, LocalDateTime timestamp) {
    public static LocationRecordDto fromEntity(AGMemberLocationEntity entity) {
        return new LocationRecordDto(
                entity.getId(),
                EncryptedDataDto.fromEncryptedString(
                        new EncryptedString(
                                entity.getCoordinatesCipher(), entity.getCoordinatesIv())),
                entity.getTimestamp());
    }
}
