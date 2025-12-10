package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Code;
import org.threepixeldev.saungeraclient.shared.data.model.CodeValue;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeValueJpaRepository extends JpaRepository<CodeValue, Long> {
    List<CodeValue> findByCode_Id(Long codeId);
    List<CodeValue> findByCode(Code code);
    Optional<CodeValue> findByName(String name);
}
