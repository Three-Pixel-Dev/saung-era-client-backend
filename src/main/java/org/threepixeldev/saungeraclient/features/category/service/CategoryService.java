package org.threepixeldev.saungeraclient.features.category.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.threepixeldev.saungeraclient.features.category.dto.CategoryResponse;
import org.threepixeldev.saungeraclient.features.category.mapper.CategoryMapper;
import org.threepixeldev.saungeraclient.shared.data.model.Category;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.CategoryJpaRepository;
import org.threepixeldev.saungeraclient.shared.dto.PagedResponse;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private static final String CACHE_NAME = "categories";
    private static final String CACHE_PAGINATION_KEY = "#keyword + '_' + #status + '_' + #pageable.pageNumber + '_' + #pageable.pageSize";
    private static final String CACHE_KEY_BY_ID = "'category:' + #id";

    private final CategoryJpaRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryService(CategoryJpaRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Cacheable(value = CACHE_NAME, key = CACHE_PAGINATION_KEY, unless = "#result.content.isEmpty()")
    @Transactional(readOnly = true)
    public PagedResponse<CategoryResponse> getAllCategories(String keyword, String status, Pageable pageable) {
        Page<Category> categoryPage = categoryRepository.findAllFilteredWithStatus(keyword, status, pageable);

        List<CategoryResponse> content = categoryPage.getContent().stream()
                .map(categoryMapper::toResponse)
                .toList();

        return PagedResponse.<CategoryResponse>builder()
                .content(content)
                .totalElements(categoryPage.getTotalElements())
                .totalPages(categoryPage.getTotalPages())
                .pageNumber(categoryPage.getNumber())
                .pageSize(categoryPage.getSize())
                .build();
    }

    @Cacheable(value = CACHE_NAME, key = CACHE_KEY_BY_ID, unless = "#result == null")
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findByIdNotDeleted(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return categoryMapper.toResponse(category);
    }

}
