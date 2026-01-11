package org.threepixeldev.saungeraclient.features.category.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.threepixeldev.saungeraclient.features.category.dto.CategoryResponse;
import org.threepixeldev.saungeraclient.shared.data.model.Category;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.CategoryJpaRepository;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.UserJpaRepository;

@Component
@AllArgsConstructor
public class CategoryMapper {

    private final UserJpaRepository userRepository;
    private final CategoryJpaRepository categoryRepository;

    public CategoryResponse toResponse(Category category) {
        if (category == null) {
            return null;
        }

        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setDescription(category.getDescription());

        if (category.getParentId() != null) {
            categoryRepository.findById(category.getParentId()).ifPresent(parent -> {
                CategoryResponse parentResponse = new CategoryResponse();
                parentResponse.setId(parent.getId());
                parentResponse.setName(parent.getName());
                parentResponse.setDescription(parent.getDescription());
                response.setParentCategory(parentResponse);
            });
        }

        response.setCreatedAt(category.getCreatedAt());
        response.setUpdatedAt(category.getUpdatedAt());
        response.setDeletedAt(category.getDeletedAt());

        return response;
    }
}
