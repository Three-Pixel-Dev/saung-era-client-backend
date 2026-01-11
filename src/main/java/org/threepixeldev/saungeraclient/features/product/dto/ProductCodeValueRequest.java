package org.threepixeldev.saungeraclient.features.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request DTO for product code value with price and quantity")
public class ProductCodeValueRequest {
    @Schema(description = "ID of the color code value (e.g., Blue)", example = "1")
    private Long colorId;

    @Schema(description = "ID of the size code value (e.g., S)", example = "2")
    private Long sizeId;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    @Schema(description = "Price for this product variant (code value combination)", example = "29.99", required = true)
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be positive")
    @Schema(description = "Quantity available for this product variant (code value combination)", example = "100", required = true)
    private Integer quantity;
}
