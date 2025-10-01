package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberLocationEntity;
import java.util.UUID;

public record AGLocationUpdateDto(LocationRecordDto location, UUID agMemberId) {
    public static AGLocationUpdateDto fromEntity(AGMemberLocationEntity entity) {
        return new AGLocationUpdateDto(LocationRecordDto.fromEntity(entity),
                entity.getAgMemberId());
    }
}
