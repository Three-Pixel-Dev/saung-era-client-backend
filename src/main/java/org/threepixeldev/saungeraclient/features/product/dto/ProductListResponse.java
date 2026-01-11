package org.threepixeldev.saungeraclient.features.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.threepixeldev.saungeraclient.features.category.dto.CategoryResponse;
import org.threepixeldev.saungeraclient.shared.dto.MasterData;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Product list response with additional stock and price information")
public class ProductListResponse extends MasterData {

    @Schema(description = "Unique identifier of the product", example = "1")
    private Long id;

    @Schema(description = "Name of the product", example = "Men's T-Shirt")
    private String name;

    @Schema(description = "General description of the product", example = "Comfortable cotton t-shirt")
    private String description;

    @Schema(description = "Type of discount applied (e.g., PERCENTAGE, FIXED)", example = "PERCENTAGE")
    private String discountType;

    @Schema(description = "Amount value of the discount", example = "10.00")
    private BigDecimal discountAmount;

    @Schema(description = "Brief summary of the product", example = "Premium cotton t-shirt")
    private String shortDescription;

    @Schema(description = "Detailed description of the product features", example = "Made from 100% organic cotton, this t-shirt offers comfort and style.")
    private String longDescription;

    @Schema(description = "Weight of the product", example = "0.2")
    private BigDecimal weight;

    @Schema(description = "Identifier of the country of origin", example = "1")
    private Long countryId;

    @Schema(description = "List of categories associated with the product")
    private List<CategoryResponse> categories;

    @Schema(description = "Current publication status of the product", example = "Active")
    private String status;

    @Schema(description = "Indicates if the product is subject to tax", example = "true")
    private Boolean isTaxable;

    @Schema(description = "Indicates if the product can be ordered when out of stock", example = "false")
    private Boolean allowBackorder;

    @Schema(description = "Comma-separated tags for search and filtering", example = "Modern,Interior")
    private String tags;

    @Schema(description = "Total stock quantity from all product code values", example = "500")
    private Integer stock;

    @Schema(description = "Minimum price from all product code values", example = "29.99")
    private BigDecimal price;
}