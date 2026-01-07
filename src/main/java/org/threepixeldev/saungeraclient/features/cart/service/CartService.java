package org.threepixeldev.saungeraclient.features.cart.service;

import org.threepixeldev.saungeraclient.features.cart.dto.request.CartItemRequest;
import org.threepixeldev.saungeraclient.features.cart.dto.response.CartItemResponse;

public interface CartService {
    CartItemResponse addToCart(CartItemRequest request);
}
