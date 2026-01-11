package org.threepixeldev.saungeraclient.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Schema(description = "Base DTO containing common audit fields for all entities")
public class MasterData {
    @Schema(description = "Timestamp when the entity was created", example = "2025-12-14T15:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Timestamp when the entity was last updated", example = "2025-12-14T15:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "Timestamp when the entity was deleted (soft delete)", example = "2025-12-14T16:00:00")
    private LocalDateTime deletedAt;

    @Schema(description = "User who created the entity")
    private UserResponse createdBy;

    @Schema(description = "User who last updated the entity")
    private UserResponse updatedBy;

    @Schema(description = "User who deleted the entity")
    private UserResponse deletedBy;
}

