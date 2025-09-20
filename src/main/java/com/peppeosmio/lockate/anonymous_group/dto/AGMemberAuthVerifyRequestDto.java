package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.common.dto.EncryptedStringDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AGMemberAuthVerifyRequestDto(@NotNull EncryptedStringDto encryptedUserName,
                                           @NotBlank String srpSessionId,
                                           @NotBlank String M1) {
}
