package com.naidu.ecommerce.controller;

import com.naidu.ecommerce.entity.Order;
import com.naidu.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/place/{customerId}")
    public ResponseEntity<Order> placeOrder(@PathVariable String customerId) {
        Order order = orderService.placeOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getOrdersByCustomer(customerId));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @PathVariable Order.OrderStatus status) {
        return ResponseEntity.ok(orderService.updateStatus(id, status));
    }
}
