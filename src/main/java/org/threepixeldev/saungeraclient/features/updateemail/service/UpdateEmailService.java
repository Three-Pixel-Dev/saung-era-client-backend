package org.threepixeldev.saungeraclient.features.updateemail.service;

import org.threepixeldev.saungeraclient.features.updateemail.dto.request.UpdateEmailRequest;
import org.threepixeldev.saungeraclient.features.updateemail.dto.response.UpdateEmailResponse;

public interface UpdateEmailService {
    UpdateEmailResponse updateEmail(UpdateEmailRequest request);
}