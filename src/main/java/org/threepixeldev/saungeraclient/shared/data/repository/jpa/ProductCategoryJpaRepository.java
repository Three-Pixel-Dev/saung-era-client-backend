package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.ProductCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCategoryJpaRepository extends JpaRepository<ProductCategory, Long> {
    List<ProductCategory> findByProductId(Long productId);
    List<ProductCategory> findByCategoryId(Long categoryId);
    Optional<ProductCategory> findByProductIdAndCategoryId(Long productId, Long categoryId);
    void deleteByProductId(Long productId);
    void deleteByCategoryId(Long categoryId);
}
