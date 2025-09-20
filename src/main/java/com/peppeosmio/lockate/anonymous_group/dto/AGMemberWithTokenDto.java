package com.peppeosmio.lockate.anonymous_group.dto;

import com.peppeosmio.lockate.anonymous_group.classes.AGMemberEntityWithToken;
import com.peppeosmio.lockate.anonymous_group.entity.AGMemberEntity;

import java.util.Base64;

public record AGMemberWithTokenDto(AGMemberDto member, String token) {

    public static AGMemberWithTokenDto fromEntityWithToken(
            AGMemberEntityWithToken entityWithToken) {
        var encoder = Base64.getEncoder();
        return new AGMemberWithTokenDto(
                AGMemberDto.fromEntity(entityWithToken.agMemberEntity(), null),
                encoder.encodeToString(entityWithToken.token()));
    }
}
