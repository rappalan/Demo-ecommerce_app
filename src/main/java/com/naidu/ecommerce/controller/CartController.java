package com.naidu.ecommerce.controller;

import com.naidu.ecommerce.dto.AddToCartRequest;
import com.naidu.ecommerce.entity.CartItem;
import com.naidu.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<CartItem> addToCart(@Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addItemToCart(request));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<List<CartItem>> getCart(@PathVariable String customerId) {
        return ResponseEntity.ok(cartService.getCartByCustomer(customerId));
    }

    @DeleteMapping("/item/{cartItemId}")
    public ResponseEntity<Void> removeItem(@PathVariable Long cartItemId) {
        cartService.removeItemFromCart(cartItemId);
        return ResponseEntity.noContent().build();
    }
}
