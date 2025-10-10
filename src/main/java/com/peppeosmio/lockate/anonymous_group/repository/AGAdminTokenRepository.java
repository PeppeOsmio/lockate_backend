package com.peppeosmio.lockate.anonymous_group.repository;

import com.peppeosmio.lockate.anonymous_group.entity.AGAdminTokenEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AGAdminTokenRepository
        extends CrudRepository<AGAdminTokenEntity, byte[]> {
}
