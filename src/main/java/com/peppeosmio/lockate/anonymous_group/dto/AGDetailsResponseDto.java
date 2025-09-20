package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.common.dto.EncryptedStringDto;

import java.time.LocalDateTime;
import java.util.List;

public record AGDetailsResponseDto(String id, EncryptedStringDto encryptedName,
                                   LocalDateTime createdAt, List<AGMemberDto> members) {
}
