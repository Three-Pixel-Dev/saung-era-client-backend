package org.threepixeldev.saungeraclient.features.category.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraclient.features.category.dto.CategoryResponse;
import org.threepixeldev.saungeraclient.features.category.service.CategoryService;
import org.threepixeldev.saungeraclient.shared.dto.PagedResponse;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Categories", description = "Endpoints for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    private static final Set<String> ALLOWED_STATUSES = Set.of("ACTIVE", "INACTIVE", "ALL");

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(
            summary = "Get all categories",
            description = "Retrieve a paginated list of categories with optional keyword search and status filtering."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved category list",
                    content = @Content(schema = @Schema(implementation = PagedResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<?> getAllCategories(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "10") int size) {
        String normalizedStatus = status.toUpperCase();
        if (!ALLOWED_STATUSES.contains(normalizedStatus)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid status. Allowed values are: " + ALLOWED_STATUSES));
        }
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<CategoryResponse> categories = categoryService.getAllCategories(keyword, status, pageable);
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "Get category by ID",
            description = "Retrieve detailed information about a specific category by its unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved category",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Category not found"
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(
            @Parameter(description = "ID of the category to retrieve", example = "1", required = true)
            @PathVariable Long id) {
        CategoryResponse category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }
}