package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryJpaRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);

    @Query("SELECT c FROM Category c WHERE c.deletedAt IS NULL")
    List<Category> findAllNotDeleted();

    @Query("""
        SELECT c FROM Category c
        WHERE
          (
               :keyword IS NULL
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND (
               (:status = 'ALL')
            OR (:status = 'ACTIVE' AND c.deletedAt IS NULL)
            OR (:status = 'INACTIVE' AND c.deletedAt IS NOT NULL)
          )
    """)
    Page<Category> findAllFilteredWithStatus(
            @Param("keyword") String keyword,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("""
        SELECT c FROM Category c
        WHERE c.deletedAt IS NULL
          AND (
               :keyword IS NULL
            OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
    """)
    Page<Category> findAllFilteredNotDeleted(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.deletedAt IS NULL")
    Optional<Category> findByIdNotDeleted(Long id);

    @Query("SELECT c FROM Category c WHERE c.id = :id AND c.deletedAt IS NOT NULL")
    Optional<Category> findByIdDeleted(Long id);

    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.deletedAt IS NULL")
    Optional<Category> findByNameNotDeleted(String name);

    List<Category> findByParentId(Long parentId);
}
