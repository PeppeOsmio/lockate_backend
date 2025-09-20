package com.peppeosmio.lockate.anonymous_group.repository;

import com.peppeosmio.lockate.anonymous_group.entity.AGMemberLocationEntity;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AGLocationRepository
        extends CrudRepository<AGMemberLocationEntity, UUID> {
    @NativeQuery(value = """
                WITH ranked_locations AS (
                    SELECT
                        l.*,
                        m.anonymous_group_id as m_anonymous_group_id,
                        m.id as m_id,
                        ROW_NUMBER() OVER (
                            PARTITION BY m.id
                            ORDER BY l.timestamp DESC
                        ) AS rn
                    FROM ag_member_location l
                    JOIN ag_member m ON l.ag_member_id = m.id
                    WHERE m.anonymous_group_id = :anonymousGroupId
                )
                SELECT *
                FROM ranked_locations
                WHERE rn = 1
            """)
    List<AGMemberLocationEntity> findLatestLocationsPerMember(
            @Param("anonymousGroupId") UUID anonymousGroupId);

}
