package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.Order;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    List<Order> findByPromotionId(Long promotionId);
    List<Order> findByOrderedDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByUserIdAndOrderedDateBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);
}
