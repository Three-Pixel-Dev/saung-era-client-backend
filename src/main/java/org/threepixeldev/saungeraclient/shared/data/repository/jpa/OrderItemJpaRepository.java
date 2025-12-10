package org.threepixeldev.saungeraclient.shared.data.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.threepixeldev.saungeraclient.shared.data.model.OrderItem;

import java.util.List;

@Repository
public interface OrderItemJpaRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderId(Long orderId);
    List<OrderItem> findByProductId(Long productId);
    void deleteByOrderId(Long orderId);
}
