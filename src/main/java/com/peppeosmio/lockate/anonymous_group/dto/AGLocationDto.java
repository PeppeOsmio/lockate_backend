package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberLocationEntity;
import com.peppeosmio.lockate.common.classes.EncryptedString;
import com.peppeosmio.lockate.common.dto.EncryptedStringDto;

import java.time.LocalDateTime;

public record AGLocationDto(EncryptedStringDto encryptedCoordinates,
                            LocalDateTime timestamp) {
    public static AGLocationDto fromEntity(AGMemberLocationEntity entity) {
        return new AGLocationDto(EncryptedStringDto.fromEncryptedString(
                new EncryptedString(entity.getCoordinatesCipher(),
                        entity.getCoordinatesIv(), entity.getCoordinatesAuthTag(),
                        entity.getCoordinatesSalt())), entity.getTimestamp());
    }
}
