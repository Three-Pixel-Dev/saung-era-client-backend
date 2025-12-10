package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Promotion;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionJpaRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByCode(String code);
    List<Promotion> findByCategoryId(Long categoryId);
    List<Promotion> findByProductId(Long productId);
    List<Promotion> findByStartedDateLessThanEqualAndExpiredDateGreaterThanEqual(LocalDateTime date, LocalDateTime date2);
}
