package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.ProductCodeValue;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductCodeValueJpaRepository extends JpaRepository<ProductCodeValue, Long> {
    List<ProductCodeValue> findByProductId(Long productId);
    List<ProductCodeValue> findByProductIdIn(List<Long> productIds);
    List<ProductCodeValue> findByColorId(Long colorId);
    List<ProductCodeValue> findBySizeId(Long sizeId);
    Optional<ProductCodeValue> findByProductIdAndColorIdAndSizeId(Long productId, Long colorId, Long sizeId);
    void deleteByProductId(Long productId);
    void deleteByColorId(Long colorId);
    void deleteBySizeId(Long sizeId);
}
