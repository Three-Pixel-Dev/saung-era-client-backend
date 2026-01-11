package org.threepixeldev.saungeraclient.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object representing a specific value for a system code")
public class CodeValueDto {

    @Schema(description = "Unique identifier of the code value", example = "1")
    private Long id;

    @Schema(description = "ID of the parent code this value belongs to", example = "1")
    private Long codeId;

    @Schema(description = "Name/Key of the code value", example = "PENDING", maxLength = 255)
    private String name;

    @Schema(description = "Description of what this code value represents", example = "Order is pending", maxLength = 1000)
    private String description;
}