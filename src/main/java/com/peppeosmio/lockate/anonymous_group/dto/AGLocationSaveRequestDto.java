package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.common.dto.EncryptedDataDto;

public record AGLocationSaveRequestDto(
        EncryptedDataDto encryptedLocation) {
}
