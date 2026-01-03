package org.threepixeldev.saungeraclient.features.order.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraclient.features.auth.dto.response.OrderResponse;
import org.threepixeldev.saungeraclient.features.order.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/api/client/protected")
@RequiredArgsConstructor
@Tag(name = "Order History", description = "Endpoints for managing user orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/my-orders/search")
    @Operation(summary = "Search my order history (optional status filter)")
    public ResponseEntity<List<OrderResponse>> searchMyOrders(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(orderService.searchMyOrders(status));
    }
}