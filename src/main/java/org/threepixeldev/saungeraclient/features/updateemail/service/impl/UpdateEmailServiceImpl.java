package org.threepixeldev.saungeraclient.features.updateemail.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.threepixeldev.saungeraclient.features.updateemail.dto.request.UpdateEmailRequest;
import org.threepixeldev.saungeraclient.features.updateemail.dto.response.UpdateEmailResponse;
import org.threepixeldev.saungeraclient.features.updateemail.service.UpdateEmailService;
import org.threepixeldev.saungeraclient.shared.data.model.User;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.UserJpaRepository;
import org.threepixeldev.saungeraclient.shared.utls.SecurityUtils;

@Service
@RequiredArgsConstructor
public class UpdateEmailServiceImpl implements UpdateEmailService {

    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public UpdateEmailResponse updateEmail(UpdateEmailRequest request) {
        Long userId = SecurityUtils.getUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        user.setEmail(request.email());
        userRepository.save(user);

        return UpdateEmailResponse.builder()
                .message("Email updated successfully")
                .status("SUCCESS")
                .build();
    }
}