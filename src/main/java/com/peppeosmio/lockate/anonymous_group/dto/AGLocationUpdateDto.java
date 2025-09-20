package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberLocationEntity;
import java.util.UUID;

public record AGLocationUpdateDto(AGLocationDto location, UUID agMemberId) {
    public static AGLocationUpdateDto fromEntity(AGMemberLocationEntity entity) {
        return new AGLocationUpdateDto(AGLocationDto.fromEntity(entity),
                entity.getAgMemberId());
    }
}
