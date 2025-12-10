package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Code;

import java.util.Optional;

@Repository
public interface CodeJpaRepository extends JpaRepository<Code, Long> {
    Optional<Code> findByName(String name);
}
