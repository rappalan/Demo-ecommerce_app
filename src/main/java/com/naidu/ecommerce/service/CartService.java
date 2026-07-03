package com.naidu.ecommerce.service;

import com.naidu.ecommerce.dto.AddToCartRequest;
import com.naidu.ecommerce.entity.CartItem;
import com.naidu.ecommerce.entity.Product;
import com.naidu.ecommerce.exception.BadRequestException;
import com.naidu.ecommerce.exception.ResourceNotFoundException;
import com.naidu.ecommerce.repository.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductService productService;

    public CartItem addItemToCart(AddToCartRequest request) {
        Product product = productService.getProductById(request.getProductId());

        if (product.getStock() < request.getQuantity()) {
            throw new BadRequestException("Not enough stock available for product: " + product.getName());
        }

        // If the same product is already in the customer's cart, just increase the quantity
        List<CartItem> existingItems = cartItemRepository.findByCustomerId(request.getCustomerId());
        for (CartItem item : existingItems) {
            if (item.getProduct().getId().equals(product.getId())) {
                item.setQuantity(item.getQuantity() + request.getQuantity());
                return cartItemRepository.save(item);
            }
        }

        CartItem newItem = new CartItem();
        newItem.setCustomerId(request.getCustomerId());
        newItem.setProduct(product);
        newItem.setQuantity(request.getQuantity());
        return cartItemRepository.save(newItem);
    }

    public List<CartItem> getCartByCustomer(String customerId) {
        return cartItemRepository.findByCustomerId(customerId);
    }

    public void removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + cartItemId));
        cartItemRepository.delete(item);
    }

    public void clearCart(String customerId) {
        List<CartItem> items = cartItemRepository.findByCustomerId(customerId);
        cartItemRepository.deleteAll(items);
    }
}
