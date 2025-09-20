package com.peppeosmio.lockate.anonymous_group.dto;

import java.util.Map;
import java.util.UUID;

public record AGGetMembersLastLocationResponseDto(Map<UUID, AGLocationDto> locations) {
}
