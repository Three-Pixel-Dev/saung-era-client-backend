package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Media;

import java.util.List;

@Repository
public interface MediaJpaRepository extends JpaRepository<Media, Long> {
    List<Media> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<Media> findByEntityType(String entityType);
    void deleteByEntityTypeAndEntityId(String entityType, Long entityId);
}
