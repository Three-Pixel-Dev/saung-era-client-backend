package org.threepixeldev.saungeraclient.features.cart.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.threepixeldev.saungeraclient.features.cart.dto.request.CartItemRequest;
import org.threepixeldev.saungeraclient.features.cart.dto.response.CartItemResponse;
import org.threepixeldev.saungeraclient.features.cart.service.CartService;
import org.threepixeldev.saungeraclient.shared.data.model.CartItem;
import org.threepixeldev.saungeraclient.shared.data.model.Product;
import org.threepixeldev.saungeraclient.shared.data.model.ProductCodeValue;
import org.threepixeldev.saungeraclient.shared.data.model.User;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.CartItemJpaRepository;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.ProductJpaRepository;
import org.threepixeldev.saungeraclient.shared.data.repository.jpa.UserJpaRepository;
import org.threepixeldev.saungeraclient.shared.utls.SecurityUtils;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartItemJpaRepository cartItemRepository;
    private final ProductJpaRepository productRepository;
    private final UserJpaRepository userRepository;

    @Override
    @Transactional
    public CartItemResponse addToCart(CartItemRequest request) {
        Long userId = SecurityUtils.getUserId();

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        User user = userRepository.getReferenceById(userId);

        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, product.getId());

        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(request.quantity());
        } else {
            cartItem = new CartItem();
            cartItem.setUser(user);
            cartItem.setProduct(product);
            cartItem.setQuantity(request.quantity());
        }

        CartItem savedItem = cartItemRepository.save(cartItem);
        return mapToResponse(savedItem);
    }

    private CartItemResponse mapToResponse(CartItem item) {
        BigDecimal price = item.getProduct().getProductCodeValues().stream()
                .map(ProductCodeValue::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        BigDecimal subTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                price,
                subTotal
        );
    }
}
