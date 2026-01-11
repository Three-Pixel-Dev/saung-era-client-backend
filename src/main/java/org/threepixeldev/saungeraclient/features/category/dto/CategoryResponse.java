package org.threepixeldev.saungeraclient.features.category.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.threepixeldev.saungeraclient.shared.dto.MasterData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Response object containing category details")
public class CategoryResponse extends MasterData {

    @Schema(description = "Unique identifier of the category", example = "1")
    private Long id;

    @Schema(description = "Name of the category", example = "Men's Clothing")
    private String name;

    @Schema(description = "Detailed description of the category", example = "This category contains all men's clothing items")
    private String description;

    @Schema(description = "Parent category details including id and name")
    private CategoryResponse parentCategory;
}