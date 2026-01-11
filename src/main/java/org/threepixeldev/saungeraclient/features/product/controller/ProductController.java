package org.threepixeldev.saungeraclient.features.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraadmin.features.product.constants.ProductSwaggerMessages;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductListResponse;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductRequest;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductResponse;
import org.threepixeldev.saungeraadmin.features.product.service.ProductService;
import org.threepixeldev.saungeraadmin.shared.dto.PagedResponse;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Tag(name = ProductSwaggerMessages.TAG_NAME, description = ProductSwaggerMessages.TAG_DESCRIPTION)
public class ProductController {

    private final ProductService productService;

    @Operation(
            summary = ProductSwaggerMessages.GET_ALL_SUMMARY,
            description = ProductSwaggerMessages.GET_ALL_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = ProductSwaggerMessages.GET_ALL_SUCCESS,
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
            summary = ProductSwaggerMessages.GET_BY_ID_SUMMARY,
            description = ProductSwaggerMessages.GET_BY_ID_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = ProductSwaggerMessages.GET_BY_ID_SUCCESS,
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = ProductSwaggerMessages.GET_BY_ID_NOT_FOUND
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = ProductSwaggerMessages.GET_BY_ID_PARAM_ID, example = "1", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @Operation(
            summary = ProductSwaggerMessages.CREATE_SUMMARY,
            description = ProductSwaggerMessages.CREATE_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = ProductSwaggerMessages.CREATE_SUCCESS,
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = ProductSwaggerMessages.CREATE_BAD_REQUEST
            )
    })
    @PostMapping
    public ResponseEntity<?> createProduct(
            @Parameter(description = ProductSwaggerMessages.CREATE_PARAM_REQUEST, required = true)
            @Valid @RequestBody ProductRequest request,
            @Parameter(description = ProductSwaggerMessages.CREATE_PARAM_USER_ID, example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long createdBy = userId != null ? userId : 1L;
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(productService.createProduct(request, createdBy));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }

    }

    @Operation(
            summary = ProductSwaggerMessages.UPDATE_SUMMARY,
            description = ProductSwaggerMessages.UPDATE_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = ProductSwaggerMessages.UPDATE_SUCCESS,
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = ProductSwaggerMessages.UPDATE_BAD_REQUEST
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = ProductSwaggerMessages.UPDATE_NOT_FOUND
            )
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = ProductSwaggerMessages.UPDATE_PARAM_ID, example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = ProductSwaggerMessages.UPDATE_PARAM_REQUEST, required = true)
            @Valid @RequestBody ProductRequest request,
            @Parameter(description = ProductSwaggerMessages.UPDATE_PARAM_USER_ID, example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long updatedBy = userId != null ? userId : 1L;
        return ResponseEntity.ok(productService.updateProduct(id, request, updatedBy));
    }

    @Operation(
            summary = ProductSwaggerMessages.DELETE_SUMMARY,
            description = ProductSwaggerMessages.DELETE_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = ProductSwaggerMessages.DELETE_SUCCESS
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = ProductSwaggerMessages.DELETE_NOT_FOUND
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = ProductSwaggerMessages.DELETE_PARAM_ID, example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = ProductSwaggerMessages.DELETE_PARAM_USER_ID, example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long deletedBy = userId != null ? userId : 1L;
        productService.deleteProduct(id, deletedBy);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = ProductSwaggerMessages.HARD_DELETE_SUMMARY,
            description = ProductSwaggerMessages.HARD_DELETE_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = ProductSwaggerMessages.HARD_DELETE_SUCCESS
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = ProductSwaggerMessages.HARD_DELETE_NOT_FOUND
            )
    })
    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> hardDeleteProduct(
            @Parameter(description = ProductSwaggerMessages.HARD_DELETE_PARAM_ID, example = "1", required = true)
            @PathVariable Long id) {
        productService.hardDeleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = ProductSwaggerMessages.RESTORE_SUMMARY,
            description = ProductSwaggerMessages.RESTORE_DESCRIPTION
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = ProductSwaggerMessages.RESTORE_SUCCESS,
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = ProductSwaggerMessages.RESTORE_BAD_REQUEST
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = ProductSwaggerMessages.RESTORE_NOT_FOUND
            )
    })
    @PostMapping("/{id}/restore")
    public ResponseEntity<ProductResponse> restoreProduct(
            @Parameter(description = ProductSwaggerMessages.RESTORE_PARAM_ID, example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = ProductSwaggerMessages.RESTORE_PARAM_USER_ID, example = "1")
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long restoredBy = userId != null ? userId : 1L;
        return ResponseEntity.ok(productService.restoreProduct(id, restoredBy));
    }
}