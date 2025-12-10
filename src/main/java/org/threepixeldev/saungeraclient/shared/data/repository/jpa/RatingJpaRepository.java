package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingJpaRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByUserId(Long userId);
    List<Rating> findByProductId(Long productId);
    Optional<Rating> findByUserIdAndProductId(Long userId, Long productId);
    void deleteByUserId(Long userId);
    void deleteByProductId(Long productId);
}
