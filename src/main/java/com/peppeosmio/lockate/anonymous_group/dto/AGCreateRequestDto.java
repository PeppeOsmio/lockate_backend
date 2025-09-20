package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.common.dto.EncryptedStringDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AGCreateRequestDto(
        @NotNull EncryptedStringDto encryptedMemberName,
        @NotNull EncryptedStringDto encryptedGroupName,
        @NotBlank String memberPasswordSrpVerifier,
        @NotBlank String memberPasswordSrpSalt,
        @NotBlank String adminPassword
) {
}

