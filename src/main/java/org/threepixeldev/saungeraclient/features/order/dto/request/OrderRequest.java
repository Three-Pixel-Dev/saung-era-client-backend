package org.threepixeldev.saungeraclient.features.order.dto.request;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

// This is a record for creating a new order (Checkout)
public record OrderRequest(
        @NotEmpty(message = "Cart items or Product IDs are required")
        List<Long> productIds,

        String promotionCode,

        String paymentMethod
) {}