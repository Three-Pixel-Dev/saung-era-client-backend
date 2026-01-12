package org.threepixeldev.saungeraclient.features.requestrefund.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.request.RefundRequest;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.response.RefundOrderResponse;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.response.RefundResponse;
import org.threepixeldev.saungeraclient.features.requestrefund.service.RequestRefundService;
import org.threepixeldev.saungeraclient.shared.data.model.Order;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.OrderJpaRepository;
import org.threepixeldev.saungeraclient.shared.utls.SecurityUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestRefundServiceImpl implements RequestRefundService {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    @Transactional
    public RefundResponse requestRefund(Long orderId, RefundRequest request) {
        Long userId = SecurityUtils.getUserId();
        Order order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        if (!order.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Order does not belong to user");
        }

        if ("REFUNDED".equals(order.getStatus()) || "REFUND_REQUESTED".equals(order.getStatus())) {
            throw new IllegalArgumentException("Refund already requested or processed");
        }

        order.setStatus("REFUND_REQUESTED");
        // Note: Ideally, save the 'reason' in a separate Refund entity or audit log
        orderJpaRepository.save(order);

        return RefundResponse.builder()
                .message("Refund requested successfully")
                .status("PENDING_APPROVAL")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RefundOrderResponse> getMyRefunds() {
        Long userId = SecurityUtils.getUserId();

        List<Order> requested = orderJpaRepository.findByUserIdAndStatus(userId, "REFUND_REQUESTED");
        List<Order> refunded = orderJpaRepository.findByUserIdAndStatus(userId, "REFUNDED");

        requested.addAll(refunded);

        return requested.stream().map(this::mapToOrderResponse).collect(Collectors.toList());
    }

    private RefundOrderResponse mapToOrderResponse(Order order) {
        return RefundOrderResponse.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .orderedDate(order.getOrderedDate())
                .status(order.getStatus())
                .promotionCode(order.getPromotionCode())
                .itemCount(order.getOrderItems() != null ? order.getOrderItems().size() : 0)
                .build();
    }
}