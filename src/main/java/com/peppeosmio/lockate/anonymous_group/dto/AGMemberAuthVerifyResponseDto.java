package com.peppeosmio.lockate.anonymous_group.dto;

import java.util.List;

public record AGMemberAuthVerifyResponseDto(AnonymousGroupDto anonymousGroup,
                                            AGMemberWithTokenDto authenticatedMemberInfo,
                                            List<AGMemberDto> members) {
}
