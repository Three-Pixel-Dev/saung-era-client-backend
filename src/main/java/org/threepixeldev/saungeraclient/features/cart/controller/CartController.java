package org.threepixeldev.saungeraclient.features.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.threepixeldev.saungeraclient.features.cart.dto.request.CartItemRequest;
import org.threepixeldev.saungeraclient.features.cart.dto.response.CartItemResponse;
import org.threepixeldev.saungeraclient.features.cart.service.CartService;

@RestController
@RequestMapping("/api/client/protected/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/save")
    public ResponseEntity<CartItemResponse> saveItemToCart(@RequestBody @Valid CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(request));
    }
}
