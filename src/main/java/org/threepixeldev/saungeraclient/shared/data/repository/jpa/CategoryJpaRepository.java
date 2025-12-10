package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Category;

import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
}
