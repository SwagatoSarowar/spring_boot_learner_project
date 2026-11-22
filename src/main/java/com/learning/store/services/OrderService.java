package com.learning.store.services;

import com.learning.store.dtos.OrderDto;
import com.learning.store.exceptions.OrderNotFoundException;
import com.learning.store.mappers.OrderMapper;
import com.learning.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.getOrdersByCustomer(user);

        return orders.stream().map(orderMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId) {
        // cleaner way to throw exception without checking with an if block
        var order = orderRepository
                .getOrderWithItems(orderId)
                .orElseThrow(OrderNotFoundException::new);

        var user = authService.getCurrentUser();

        if (!order.isPlacedBy(user))
            throw new AccessDeniedException("You don't have access to this order");

        return orderMapper.toDto(order);
    }
}
