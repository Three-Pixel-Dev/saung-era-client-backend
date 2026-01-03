package org.threepixeldev.saungeraclient.features.updateemail.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.threepixeldev.saungeraclient.features.updateemail.dto.request.UpdateEmailRequest;
import org.threepixeldev.saungeraclient.features.updateemail.dto.response.UpdateEmailResponse;
import org.threepixeldev.saungeraclient.features.updateemail.service.UpdateEmailService;

@RestController
@RequestMapping("/api/client/protected/me")
@RequiredArgsConstructor
@Tag(name = "Profile Management", description = "Endpoints for managing user profile")
public class UpdateEmailController {

    private final UpdateEmailService updateEmailService;

    @PatchMapping("/update/email")
    @Operation(summary = "Change user email")
    public ResponseEntity<UpdateEmailResponse> updateEmail(@RequestBody @Valid UpdateEmailRequest request) {
        return ResponseEntity.ok(updateEmailService.updateEmail(request));
    }
}