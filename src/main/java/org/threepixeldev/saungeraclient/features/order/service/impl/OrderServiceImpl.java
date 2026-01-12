package org.threepixeldev.saungeraclient.features.order.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.threepixeldev.saungeraclient.features.auth.dto.response.OrderResponse;
import org.threepixeldev.saungeraclient.features.order.service.OrderService;
import org.threepixeldev.saungeraclient.shared.data.model.Order;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.OrderJpaRepository;
import org.threepixeldev.saungeraclient.shared.utls.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> searchMyOrders(String status) {
        Long userId = SecurityUtils.getUserId();
        List<Order> orders;

        // If status is provided and not empty, filter by it. Otherwise, get all.
        if (status != null && !status.trim().isEmpty()) {
            orders = orderJpaRepository.findByUserIdAndStatus(userId, status);
        } else {
            orders = orderJpaRepository.findByUserId(userId);
        }

        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .orderedDate(order.getOrderedDate())
                .status(order.getStatus())
                .promotionCode(order.getPromotionCode())
                .itemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0)
                .build();
    }
}