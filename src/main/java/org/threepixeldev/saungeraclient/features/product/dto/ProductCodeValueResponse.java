package org.threepixeldev.saungeraclient.features.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response DTO for product code value")
public class ProductCodeValueResponse {
    @Schema(description = "ID of the product code value", example = "1")
    private Long id;

    @Schema(description = "ID of the color code value", example = "1")
    private Long colorId;

    @Schema(description = "ID of the size code value", example = "2")
    private Long sizeId;

    @Schema(description = "Price for this product variant", example = "29.99")
    private BigDecimal price;

    @Schema(description = "Quantity available for this product variant", example = "100")
    private Integer quantity;

    @Schema(description = "SKU for this product variant", example = "C1")
    private String sku;
}
