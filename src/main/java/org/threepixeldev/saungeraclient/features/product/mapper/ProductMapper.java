package org.threepixeldev.saungeraclient.features.product.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.threepixeldev.saungeraadmin.features.category.mapper.CategoryMapper;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductCodeValueResponse;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductListResponse;
import org.threepixeldev.saungeraadmin.features.product.dto.ProductResponse;
import org.threepixeldev.saungeraadmin.shared.data.model.Product;
import org.threepixeldev.saungeraadmin.shared.data.model.ProductCodeValue;
import org.threepixeldev.saungeraadmin.shared.data.model.User;
import org.threepixeldev.saungeraadmin.shared.data.repository.jpa.UserJpaRepository;
import org.threepixeldev.saungeraadmin.shared.mapper.UserMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final UserJpaRepository userRepository;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setDiscountType(product.getDiscountType());
        response.setDiscountAmount(product.getDiscountAmount());
        response.setShortDescription(product.getShortDescription());
        response.setLongDescription(product.getLongDescription());
        response.setWeight(product.getWeight());
        response.setCountryId(product.getCountryId());

        response.setStatus(product.getStatus());
        response.setIsTaxable(product.getIsTaxable());
        response.setAllowBackorder(product.getAllowBackorder());
        response.setTags(product.getTags());

        // Map categories via ProductCategory entity
        if (!CollectionUtils.isEmpty(product.getProductCategories())) {
            response.setCategories(product.getProductCategories().stream()
                    .map(pc -> categoryMapper.toResponse(pc.getCategory()))
                    .collect(Collectors.toList()));
        } else {
            response.setCategories(Collections.emptyList());
        }
        if (product.getProductCodeValues() != null) {
            response.setProductCodeValues(product.getProductCodeValues().stream()
                    .map(this::toProductCodeValueResponse)
                    .collect(Collectors.toList()));
        } else {
            response.setProductCodeValues(Collections.emptyList());
        }

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setDeletedAt(product.getDeletedAt());

        if (product.getCreatedBy() != null) {
            User createdByUser = userRepository.findById(product.getCreatedBy()).orElse(null);
            response.setCreatedBy(userMapper.toUserResponse(createdByUser));
        }
        if (product.getUpdatedBy() != null) {
            User updatedByUser = userRepository.findById(product.getUpdatedBy()).orElse(null);
            response.setUpdatedBy(userMapper.toUserResponse(updatedByUser));
        }
        if (product.getDeletedBy() != null) {
            User deletedByUser = userRepository.findById(product.getDeletedBy()).orElse(null);
            response.setDeletedBy(userMapper.toUserResponse(deletedByUser));
        }

        return response;
    }

    public ProductResponse toResponse(Product product, List<ProductCodeValue> productCodeValues) {
        ProductResponse response = toResponse(product);
        if (response != null && !CollectionUtils.isEmpty(productCodeValues)) {
            response.setProductCodeValues(productCodeValues.stream()
                    .map(this::toProductCodeValueResponse)
                    .collect(Collectors.toList()));
        } else if (response != null) {
            response.setProductCodeValues(Collections.emptyList());
        }
        return response;
    }

    public ProductCodeValueResponse toProductCodeValueResponse(ProductCodeValue productCodeValue) {
        if (productCodeValue == null) {
            return null;
        }

        ProductCodeValueResponse response = new ProductCodeValueResponse();
        response.setId(productCodeValue.getId());
        response.setColorId(productCodeValue.getColorId());
        response.setSizeId(productCodeValue.getSizeId());
        response.setPrice(productCodeValue.getPrice());
        response.setQuantity(productCodeValue.getQuantity());
        response.setSku(productCodeValue.getSku());
        return response;
    }

    public ProductListResponse toListResponse(Product product, List<ProductCodeValue> productCodeValues) {
        if (product == null) {
            return null;
        }

        ProductListResponse response = new ProductListResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setDiscountType(product.getDiscountType());
        response.setDiscountAmount(product.getDiscountAmount());
        response.setShortDescription(product.getShortDescription());
        response.setLongDescription(product.getLongDescription());
        response.setWeight(product.getWeight());
        response.setCountryId(product.getCountryId());

        response.setStatus(product.getStatus());
        response.setIsTaxable(product.getIsTaxable());
        response.setAllowBackorder(product.getAllowBackorder());
        response.setTags(product.getTags());

        if (!CollectionUtils.isEmpty(product.getProductCategories())) {
            response.setCategories(product.getProductCategories().stream()
                    .map(pc -> categoryMapper.toResponse(pc.getCategory()))
                    .collect(Collectors.toList()));
        } else {
            response.setCategories(Collections.emptyList());
        }

        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        response.setDeletedAt(product.getDeletedAt());

        if (product.getCreatedBy() != null) {
            User createdByUser = userRepository.findById(product.getCreatedBy()).orElse(null);
            response.setCreatedBy(userMapper.toUserResponse(createdByUser));
        }
        if (product.getUpdatedBy() != null) {
            User updatedByUser = userRepository.findById(product.getUpdatedBy()).orElse(null);
            response.setUpdatedBy(userMapper.toUserResponse(updatedByUser));
        }
        if (product.getDeletedBy() != null) {
            User deletedByUser = userRepository.findById(product.getDeletedBy()).orElse(null);
            response.setDeletedBy(userMapper.toUserResponse(deletedByUser));
        }

        if (!CollectionUtils.isEmpty(productCodeValues)) {
            Integer totalStock = productCodeValues.stream()
                    .map(ProductCodeValue::getQuantity)
                    .reduce(0, Integer::sum);
            response.setStock(totalStock);

            BigDecimal minPrice = productCodeValues.stream()
                    .map(ProductCodeValue::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(null);
            response.setPrice(minPrice);
        } else {
            response.setStock(0);
            response.setPrice(null);
        }

        return response;
    }
}