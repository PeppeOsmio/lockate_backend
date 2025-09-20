package com.peppeosmio.lockate.anonymous_group.dto;

import java.util.List;

public record AGCreateResponseDto(AnonymousGroupDto anonymousGroup,
                                  AGMemberWithTokenDto authenticatedMemberInfo) {
}
