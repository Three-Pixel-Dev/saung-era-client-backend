package org.threepixeldev.saungeraclient.features.updateemail.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmailResponse {
    private String message;
    private String status;
}