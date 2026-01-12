package org.threepixeldev.saungeraclient.features.requestrefund.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.request.RefundRequest;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.response.RefundOrderResponse;
import org.threepixeldev.saungeraclient.features.requestrefund.dto.response.RefundResponse;
import org.threepixeldev.saungeraclient.features.requestrefund.service.RequestRefundService;

import java.util.List;

@RestController
@RequestMapping("/api/client/protected")
@RequiredArgsConstructor
@Tag(name = "Order Refunds", description = "Endpoints for managing order refunds")
public class RequestRefundController {

    private final RequestRefundService requestRefundService;

    @PostMapping("/orders/{orderId}/refund")
    @Operation(summary = "Request order refund")
    public ResponseEntity<RefundResponse> requestRefund(
            @PathVariable Long orderId,
            @RequestBody @Valid RefundRequest request) {
        return ResponseEntity.ok(requestRefundService.requestRefund(orderId, request));
    }

    @GetMapping("/my-refunds")
    @Operation(summary = "Get my refunds")
    public ResponseEntity<List<RefundOrderResponse>> getMyRefunds() {
        return ResponseEntity.ok(requestRefundService.getMyRefunds());
    }
}