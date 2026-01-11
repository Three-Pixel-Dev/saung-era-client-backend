package org.threepixeldev.saungeraclient.features.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraclient.features.product.dto.ProductListResponse;
import org.threepixeldev.saungeraclient.features.product.dto.ProductResponse;
import org.threepixeldev.saungeraclient.features.product.service.ProductService;
import org.threepixeldev.saungeraclient.shared.dto.PagedResponse;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = "Get all products",
            description = "Retrieve a paginated list of products with optional filtering by keyword, status, and category."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved product list",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<PagedResponse<ProductListResponse>> getAllProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(productService.getAllProducts(keyword, status, categoryId, pageable));
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieve detailed information about a specific product by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved product",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Product not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID of the product to retrieve", example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
}