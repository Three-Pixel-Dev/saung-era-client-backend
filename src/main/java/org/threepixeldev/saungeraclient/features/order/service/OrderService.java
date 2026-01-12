package org.threepixeldev.saungeraclient.features.order.service;


import org.threepixeldev.saungeraclient.features.auth.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {
    List<OrderResponse> searchMyOrders(String status);
}