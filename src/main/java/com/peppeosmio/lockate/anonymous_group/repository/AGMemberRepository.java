package com.peppeosmio.lockate.anonymous_group.repository;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.UUID;

@Repository
public interface AGMemberRepository extends CrudRepository<AGMemberEntity, UUID> {
    int countByAnonymousGroupId(UUID anonymousGroupId);
    int countByAnonymousGroupIdIn(Collection<UUID> anonymousGroupIds);
}
