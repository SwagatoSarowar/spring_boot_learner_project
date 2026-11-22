package com.learning.store.services;

import com.learning.store.dtos.CheckoutRequest;
import com.learning.store.dtos.CheckoutResponse;
import com.learning.store.entities.Order;
import com.learning.store.exceptions.CartIsEmptyException;
import com.learning.store.exceptions.CartNotFoundException;
import com.learning.store.repositories.CartRepository;
import com.learning.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;

    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);

        if (cart == null)
            throw new CartNotFoundException();

        if (cart.isEmpty())
            throw new CartIsEmptyException();

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);
        cartService.clearCart(cart.getId());

        return new CheckoutResponse(order.getId());
    }
}
