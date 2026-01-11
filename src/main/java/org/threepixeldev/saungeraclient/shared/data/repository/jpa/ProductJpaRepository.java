package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByCountryId(Long countryId);
    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("""
        SELECT DISTINCT p FROM Product p
        LEFT JOIN p.productCategories pc
        WHERE p.deletedAt IS NULL
          AND (:categoryId IS NULL OR pc.category.id = :categoryId)
          AND (
               :keyword IS NULL OR :keyword = ''
            OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
          AND (
            (:status = 'Out of Stock' AND (SELECT COALESCE(SUM(v.quantity), 0) FROM ProductCodeValue v WHERE v.product = p) = 0)
            OR
            (:status = 'Low Stock' AND (SELECT COALESCE(SUM(v.quantity), 0) FROM ProductCodeValue v WHERE v.product = p) BETWEEN 1 AND 10)
            OR
            (
                (:status IS NULL OR :status = '' OR (:status != 'Out of Stock' AND :status != 'Low Stock'))
                AND 
                (:status IS NULL OR :status = '' OR :status = 'ALL' OR p.status = :status)
            )
          )
    """)
    Page<Product> searchProducts(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
