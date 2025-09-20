package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.common.dto.EncryptedStringDto;

public record AGLocationSaveRequestDto(
        EncryptedStringDto encryptedLocation) {
}
