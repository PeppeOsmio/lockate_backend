package com.peppeosmio.lockate.anonymous_group.service.result;

import com.peppeosmio.lockate.anonymous_group.entity.AGAdminTokenEntity;
import com.peppeosmio.lockate.anonymous_group.entity.AnonymousGroupEntity;
import com.peppeosmio.lockate.anonymous_group.security.AGAdminAuthentication;

public record AGAdminAuthResult(AnonymousGroupEntity anonymousGroupEntity,
                                AGAdminTokenEntity agAdminTokenEntity,
                                AGAdminAuthentication authentication) {
}
