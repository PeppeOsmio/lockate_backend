package com.peppeosmio.lockate.anonymous_group.mapper;

import com.peppeosmio.lockate.anonymous_group.dto.AGMemberDto;
import com.peppeosmio.lockate.anonymous_group.dto.LocationRecordDto;
import com.peppeosmio.lockate.anonymous_group.entity.AGMemberEntity;
import com.peppeosmio.lockate.common.dto.EncryptedDataDto;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
public class AGMemberMapper {
    public AGMemberDto toDto(AGMemberEntity entity, @Nullable LocationRecordDto locationRecordDto) {
        var encoder = Base64.getEncoder();
        return new AGMemberDto(
                entity.getId(),
                new EncryptedDataDto(
                        encoder.encodeToString(entity.getNameCipher()),
                        encoder.encodeToString(entity.getNameIv())),
                entity.getCreatedAt(),
                Optional.ofNullable(locationRecordDto));
    }
}
