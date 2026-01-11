package org.threepixeldev.saungeraclient.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a system code")
public class CodeDto {

    @Schema(description = "Unique identifier of the code", example = "1")
    private Long id;

    @Schema(description = "Name of the code", example = "STATUS", maxLength = 255)
    private String name;

    @Schema(description = "Description of the code", example = "Status code for order management", maxLength = 1000)
    private String description;
}