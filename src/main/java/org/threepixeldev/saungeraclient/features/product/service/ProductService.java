package org.threepixeldev.saungeraclient.features.product.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductCodeValueRequest;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductListResponse;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductRequest;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductResponse;
import org.threepixeldev.saungeraadmin.features.product.mapper.ProductMapper;
import org.threepixeldev.saungeraadmin.shared.data.model.Category;
import org.threepixeldev.saungeraadmin.shared.data.model.Product;
import org.threepixeldev.saungeraadmin.shared.data.model.ProductCategory;
import org.threepixeldev.saungeraadmin.shared.data.model.ProductCodeValue;
import org.threepixeldev.saungeraadmin.shared.data.repository.jpa.*;
import org.threepixeldev.saungeraadmin.shared.dto.PagedResponse;

import java.util.ArrayList;
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

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public ProductResponse createProduct(ProductRequest request, Long createdBy) {

        Product product = new Product();
        updateProductFields(product, request);
        product.setCreatedBy(createdBy);
        product.setUpdatedBy(createdBy);

        Product savedProduct = productRepository.save(product);

        if (!CollectionUtils.isEmpty(request.getCategoryIds())) {
            saveProductCategories(savedProduct, request.getCategoryIds(), createdBy);
        }

        if (!CollectionUtils.isEmpty(request.getProductCodeValues())) {
            saveProductCodeValues(savedProduct, request.getProductCodeValues(), createdBy, false);
        }

        List<ProductCodeValue> productCodeValues = productCodeValueRepository.findByProductId(savedProduct.getId());
        return productMapper.toResponse(savedProduct, productCodeValues);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request, Long updatedBy) {
        Product product = productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        updateProductFields(product, request);
        product.setUpdatedBy(updatedBy);

        Product savedProduct = productRepository.save(product);

        // Update Categories
        if (request.getCategoryIds() != null) {
            productCategoryRepository.deleteByProductId(savedProduct.getId());
            productCategoryRepository.flush();
            saveProductCategories(savedProduct, request.getCategoryIds(), updatedBy);
        }

//<<<<<<< HEAD
//        // Smart Update for Product Code Values (Variants) to prevent duplicates and fix existing ones
//        if (request.getProductCodeValues() != null) {
//            handleProductCodeValuesUpdate(savedProduct, request.getProductCodeValues(), updatedBy);
//        }
//
//        entityManager.flush();
//        entityManager.refresh(savedProduct);
//
//        return productMapper.toResponse(savedProduct);
//    }
//
//    private void handleProductCodeValuesUpdate(Product product, List<ProductCodeValueRequest> requests, Long userId) {
//
//        List<ProductCodeValue> existingValues = productCodeValueRepository.findByProductId(product.getId());
//
//
//        List<ProductCodeValue> mutableExistingValues = new ArrayList<>(existingValues);
//        List<ProductCodeValue> toSave = new ArrayList<>();
//
//        for (ProductCodeValueRequest req : requests) {
//            if (req.getColorId() != null && !codeValueRepository.existsById(req.getColorId())) {
//                throw new RuntimeException("Color code value not found with id: " + req.getColorId());
//            }
//            if (req.getSizeId() != null && !codeValueRepository.existsById(req.getSizeId())) {
//                throw new RuntimeException("Size code value not found with id: " + req.getSizeId());
//            }
//
//            // Find matching existing value
//            ProductCodeValue matched = null;
//            for (ProductCodeValue ev : mutableExistingValues) {
//                if (ev.getColorId().equals(req.getColorId()) && ev.getSizeId().equals(req.getSizeId())) {
//                    matched = ev;
//                    break;
//                }
//            }
//
//            if (matched != null) {
//
//                matched.setPrice(req.getPrice());
//                matched.setQuantity(req.getQuantity());
//                matched.setUpdatedBy(userId);
//                toSave.add(matched);
//                mutableExistingValues.remove(matched);
//            } else {
//                ProductCodeValue newVal = new ProductCodeValue();
//                newVal.setProduct(product);
//                newVal.setColorId(req.getColorId());
//                newVal.setSizeId(req.getSizeId());
//                newVal.setPrice(req.getPrice());
//                newVal.setQuantity(req.getQuantity());
//                newVal.setCreatedBy(userId);
//                newVal.setUpdatedBy(userId);
//                toSave.add(newVal);
//            }
//        }
//
//        // 2. DELETE Remaining: Any items left in mutableExistingValues are either removed by user OR are duplicates
//        if (!mutableExistingValues.isEmpty()) {
//            productCodeValueRepository.deleteAll(mutableExistingValues);
//        }
//
//        // 3. Save updates and inserts
//        List<ProductCodeValue> savedValues = productCodeValueRepository.saveAll(toSave);
//
//        // 4. Generate SKU if missing
//        boolean skuUpdated = false;
//        for (ProductCodeValue pcv : savedValues) {
//            if (pcv.getSku() == null || pcv.getSku().isEmpty()) {
//                pcv.setSku("C" + pcv.getId());
//                skuUpdated = true;
//            }
//        }
//        if (skuUpdated) {
//            productCodeValueRepository.saveAll(savedValues);
//        }
//=======
        if (!CollectionUtils.isEmpty(request.getProductCodeValues())) {
            productCodeValueRepository.deleteByProductId(savedProduct.getId());
            productCodeValueRepository.flush();
            saveProductCodeValues(savedProduct, request.getProductCodeValues(), updatedBy, true);
            entityManager.flush();
            entityManager.refresh(savedProduct);
        } else {
            List<ProductCodeValue> existingCodeValues = productCodeValueRepository.findByProductId(savedProduct.getId());
            if (!CollectionUtils.isEmpty(existingCodeValues)) {
                productCodeValueRepository.deleteByProductId(savedProduct.getId());
                productCodeValueRepository.flush();
            }
        }

        List<ProductCodeValue> productCodeValues = productCodeValueRepository.findByProductId(savedProduct.getId());
        return productMapper.toResponse(savedProduct, productCodeValues);
//>>>>>>> 2a65eba918453556fbcba18190e312601e9bb9f0
    }

    private void saveProductCategories(Product product, List<Long> categoryIds, Long userId) {
        List<Category> categories = categoryRepository.findAllById(categoryIds);
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Category category : categories) {
            ProductCategory pc = new ProductCategory();
            pc.setProduct(product);
            pc.setCategory(category);
            pc.setCreatedBy(userId);
            pc.setUpdatedBy(userId);
            productCategories.add(pc);
        }
        productCategoryRepository.saveAll(productCategories);
    }

    // Keep this for Create method
    private void saveProductCodeValues(Product product, List<ProductCodeValueRequest> productCodeValueRequests, Long userId, boolean isUpdate) {
        List<ProductCodeValue> productCodeValues = new ArrayList<>();
        for (ProductCodeValueRequest request : productCodeValueRequests) {
            if (request.getColorId() != null && !codeValueRepository.existsById(request.getColorId())) {
                throw new RuntimeException("Color code value not found with id: " + request.getColorId());
            }

            if (request.getSizeId() != null && !codeValueRepository.existsById(request.getSizeId())) {
                throw new RuntimeException("Size code value not found with id: " + request.getSizeId());
            }

            ProductCodeValue pcv = new ProductCodeValue();
            pcv.setProduct(product);
            pcv.setColorId(request.getColorId());
            pcv.setSizeId(request.getSizeId());
            pcv.setPrice(request.getPrice());
            pcv.setQuantity(request.getQuantity());
            pcv.setCreatedBy(userId);
            pcv.setUpdatedBy(userId);
            productCodeValues.add(pcv);
        }
        List<ProductCodeValue> savedProductCodeValues = productCodeValueRepository.saveAll(productCodeValues);

        for (ProductCodeValue pcv : savedProductCodeValues) {
            pcv.setSku("C" + pcv.getId());
        }
        List<ProductCodeValue> finalSavedValues = productCodeValueRepository.saveAll(savedProductCodeValues);

        // Validation check for SKU
        for (ProductCodeValue pcv : finalSavedValues) {
            if (pcv.getSku() == null || pcv.getSku().isEmpty()) {
                throw new RuntimeException("Failed to generate SKU");
            }
        }
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void deleteProduct(Long id, Long deletedBy) {
        Product product = productRepository.findById(id)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
        product.delete(deletedBy);
        productRepository.save(product);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public void hardDeleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @CacheEvict(value = CACHE_NAME, allEntries = true)
    public ProductResponse restoreProduct(Long id, Long restoredBy) {
        Product product = productRepository.findById(id)
                .filter(p -> p.getDeletedAt() != null)
                .orElseThrow(() -> new RuntimeException("Deleted product not found with id: " + id));
        product.restore();
        product.setUpdatedBy(restoredBy);
        Product savedProduct = productRepository.save(product);
        List<ProductCodeValue> productCodeValues = productCodeValueRepository.findByProductId(savedProduct.getId());
        return productMapper.toResponse(savedProduct, productCodeValues);
    }

    private void updateProductFields(Product product, ProductRequest request) {
        product.setName(request.getName());
        product.setDiscountType(request.getDiscountType());
        product.setDiscountAmount(request.getDiscountAmount());
        product.setShortDescription(request.getShortDescription());
        product.setLongDescription(request.getLongDescription());
        product.setWeight(request.getWeight());
        product.setCountryId(request.getCountryId() != null ? request.getCountryId() : 1L);
        product.setIsTaxable(request.getIsTaxable());
        product.setStatus(request.getStatus());
        product.setTags(request.getTags());
    }

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