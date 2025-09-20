package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.common.dto.EncryptedStringDto;

public record AGGetMemberPasswordSrpInfoResponseDto(
        EncryptedStringDto encryptedName,
        String salt) {
}
