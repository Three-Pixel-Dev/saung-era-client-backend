package org.threepixeldev.saungeraclient.features.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.threepixeldev.saungeraclient.features.product.dto.ProductListResponse;
import org.threepixeldev.saungeraclient.features.product.dto.ProductResponse;
import org.threepixeldev.saungeraclient.features.product.mapper.ProductMapper;
import org.threepixeldev.saungeraclient.shared.data.model.Product;
import org.threepixeldev.saungeraclient.shared.data.model.ProductCodeValue;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.*;
import org.threepixeldev.saungeraclient.shared.dto.PagedResponse;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductService {

    private static final String CACHE_NAME = "products";
    private static final String CACHE_KEY_BY_ID = "'product:' + #id";

    private final ProductJpaRepository productRepository;
    private final CategoryJpaRepository categoryRepository;
    private final ProductCategoryJpaRepository productCategoryRepository;
    private final CodeValueJpaRepository codeValueRepository;
    private final ProductCodeValueJpaRepository productCodeValueRepository;
    private final ProductMapper productMapper;

    @PersistenceContext
    private EntityManager entityManager;

    @Cacheable(value = CACHE_NAME, key = "{#keyword, #status, #categoryId, #pageable.pageNumber, #pageable.pageSize}", unless = "#result.content.isEmpty()")
    @Transactional(readOnly = true)
    public PagedResponse<ProductListResponse> getAllProducts(String keyword, String status, Long categoryId, Pageable pageable) {
        Page<Product> productPage = productRepository.searchProducts(keyword, status, categoryId, pageable);
        List<Product> products = productPage.getContent();
        
        Map<Long, List<ProductCodeValue>> productCodeValuesMap;
        if (!CollectionUtils.isEmpty(products)) {
            List<Long> productIds = products.stream()
                    .map(Product::getId)
                    .toList();
            
            List<ProductCodeValue> allProductCodeValues = productCodeValueRepository.findByProductIdIn(productIds);
            productCodeValuesMap = allProductCodeValues.stream()
                    .collect(Collectors.groupingBy(pcv -> pcv.getProduct().getId()));
        } else {
            productCodeValuesMap = Collections.emptyMap();
        }

        List<ProductListResponse> content = products.stream()
                .map(product -> {
                    List<ProductCodeValue> productCodeValues = productCodeValuesMap.getOrDefault(product.getId(), Collections.emptyList());
                    return productMapper.toListResponse(product, productCodeValues);
                })
                .toList();

        return PagedResponse.<ProductListResponse>builder()
                .content(content)
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .pageNumber(productPage.getNumber())
                .pageSize(productPage.getSize())
                .build();
    }

    @Cacheable(value = CACHE_NAME, key = CACHE_KEY_BY_ID, unless = "#result == null")
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        List<ProductCodeValue> productCodeValues = productCodeValueRepository.findByProductId(product.getId());
        return productMapper.toResponse(product, productCodeValues);
    }
}