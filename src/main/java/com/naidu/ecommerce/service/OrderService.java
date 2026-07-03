package com.naidu.ecommerce.service;

import com.naidu.ecommerce.entity.CartItem;
import com.naidu.ecommerce.entity.Order;
import com.naidu.ecommerce.entity.OrderItem;
import com.naidu.ecommerce.entity.Product;
import com.naidu.ecommerce.exception.BadRequestException;
import com.naidu.ecommerce.exception.ResourceNotFoundException;
import com.naidu.ecommerce.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    /**
     * Converts everything currently in a customer's cart into a placed Order.
     * This mirrors the "place order" flow described in the interview answers:
     * check stock -> create order -> reduce stock -> clear cart.
     */
    @Transactional
    public Order placeOrder(String customerId) {
        List<CartItem> cartItems = cartService.getCartByCustomer(customerId);

        if (cartItems.isEmpty()) {
            throw new BadRequestException("Cannot place an order with an empty cart");
        }

        Order order = new Order();
        order.setCustomerId(customerId);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.PLACED);

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Not enough stock for product: " + product.getName());
            }

            // Reduce stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productService.saveProduct(product);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            order.getItems().add(orderItem);

            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // Empty the cart now that the order has been placed
        cartService.clearCart(customerId);

        return savedOrder;
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public Order updateStatus(Long orderId, Order.OrderStatus status) {
        Order order = getOrderById(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
